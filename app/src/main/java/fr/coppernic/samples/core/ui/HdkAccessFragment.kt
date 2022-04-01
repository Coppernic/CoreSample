package fr.coppernic.samples.core.ui

import android.os.Bundle
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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer

/**
 * A simple [Fragment] subclass.
 */
class HdkAccessFragment : androidx.fragment.app.Fragment(),
    ViewBindingEnabled<FragmentHdkAccessBinding> by ViewBindingHolder(FragmentHdkAccessBinding::class) {

    private var gpioPort: GpioPort? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflate(inflater)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        deflate()
    }

    override fun onStart() {
        super.onStart()
        val subscribe = GpioPort.GpioManager.get()
            .getGpioSingle(context)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ g -> gpioPort = g; gpioPort?.let { updateButton(it) } }, onError)
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
        if (gpioPort != null) {
            gpioPort!!.close()
        }
        super.onStop()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(viewBinding) {
            toggleVccEn.setOnClickListener {
                if (gpioPort != null) {
                    showErr(gpioPort!!.setVccEn(toggleVccEn.isChecked))
                }
            }

            toggleIoEn.setOnClickListener {
                if (gpioPort != null) {
                    showErr(gpioPort!!.setIoEn(viewBinding.toggleIoEn.isChecked))
                }
            }

            toggleGpio1.setOnClickListener {
                if (gpioPort != null) {
                    showErr(gpioPort!!.setGpio1(viewBinding.toggleGpio1.isChecked))
                }
            }

            toggleGpio2.setOnClickListener {
                if (gpioPort != null) {
                    showErr(gpioPort!!.setGpio2(viewBinding.toggleGpio2.isChecked))
                }
            }

        }

    }


//    @OnClick(R.id.toggleVccEn)
//    fun toggleVccEn() {
//        if (gpioPort != null) {
//            showErr(gpioPort!!.setVccEn(viewBinding.toggleVccEn.isChecked))
//        }
//    }
//
//    @OnClick(R.id.toggleIoEn)
//    fun toggleIoEn() {
//        if (gpioPort != null) {
//            showErr(gpioPort!!.setIoEn(viewBinding.toggleIoEn.isChecked))
//        }
//    }
//
//    @OnClick(R.id.toggleGpio1)
//    fun toggleGpio1() {
//        if (gpioPort != null) {
//            showErr(gpioPort!!.setGpio1(viewBinding.toggleGpio1.isChecked))
//        }
//    }
//
//    @OnClick(R.id.toggleGpio2)
//    fun toggleGpio2() {
//        if (gpioPort != null) {
//            showErr(gpioPort!!.setGpio2(viewBinding.toggleGpio2.isChecked))
//        }
//    }

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


    fun showErr(res: RESULT) {
        if (res != RESULT.OK) {
            Toast.makeText(context, L.getMethodName(3) + " : " + res, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val TAG = "HdkAccessFragment"
    }
}