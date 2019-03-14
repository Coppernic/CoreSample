package fr.coppernic.samples.core.ui


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import fr.coppernic.samples.core.R
import fr.coppernic.sdk.net.cone2.StaticIpConfig
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

        btnIp.setOnClickListener {
            StaticIpConfig.configureStaticIp(context!!,
                    etdIp.text.toString(),
                    edtMask.text.toString().toInt(),
                    edtGateway.text.toString(),
                    edtDns1.text.toString(),
                    edtDns2.text.toString())
        }
    }
}
