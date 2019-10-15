package fr.coppernic.samples.core.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import fr.coppernic.samples.core.R
import fr.coppernic.samples.core.utils.RegexTextWatcher
import fr.coppernic.samples.core.utils.addTo
import fr.coppernic.sdk.net.cone2.StaticIpConfig
import fr.coppernic.sdk.net.ethernet.EthernetConnector
import fr.coppernic.sdk.net.ethernet.EthernetServiceManager
import fr.coppernic.sdk.utils.helpers.CpcNet
import fr.coppernic.sdk.utils.helpers.OsHelper
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_net.*
import timber.log.Timber
import java.security.InvalidParameterException

/**
 * A simple [Fragment] subclass.
 *
 */
class NetFragment : Fragment() {

    private val presenter = NetPresenter()
    private val manager = EthernetServiceManager()
    private var connector: EthernetConnector? = null

    /**
     * Backing field for public var. Always provides a disposable that can be disposed.
     */
    private var disposables = CompositeDisposable()
        get() {
            if (field.isDisposed) {
                field = CompositeDisposable()
            }
            return field
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_net, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        toggleEthernet.isEnabled = false
        toggleEthernet.isChecked = CpcNet.isEthernetConnected(context)
        toggleEthernet.setOnCheckedChangeListener { _, isChecked ->
            connector?.enableEthernet(isChecked)
            toggleCradleEthernet.isEnabled = isChecked
            if (!isChecked) {
                toggleCradleEthernet.isChecked = false
            }
        }

        toggleCradleEthernet.isEnabled = toggleEthernet.isChecked
        toggleCradleEthernet.visibility = if (OsHelper.isConeV2()) View.VISIBLE else View.INVISIBLE
        toggleCradleEthernet.setOnCheckedChangeListener { _, isChecked ->
            connector?.enableCradle(isChecked)
        }

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

        btnIp.setOnClickListener {
            if (presenter.isValidIp(edtIp.text.toString())
                    && presenter.isValidIp(edtDns1.text.toString())
                    && presenter.isValidIp(edtGateway.text.toString())
                    && presenter.isValidIp(edtDns2.text.toString())
                    && presenter.isValidMask(edtMask.text.toString())) {

                try {
                    val prefix = presenter.fromMaskPrefix(edtMask.text.toString())
                    StaticIpConfig.configureStaticIp(view.context,
                            edtIp.text.toString(),
                            prefix,
                            edtDns1.text.toString(),
                            edtDns2.text.toString())
                    Toast.makeText(context, R.string.static_ip, Toast.LENGTH_SHORT).show()
                } catch (e: InvalidParameterException) {
                    Toast.makeText(context, R.string.error_Edt, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        manager.get().getConnector(context).subscribe({
            connector = it
            toggleEthernet.isEnabled = true
        }, {
            Timber.e("Ethernet manager is not supported on this device")
            Timber.w(it)
        }).addTo(disposables)
    }

    override fun onStop() {
        super.onStop()
        connector?.close()
    }
}
