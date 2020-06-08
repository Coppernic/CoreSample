package fr.coppernic.samples.core.ui.hardwareTools.autoTest

import android.content.Context
import fr.coppernic.sdk.utils.core.CpcResult.RESULT
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber
import java.util.*

abstract class HdwrTestBase(ctx: Context?, notifier: HdwrTestResultInterface?) {

    interface HdwrTestResultInterface {
        fun onTestResult(res: RESULT)
    }

    class TestFailException(errString: String?) : Exception(errString) {
        companion object {
            /**
             *
             */
            private const val serialVersionUID = -5529596337348856482L
        }
    }

    protected var mCtx: Context? = null
    protected var mNotifier: HdwrTestResultInterface? = null
    protected var mLastErrorDesc: String? = null
    protected var mListOfErrors = ArrayList<String>()
    protected var mResult = RESULT.OK
    open val tag = "HdwTestBase"

    val testErrorList: String
        get() {
            val sb = StringBuilder()
            for (s in mListOfErrors) {
                sb.append(s)
                sb.append("\n")
            }
            return sb.toString()
        }

    fun launchTest() {

        setUp()?.observeOn(AndroidSchedulers.mainThread())?.subscribe({
            startTest()
            tearDown()
            mNotifier?.onTestResult(mResult)
        }, {
            Timber.d(it.message)
        }
        )


//        val mTestTask: AsyncTask<Any, Any, RESULT> = object : AsyncTask<Any, Any, RESULT>() {
//            protected override fun doInBackground(vararg arg0: Any): RESULT {
//                try {
//                    setUp()
//                    startTest()
//                    tearDown()
//                } catch (e: InterruptedException) {
//                    Log.e(tag, "InterruptedException error, test is canceled")
//                    e.printStackTrace()
//                } catch (e: Exception) {
//                    Log.e(tag, "Error : $e\nTest is canceled")
//                }
//                return mResult
//            }
//
//            override fun onPostExecute(result: RESULT) {
//                mNotifier?.onTestResult(result)
//                super.onPostExecute(result)
//            }
//        }
//        mTestTask.execute(Any())
    }

    @Throws(Exception::class)
    protected abstract fun startTest(): RESULT?
    protected open fun setUp(): Completable? {
        mListOfErrors.clear()
        mLastErrorDesc = ""
        mResult = RESULT.ERROR
        return null
    }


    protected open fun tearDown() {}

    protected fun postError(errString: String) {
        mLastErrorDesc = errString
        mListOfErrors.add(errString)
        mResult = RESULT.ERROR
    }

    @Throws(TestFailException::class)
    protected fun postErrorAndFail(errString: String) {
        postError(errString)
        throw TestFailException(errString)
    }

    init {
        mCtx = ctx
        mNotifier = notifier
    }
}
