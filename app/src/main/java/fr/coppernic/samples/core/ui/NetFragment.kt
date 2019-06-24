package fr.coppernic.samples.core.ui


import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fr.coppernic.samples.core.MainActivity

import fr.coppernic.samples.core.R
import fr.coppernic.sdk.net.cone2.StaticIpConfig
import fr.coppernic.sdk.utils.helpers.OsHelper
import kotlinx.android.synthetic.main.fragment_net.*

/**
 * A simple [Fragment] subclass.
 *
 */
class NetFragment : Fragment() {



    //var prefixRegex = "([0-9]{4}).([0-9]{4}).([0-9]{4}.([0-9]{4}))"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_net, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (OsHelper.isConeV2()) {
            btnIp.setOnClickListener {
                StaticIpConfig.configureStaticIp(context!!,
                        etdIp.text.toString(),
                        edtMask.text.toString().replace(".","").length,
                        edtGateway.text.toString(),
                        edtDns1.text.toString(),
                        edtDns2.text.toString())
        }
        } else (setAlertDialog())
    }

    fun setAlertDialog(){
        val builder = AlertDialog.Builder(context!!)
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        builder.setView(R.layout.abc_alert_dialog_material)
        builder.setTitle(R.string.alert_title)
        builder.setIcon(R.mipmap.android_blue)
        builder.setMessage(R.string.alert_message)
        builder.setPositiveButton("OK",DialogInterface.OnClickListener { dialog , witch ->
            startActivity(intent)
        })
        builder.show()
    }
}