package fr.coppernic.samples.core.ui.hardwareTools.autoTest

interface HdwrFragmentBase {
    fun isHardwareSupported(): Boolean

    fun getTitle(): String?
}