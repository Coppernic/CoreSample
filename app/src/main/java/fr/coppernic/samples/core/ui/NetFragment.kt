package fr.coppernic.samples.core.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

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

    private val presenter = NetPresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_net, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        edtIp.addTextChangedListener(RegexTextWatcher(
                regex = android.util.Patterns.IP_ADDRESS.toRegex(),
                message = getString(R.string.alert_field),
                layout = textInputLayout))
        edtMask.addTextChangedListener(RegexTextWatcher(
                regex = ("(((255\\.){3}(255|254|252|248|240|224|192|128|0+))|" +
                        "((255\\.){2}(255|254|252|248|240|224|192|128|0+)\\.0)|" +
                        "((255\\.)(255|254|252|248|240|224|192|128|0+)(\\.0+){2})|" +
                        "((255|254|252|248|240|224|192|128|0+)(\\.0+){3}" +
                        "|(([0-9])|(1[0-9])|(2[0-4]))))").toRegex(),
                message = getString(R.string.alert_field),
                layout = textInputLayout2))
        edtGateway.addTextChangedListener(RegexTextWatcher(
                regex = android.util.Patterns.IP_ADDRESS.toRegex(),
                message = getString(R.string.alert_field),
                layout = textInputLayout3))
        edtDns1.addTextChangedListener(RegexTextWatcher(
                regex = android.util.Patterns.IP_ADDRESS.toRegex(),
                message = getString(R.string.alert_field),
                layout = textInputLayout4))
        edtDns2.addTextChangedListener(RegexTextWatcher(
                regex = android.util.Patterns.IP_ADDRESS.toRegex(),
                message = getString(R.string.alert_field),
                layout = textInputLayout5))

        if (OsHelper.isConeV2()) {
            btnIp.setOnClickListener {
                if (presenter.isValidIp(edtIp.text.toString())
                        && presenter.isValidIp(edtDns1.text.toString())
                        && presenter.isValidIp(edtGateway.text.toString())
                        && presenter.isValidIp(edtDns2.text.toString())
                        && presenter.isValidMask(edtMask.text.toString())) {
                    val prefix = presenter.fromMasktoPrefix(edtMask.text.toString())
                    if (prefix != null
                            && edtIp != null
                            && edtDns1 != null
                            && edtDns2 != null
                            && edtGateway != null) {
                        StaticIpConfig.configureStaticIp(view.context,
                                edtIp.text.toString(),
                                prefix,
                                edtDns1.text.toString(),
                                edtDns2.text.toString())
                        Toast.makeText(context, R.string.static_ip, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, R.string.error_Edt, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, R.string.error_Edt, Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, R.string.wrong_Device, Toast.LENGTH_SHORT).show()
        }
    }
}