package fr.coppernic.samples.core.ui

import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import fr.coppernic.samples.core.R
import fr.coppernic.sdk.net.cone2.StaticIpConfig
import fr.coppernic.sdk.utils.helpers.OsHelper
import kotlinx.android.synthetic.main.fragment_net.*
import fr.coppernic.samples.core.utils.RegexTextWatcher

/**
 * A simple [Fragment] subclass.
 *
 */

class NetFragment : Fragment() {

    private val presenter = NetPresenter2()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_net, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etdIp.addTextChangedListener(RegexTextWatcher(regex =  android.util.Patterns.IP_ADDRESS.toRegex(), message = "Error", layout = textInputLayout))
        edtMask.addTextChangedListener(RegexTextWatcher(regex ="[0-9]{1,16}".toRegex(), message = "Error", layout = textInputLayout2))
        edtGateway.addTextChangedListener(RegexTextWatcher(regex = android.util.Patterns.IP_ADDRESS.toRegex(), message = "Error", layout = textInputLayout3))
        edtDns1.addTextChangedListener(RegexTextWatcher(regex = android.util.Patterns.IP_ADDRESS.toRegex(), message = "Error", layout = textInputLayout4))
        edtDns2.addTextChangedListener(RegexTextWatcher(regex = android.util.Patterns.IP_ADDRESS.toRegex(), message = "Error", layout = textInputLayout5))

        if (OsHelper.isConeV2()) {
            btnIp.setOnClickListener {
                if (presenter.isValidIp(etdIp.toString())
                        || presenter.isValidIp(edtDns1.toString())
                        || presenter.isValidIp(edtGateway.toString())
                        || presenter.isValidIp(edtDns2.toString())
                        || presenter.isValidMask(edtMask.toString())) {
                    StaticIpConfig.configureStaticIp(context!!,
                            etdIp.text.toString(),
                            edtMask.text.toString().replace(".", "").toInt(),
                            edtGateway.text.toString(),
                            edtDns1.text.toString(),
                            edtDns2.text.toString())
                }
            }
        }
    }
}