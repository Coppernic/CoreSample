package fr.coppernic.samples.core.ui.hardwareTools


import android.content.Context
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import io.reactivex.Completable
import timber.log.Timber
import java.util.concurrent.Semaphore


abstract class HdwrTestDialogBase(ctx: Context?, man: FragmentManager,
                                  notifier: HdwrTestResultInterface?) : HdwrTestBase(ctx, notifier), HdwrErrorAlertDialogListener {

    private var fragmentManager: FragmentManager = man
    private var currentDialogMessage: String? = null
    private val semDisplay = Semaphore(0)
    var cancelTest = false

    companion object {
        private const val TAG = "HdwrTestDialogBase"
    }

    /**
     * Show a dialog box with dialog given by implementation of getTestDialog()
     *
     */
    fun showDialog() {
        val f: DialogFragment = ErrorAlertDialogFragment.newInstance(currentDialogMessage, this)
        f.show(fragmentManager, "DialogFragment")
    }

    /**
     * Show a dialog box with the message provided in parameter.
     *
     * @param t dialog message
     */
    private fun showDialog(t: String?) {
        currentDialogMessage = t
        showDialog()
    }

    override fun setUp(): Completable? {
        super.setUp()
        cancelTest = false
        return null
    }

    override fun onPositiveClick() {
        Timber.i("onPositiveClick")
        semDisplay.release()
    }

    override fun onNegativeClick() {
        Timber.i("onNegativeClick")
        cancelTest = true
        semDisplay.release()
    }

    @Throws(Exception::class)
    protected fun waitOnError(s: String?) {
        showDialog(s)
        semDisplay.acquire()
        if (cancelTest) {
            postErrorAndFail(s!!)
        } else {
            postError(s!!)
        }
    }
}