package net.eraga.rxusb.serial

import kotlin.experimental.and


object HexData {
    private const val HEXES = "0123456789ABCDEF"
    private const val HEX_INDICATOR = "0x"
    private const val SPACE = " "

    fun hexToString(data: ByteArray): String {

        val hex = StringBuilder(2 * data.size)
        for (i in 0 until data.size) {
            val dataAtIndex = data[i]
            hex.append(HEX_INDICATOR)
            hex.append(HEXES[dataAtIndex.and(0xF0.toByte()).toInt() shr 4])
                    .append(HEXES[(dataAtIndex and 0x0F.toByte()).toInt()])
            hex.append(SPACE)
        }
        return hex.toString()
    }

    fun stringTobytes(hexString: String): ByteArray {
        var stringProcessed = hexString.trim { it <= ' ' }.replace("0x".toRegex(), "")
        stringProcessed = stringProcessed.replace("\\s+".toRegex(), "")
        val data = ByteArray(stringProcessed.length / 2)
        var i = 0
        var j = 0
        while (i <= stringProcessed.length - 1) {
            val character = Integer.parseInt(stringProcessed.substring(i, i + 2), 16).toByte()
            data[j] = character
            j++
            i += 2
        }
        return data
    }

    fun hex4digits(id: String): String {
        if (id.length == 1) return "000" + id
        if (id.length == 2) return "00" + id
        if (id.length == 3)
            return "0" + id
        else
            return id
    }
}
