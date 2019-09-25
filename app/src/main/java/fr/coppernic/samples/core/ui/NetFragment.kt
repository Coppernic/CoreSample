package fr.coppernic.samples.core.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import fr.coppernic.samples.core.R
import fr.coppernic.samples.core.interactor.NetFactory
import fr.coppernic.samples.core.interactor.NetFactory.ImplType.*
import fr.coppernic.sdk.net.cone2.StaticIpConfig
import fr.coppernic.sdk.utils.helpers.OsHelper
import kotlinx.android.synthetic.main.fragment_net.*
import fr.coppernic.samples.core.utils.RegexTextWatcher
import fr.coppernic.sdk.utils.helpers.CpcNet

/**
 * A simple [Fragment] subclass.
 *
 */
class NetFragment : Fragment() {

    private val presenter = NetPresenter()
    private val netCone = NetFactory.getEthernetInteractor(CONE)
    private val netCone2 = NetFactory.getEthernetInteractor(CONE2)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_net, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toggleEthernet.isEnabled = false
        toggleCradleEthernet.isChecked = false
        toggleCradleEthernet.isEnabled = CpcNet.isEthernetConnected(context)
        toggleCradleEthernet.visibility = View.INVISIBLE
        btnIp.isEnabled = false

        edtIp.addTextChangedListener(RegexTextWatcher(
                regex = android.util.Patterns.IP_ADDRESS.toRegex(),
                message = getString(R.string.alert_wrong_Ip),
                layout = textInputLayout))
        edtMask.addTextChangedListener(RegexTextWatcher(
                regex = presenter.regexMask,
                message = getString(R.string.alert_wrong_Mask),
                layout = textInputLayout2))
        edtGateway.addTextChangedListener(RegexTextWatcher(
                regex = android.util.Patterns.IP_ADDRESS.toRegex(),
                message = getString(R.string.alert_wrong_Gateway),
                layout = textInputLayout3))
        edtDns1.addTextChangedListener(RegexTextWatcher(
                regex = android.util.Patterns.IP_ADDRESS.toRegex(),
                message = getString(R.string.alert_wrong_DNS1),
                layout = textInputLayout4))
        edtDns2.addTextChangedListener(RegexTextWatcher(
                regex = android.util.Patterns.IP_ADDRESS.toRegex(),
                message = getString(R.string.alert_wrong_DNS2),
                layout = textInputLayout5))

        toggleEthernet.isEnabled = true
        toggleEthernet.isChecked = false
        toggleEthernet.setOnCheckedChangeListener { _, isEthernetChecked ->
            if (OsHelper.isConeV2()) context?.let { netCone2.enableEthernet(it, true) }
            else if (OsHelper.isCone()) context?.let { netCone.enableEthernet(it, true) }
            if (isEthernetChecked) {
                toggleCradleEthernet.isEnabled = true
                btnIp.isEnabled = true
            } else {
                if (OsHelper.isConeV2()) context?.let { netCone2.enableEthernet(it, false) }
                else if (OsHelper.isCone()) context?.let { netCone.enableEthernet(it, false) }
                btnIp.isEnabled = false
                toggleCradleEthernet.isChecked = false
                toggleCradleEthernet.isEnabled = false
            }
        }

        if (OsHelper.isConeV2()) {
            toggleCradleEthernet.visibility = View.VISIBLE
            toggleCradleEthernet.setOnCheckedChangeListener { _, isCradleChecked ->
                if (isCradleChecked) context?.let { netCone2.enableCradle(it, true) }
                else context?.let { netCone2.enableCradle(it, false) }
            }
        }

        btnIp.setOnClickListener {
            if (presenter.isValidIp(edtIp.text.toString())
                    && presenter.isValidIp(edtDns1.text.toString())
                    && presenter.isValidIp(edtGateway.text.toString())
                    && presenter.isValidIp(edtDns2.text.toString())
                    && presenter.isValidMask(edtMask.text.toString())) {
                val prefix = presenter.fromMaskPrefix(edtMask.text.toString())
                if (prefix != null) {
                    StaticIpConfig.configureStaticIp(view.context,
                            edtIp.text.toString(),
                            prefix,
                            edtDns1.text.toString(),
                            edtDns2.text.toString())
                }
                Toast.makeText(context, R.string.static_ip, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, R.string.error_Edt, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

