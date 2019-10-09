package fr.coppernic.samples.core.service.net.ethernet

import android.os.IBinder
import fr.coppernic.sdk.net.ethernet.IEthernet
import fr.coppernic.sdk.utils.service.BaseServiceManager

private const val SERVICE_ACTION_BIND = "fr.coppernic.sdk.net.ethernet.BIND"

class EthernetServiceManager : BaseServiceManager<EthernetConnector>() {

    override fun getAction(): String {
        return SERVICE_ACTION_BIND
    }

    /**
     * Get {@link EthernetServiceManager} instance
     *
     * @return {@link EthernetServiceManager} instance
     */
    fun get(): EthernetServiceManager {
        return Holder.INSTANCE
    }

    override fun createConnector(service: IBinder): EthernetConnector {
        return EthernetConnector(IEthernet.Stub.asInterface(service))
    }

    private object Holder {
        val INSTANCE = EthernetServiceManager()
    }
}