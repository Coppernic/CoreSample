package fr.coppernic.samples.core.ui.hardwareTools

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import fr.coppernic.samples.core.R
import fr.coppernic.sdk.utils.core.CpcResult.RESULT
import timber.log.Timber
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [HdwrTestConeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HdwrTestConeFragment : Fragment(), HdwrFragmentBase, HdwrTestBase.HdwrTestResultInterface {

    val TAG = "HdwrTestConeFragment"

    lateinit var btnLaunchTest: Button
    lateinit var spinTestList: Spinner
    lateinit var textResult: TextView
    private val listOfTests: ArrayList<HdwrTestBase> = ArrayList<HdwrTestBase>()

    var mCurrentTest: HdwrTestBase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        populateListOfTests()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_hdwr_test_cone,
                container,
                false)
        btnLaunchTest = rootView.findViewById<View>(R.id.frag_test_btn_test1) as Button
        spinTestList = rootView.findViewById<View>(R.id.frag_test_spinner1) as Spinner
        textResult = rootView.findViewById<View>(R.id.frag_test_textResult) as TextView
        btnLaunchTest.setOnClickListener { launchTest() }
        fillListOfTests()
        return rootView
    }

    private fun populateListOfTests() {
        fragmentManager?.let {
            ExpansionPortTest(activity,
                    it, this)
        }?.let { listOfTests.add(it) }
    }

    private fun getListOfTests(): MutableList<String> {
        val list = ArrayList<String>()
        for (base in listOfTests) {
            list.add(base.tag)
        }
        return list
    }

    private fun fillListOfTests() {
        val adapter = ArrayAdapter(context,
                android.R.layout.simple_spinner_item, getListOfTests())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinTestList.adapter = adapter
    }

    private fun findTest(tag: String): HdwrTestBase? {
        for (base in listOfTests) {
            if (tag.contentEquals(base.tag)) {
                return base
            }
        }
        return null
    }

    private fun launchTest() {
        if (mCurrentTest == null) {
            mCurrentTest = findTest(spinTestList.selectedItem.toString())
            mCurrentTest?.launchTest()
            btnLaunchTest.text = getString(R.string.test_progress)
        } else {
            Timber.e("Test already in progress")
            Toast.makeText(activity, "A test is already in progress",
                    Toast.LENGTH_SHORT).show()
        }
    }

    override fun onTestResult(res: RESULT) {
        if (res == RESULT.OK) {
            textResult.text = "Test is successful"
        } else {
            textResult.text = mCurrentTest?.testErrorList
        }
        btnLaunchTest.text = getString(R.string.launch_test)
        mCurrentTest = null
    }

    override fun isHardwareSupported(): Boolean {
        return true
    }

    override fun getTitle(): String? {
        return "COne"
    }
}
