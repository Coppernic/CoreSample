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

/**
 * A simple [Fragment] subclass.
 */
class HdkAccessFragment : androidx.fragment.app.Fragment(),
    ViewBindingEnabled<FragmentHdkAccessBinding> by ViewBindingHolder(FragmentHdkAccessBinding::class) {

    private var gpioPort: GpioPort? = null
    private var disposable: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflate(inflater)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        deflate()
    }

    override fun onStart() {
        super.onStart()
        disposable = GpioPort.GpioManager.get()
            .getGpioSingle(context)
            .observeOn(mainThread())
            .subscribe({ g ->
                gpioPort = g
                gpioPort?.let { updateButton(it) }
                getGpiostates(gpioPort)
            }, onError)
    }


    private fun updateButton(g: GpioPort) {
        with(viewBinding) {
            toggleIoEn.isChecked = g.ioEn
            toggleVccEn.isChecked = g.vccEn
            toggleGpio1.isChecked = g.gpio1
            toggleGpio2.isChecked = g.gpio2
        }
    }

    override fun onStop() {
        disposable?.dispose()
        gpioPort?.close()
        super.onStop()
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

    @SuppressLint("CheckResult")
    private fun getGpiostates(port: GpioPort?) {
        port?.let { port ->
            viewBinding.imgLed1.setColorFilter(if (port.gpio1) Color.GREEN else Color.LTGRAY, android.graphics.PorterDuff.Mode.MULTIPLY)
            viewBinding.imgLed2.setColorFilter(if (port.gpio2) Color.GREEN else Color.LTGRAY, android.graphics.PorterDuff.Mode.MULTIPLY)
        }
    }

    companion object {
        const val TAG = "HdkAccessFragment"
    }
}