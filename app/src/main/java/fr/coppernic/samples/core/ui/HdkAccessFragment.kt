package fr.coppernic.samples.core.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
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
import fr.coppernic.sdk.utils.sound.Sound
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
    var checkHid = false


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
                    viewBinding.imgLed1.setColorFilter(
                        if (it.gpio1) Color.GREEN else Color.LTGRAY, android.graphics.PorterDuff.Mode
                            .MULTIPLY
                    )
                }
            }

            toggleGpio2.setOnClickListener {
                gpioPort?.let {
                    showErr(it.setGpio2(viewBinding.toggleGpio2.isChecked))
                    viewBinding.imgLed2.setColorFilter(
                        if (it.gpio2) Color.GREEN else Color.LTGRAY, android.graphics.PorterDuff.Mode
                            .MULTIPLY
                    )
                }
            }

            btnRefresh.setOnClickListener {
                getGpiostates(gpioPort)
            }

            btnHidConfCard.setOnClickListener {
                if (viewBinding.btnHidConfCard.isChecked) {
                    checkHid = true
                    jobLedRead = lifecycleScope.launch {
                        Timber.d("call to launch")
                        while (true) {
                            delay(40)
                            // do something every second
                            Timber.d("call to gpioPort")
                            getGpiostates(gpioPort)
                        }
                    }
                    toggleVccEn.isEnabled = false
                    toggleIoEn.isEnabled = false
                    toggleGpio1.isEnabled = false
                    toggleGpio2.isEnabled = false
                    btnRefresh.isEnabled = false

                    gpioPort?.let {
                        it.vccEn = true
                        it.setIoEn(true)
                    }
                } else {
                    checkHid = false
                    jobLedRead?.cancel()
                    toggleVccEn.isEnabled = true
                    toggleIoEn.isEnabled = true
                    toggleGpio1.isEnabled = true
                    toggleGpio2.isEnabled = true
                    btnRefresh.isEnabled = true

                    toggleVccEn.isChecked = false
                    toggleIoEn.isChecked = false

                    gpioPort?.let {
                        it.vccEn = false
                        it.setIoEn(false)
                    }
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

    var previousGpio1: Boolean? = null
    var previousGpio2: Boolean? = null

    var gpio1AlreadyAtOne: Boolean = false

    var gpio1AtOneTimeStart: Long = 0
    var nbGpio1AtZero = 0

    // On HID, the config card
    private fun checkGpio1(gpio1: Boolean) {
        // Checks if gpio has switched to one
        if (!gpio1AlreadyAtOne) {
            if (gpio1) {
                gpio1AtOneTimeStart = System.currentTimeMillis()
                // TODO : beep
                beepFunction()
            }
        }
        if (gpio1AlreadyAtOne) {
            if ((gpio1AtOneTimeStart + 400) > System.currentTimeMillis()) {
                // Wait 400ms before checking
                return
            }

            // Wait at least 10 consecutive 0
            if (!gpio1) {
                nbGpio1AtZero++
            } else {
                nbGpio1AtZero = 0
            }

            if (nbGpio1AtZero >= 5) {
                gpio1AlreadyAtOne = false
            }
        }
    }


    @SuppressLint("CheckResult")
    private fun getGpiostates(port: GpioPort?) {
        port?.let { port ->
            val gpio1 = port.readGpio1()
            val gpio2 = port.readGpio2()
            viewBinding.imgLed1.setColorFilter(if (gpio1) Color.GREEN else Color.LTGRAY, android.graphics.PorterDuff.Mode.MULTIPLY)
            viewBinding.imgLed2.setColorFilter(if (gpio2) Color.GREEN else Color.LTGRAY, android.graphics.PorterDuff.Mode.MULTIPLY)
            if (checkHid) {
                checkGpio1(gpio1)
            }
        }
    }

    companion object {
        const val TAG = "HdkAccessFragment"
    }

    private fun beepFunction() {
        val sound = context?.let { Sound(it) }
        sound?.playOk(200, this::callback)
    }

    private fun callback() {
        //do something here
        return Unit
    }


}