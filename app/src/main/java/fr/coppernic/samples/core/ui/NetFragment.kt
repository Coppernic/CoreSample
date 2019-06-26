package fr.coppernic.samples.core.ui

import android.os.Bundle
import android.support.v4.app.Fragment
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

        if (OsHelper.isConeV2()) {
            btnIp.setOnClickListener {
                if (isFieldValid()) {
                    StaticIpConfig.configureStaticIp(context!!,
                            etdIp.text.toString(),
                            edtMask.text.toString().replace(".", "").length,
                            edtGateway.text.toString(),
                            edtDns1.text.toString(),
                            edtDns2.text.toString())
                } else if (etdIp.text.toString().length != 10) {
                    etdIp.setError("Please enter valid Ip")
                } else if (edtMask.text.toString().length != 10) {
                    edtMask.setError("Please enter valid Mask")
                } else if (edtGateway.text.toString().length != 10) {
                    edtGateway.setError("Please enter valid Gateway")
                } else if (edtDns1.text.toString().length != 10) {
                    edtDns1.setError("Please enter valid Dns")
                } else if (edtDns2.text.toString().length != 10) {
                    edtDns2.setError("Please enter valid Dns")
                }
            }
        }
    }

    private fun isFieldValid(): Boolean {
        return android.util.Patterns.IP_ADDRESS.matcher(etdIp.text.toString()).matches()
    }
}

