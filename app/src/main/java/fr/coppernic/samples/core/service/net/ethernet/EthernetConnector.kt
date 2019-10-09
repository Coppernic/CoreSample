package fr.coppernic.samples.core.service.net.ethernet

import android.os.RemoteException
import fr.coppernic.sdk.net.ethernet.IEthernet
import java.io.Closeable

class EthernetConnector(private val interactor: IEthernet) : Closeable {

    /**
     * Enable ethernet
     *
     * * Caller must share system uid to run this method.
     *
     * @param enable true to enable ethernet, false to disable it
     */
    fun enableEthernet(enable: Boolean) {
        try {
            interactor.enableEthernet(enable)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    /**
     * Enable ethernet going through docking station
     *
     * Supported only on C-One² from OS 20190329
     *
     * @param enable true to enable docking, false to disable it
     */
    fun enableEthernetThroughDocking(enable: Boolean) {
        try {
            interactor.enableEthernetThroughDocking(enable)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }


    override fun close() {
        EthernetServiceManager().get().close(this)
    }
}