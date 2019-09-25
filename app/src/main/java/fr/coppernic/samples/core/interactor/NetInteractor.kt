package fr.coppernic.samples.core.interactor

import android.content.Context

interface NetInteractor {

    fun enableEthernet(context: Context, enable: Boolean)
    fun enableCradle(context: Context, enable: Boolean)
}

object NetFactory {

    enum class ImplType { CONE, CONE2 }

    fun getEthernetInteractor(type: ImplType): NetInteractor {
        return when (type) {
            NetFactory.ImplType.CONE2 -> NetInteractorImpl()
            NetFactory.ImplType.CONE -> NetInteractorImplCone()
        }
    }
}