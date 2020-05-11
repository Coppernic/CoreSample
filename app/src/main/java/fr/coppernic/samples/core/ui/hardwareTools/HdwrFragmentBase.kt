package fr.coppernic.samples.core.ui.hardwareTools

interface HdwrFragmentBase {
    fun isHardwareSupported(): Boolean

    fun getTitle(): String?
}