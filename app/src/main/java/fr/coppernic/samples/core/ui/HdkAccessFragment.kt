package fr.coppernic.samples.core.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.afollestad.materialdialogs.MaterialDialog
import fr.coppernic.samples.core.databinding.FragmentHdkAccessBinding
import fr.coppernic.samples.core.ui.common.ViewBindingEnabled
import fr.coppernic.samples.core.ui.common.ViewBindingHolder
import fr.coppernic.sdk.hdk.access.GpioPort
import fr.coppernic.sdk.utils.core.CpcResult.RESULT
import fr.coppernic.sdk.utils.core.CpcResult.ResultException
import fr.coppernic.sdk.utils.debug.L
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */
class HdkAccessFragment : androidx.fragment.app.Fragment(),
    ViewBindingEnabled<FragmentHdkAccessBinding> by ViewBindingHolder(FragmentHdkAccessBinding::class) {

    private var gpioPort: GpioPort? = null
    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        disposable = GpioPort.GpioManager.get()
            .getGpioSingle(context)
            .observeOn(mainThread())
            .subscribe({ g ->
                gpioPort = g
                gpioPort?.let { updateButton(it) }
                getGpiostates(gpioPort)
            }, onError)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
//        gpioPort?.close()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflate(inflater)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        deflate()
    }

    var jobLedRead: Job? = null

    private fun updateButton(g: GpioPort) {
        with(viewBinding) {
            toggleIoEn.isChecked = g.ioEn
            toggleVccEn.isChecked = g.vccEn
            toggleGpio1.isChecked = g.gpio1
            toggleGpio2.isChecked = g.gpio2
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(viewBinding) {
            toggleVccEn.setOnClickListener {
                gpioPort?.let {
                    showErr(it.setVccEn(toggleVccEn.isChecked))
                }
            }

            toggleIoEn.setOnClickListener {
                gpioPort?.let {
                    showErr(it.setIoEn(viewBinding.toggleIoEn.isChecked))
                }
            }

            toggleGpio1.setOnClickListener {
                gpioPort?.let {
                    showErr(it.setGpio1(viewBinding.toggleGpio1.isChecked))
                    viewBinding.imgLed1.setColorFilter(if (it.gpio1) Color.GREEN else Color.LTGRAY, android.graphics.PorterDuff.Mode
                        .MULTIPLY)
                }
            }

            toggleGpio2.setOnClickListener {
                gpioPort?.let {
                    showErr(it.setGpio2(viewBinding.toggleGpio2.isChecked))
                    viewBinding.imgLed2.setColorFilter(if (it.gpio2) Color.GREEN else Color.LTGRAY, android.graphics.PorterDuff.Mode
                        .MULTIPLY)
                }
            }

            btnRefresh.setOnClickListener {
                getGpiostates(gpioPort)
            }

            btnHidConfCard.setOnClickListener {
                if (viewBinding.btnHidConfCard.isChecked) {
                    jobLedRead = lifecycleScope.launch {
                        Timber.d("call to launch")
                        while (true) {
                            delay(50)
                            // do something every second
                            Timber.d("call to gpioPort")
                            getGpiostates(gpioPort)
                        }
                    }
                } else {
                    jobLedRead?.cancel()
                }
            }
        }
    }

    private val onError: Consumer<Throwable> = Consumer { throwable ->
        if (throwable is ResultException) {
            if (throwable.result == RESULT.SERVICE_NOT_FOUND) {
                val dialog: MaterialDialog = MaterialDialog.Builder(requireActivity())
                    .title(fr.coppernic.samples.core.R.string.error)
                    .content(fr.coppernic.samples.core.R.string.error_service_not_found)
                    .build()
                dialog.show()
            }
        }
    }


    private fun showErr(res: RESULT) {
        if (res != RESULT.OK) {
            Toast.makeText(context, L.getMethodName(3) + " : " + res, Toast.LENGTH_SHORT).show()
        }
    }

    var previousgpio1: Boolean? = null
    var previousgpio2: Boolean? = null

    @SuppressLint("CheckResult")
    private fun getGpiostates(port: GpioPort?) {
        port?.let { port ->
            val gpio1 = port.readGpio1()
            val gpio2 = port.readGpio2()
            viewBinding.imgLed1.setColorFilter(if (gpio1) Color.GREEN else Color.LTGRAY, android.graphics.PorterDuff.Mode.MULTIPLY)
            viewBinding.imgLed2.setColorFilter(if (gpio2) Color.GREEN else Color.LTGRAY, android.graphics.PorterDuff.Mode.MULTIPLY)
        }
    }

    companion object {
        const val TAG = "HdkAccessFragment"
    }
}