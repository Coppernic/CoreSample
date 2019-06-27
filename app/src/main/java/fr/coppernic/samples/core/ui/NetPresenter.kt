package fr.coppernic.samples.core.ui

class NetPresenter () {


    fun isValidIp () : Boolean {

        val regexIp = android.util.Patterns.IP_ADDRESS
        return isValidIp()
    }

    fun isValidMask ():Boolean {

        val regexMask = Regex(pattern = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}\\/\\d+")
        return isValidMask()
    }
    fun fromMasktoPrefix(netmask: Int):Int {

        return Integer.bitCount(netmask)

    }
    @Throws(IllegalArgumentException::class)

    fun prefixLengthToNetmaskInt(prefixLength: Int): Int {
        if (prefixLength < 0 || prefixLength > 32) {
            throw IllegalArgumentException("Invalid prefix length (0 <= prefix <= 32)")
        }
        val value = -0x1 shl 32 - prefixLength //ou val value = -0x1 shl 32 - prefixLength
        return Integer.reverseBytes(value)
    }
}