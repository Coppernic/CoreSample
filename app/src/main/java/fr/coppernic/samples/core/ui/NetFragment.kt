package fr.coppernic.samples.core.ui

import android.os.Bundle
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

/**
 * A simple [Fragment] subclass.
 *
 */

class NetFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_net, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etdIp.addTextChangedListener(textWatcher)
        edtMask.addTextChangedListener(textWatcher)
        edtGateway.addTextChangedListener(textWatcher)
        edtDns1.addTextChangedListener(textWatcher)
        edtDns2.addTextChangedListener(textWatcher)

        if (OsHelper.isConeV2()) {
            btnIp.setOnClickListener {
                if (isFieldValid()) {
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

    val textWatcher = object : TextWatcher {
        override
        fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        }

        override
        fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

        }

        override
        fun afterTextChanged(editable: Editable?) {

            if (etdIp.text.toString().length != 7 || etdIp.text.toString().length != 15) {
                etdIp.setError(R.string.alert_wrong_Ip.toString())
            } else if (edtMask.text.toString().length != 7 || edtMask.text.toString().length != 15) {
                edtMask.setError(R.string.alert_wrong_Mask.toString())
            } else if (edtGateway.text.toString().length != 7 || edtGateway.text.toString().length != 15) {
                edtGateway.setError(R.string.alert_wrong_Gateway.toString())
            } else if (edtDns1.text.toString().length != 7 || edtDns1.text.toString().length != 15) {
                edtDns1.setError(R.string.alert_wrong_DNS1.toString())
            } else if (edtDns2.text.toString().length != 7 || edtDns2.text.toString().length != 15) {
                edtDns2.setError(R.string.alert_wrong_DNS2.toString())
            }
        }
    }

    private fun isFieldValid(): Boolean {

        val regexMask = Regex(pattern = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}\\/\\d+")
        val ipField = android.util.Patterns.IP_ADDRESS.matcher(etdIp.text.toString()).matches()
        val maskfield = regexMask.matches(edtMask.text.toString())
        val gatewayField = android.util.Patterns.IP_ADDRESS.matcher(edtGateway.text.toString()).matches()
        val dns1Field = android.util.Patterns.IP_ADDRESS.matcher(edtDns1.text.toString()).matches()
        val dns2Field = android.util.Patterns.IP_ADDRESS.matcher(edtDns2.text.toString()).matches()

        return ipField && maskfield && gatewayField && dns1Field && dns2Field
    }
}