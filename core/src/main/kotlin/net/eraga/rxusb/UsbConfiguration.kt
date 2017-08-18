package net.eraga.rxusb

import java.util.*

/**
 * Date: 23/04/2017
 * Time: 22:19
 */
class UsbConfiguration(
        val mId: Int,
        val mName: String,
        val mAttributes: Int,
        val mMaxPower: Int
) {

    private var _interfaces: Array<UsbInterface>? = null
    var interfaces: Array<UsbInterface>
        set(value) = if (_interfaces == null) _interfaces = value else throw IllegalStateException("Field can be " +
                "initialized only once")
        get() = _interfaces ?: throw IllegalStateException("Initialization is not completed")

    /**
     * Mask for "self-powered" bit in the configuration's attributes.
     * @see .getAttributes
     */
    private val ATTR_SELF_POWERED = 1 shl 6
    /**
     * Mask for "remote wakeup" bit in the configuration's attributes.
     * @see .getAttributes
     */
    private val ATTR_REMOTE_WAKEUP = 1 shl 5

    /**
     * Returns the self-powered attribute value configuration's attributes field.
     * This attribute indicates that the device has a power source other than the USB connection.

     * @return the configuration's self-powered attribute
     */
    fun isSelfPowered(): Boolean = (mAttributes and ATTR_SELF_POWERED) != 0

    /**
     * Returns the remote-wakeup attribute value configuration's attributes field.
     * This attributes that the device may signal the host to wake from suspend.

     * @return the configuration's remote-wakeup attribute
     */
    fun isRemoteWakeup(): Boolean = (mAttributes and ATTR_REMOTE_WAKEUP) != 0

    /**
     * Returns the configuration's max power consumption, in milliamps.

     * @return the configuration's max power
     */
    fun getMaxPower(): Int {
        return mMaxPower * 2
    }

    /**
     * Returns the [UsbInterface] at the given index.

     * @return the interface
     */
    fun getInterface(index: Int): UsbInterface {
        return interfaces[index]
    }

    /**
     * Returns the number of [UsbInterface]s this configuration contains.

     * @return the number of endpoints
     */
    fun getInterfaceCount(): Int {
        return interfaces.size
    }




    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UsbConfiguration) return false

        if (mId != other.mId) return false
        if (mName != other.mName) return false
        if (mAttributes != other.mAttributes) return false
        if (mMaxPower != other.mMaxPower) return false
        if (!Arrays.equals(_interfaces, other._interfaces)) return false
        if (ATTR_SELF_POWERED != other.ATTR_SELF_POWERED) return false
        if (ATTR_REMOTE_WAKEUP != other.ATTR_REMOTE_WAKEUP) return false

        return true
    }

    override fun hashCode(): Int {
        var result = mId
        result = 31 * result + mName.hashCode()
        result = 31 * result + mAttributes
        result = 31 * result + mMaxPower
        result = 31 * result + (_interfaces?.let { Arrays.hashCode(it) } ?: 0)
        result = 31 * result + ATTR_SELF_POWERED
        result = 31 * result + ATTR_REMOTE_WAKEUP
        return result
    }

    override fun toString(): String {
        return "\n\tUsbConfiguration(\n" +
                "\t\tmId=$mId, mName='$mName',\n" +
                "\t\t_interfaces=${Arrays.toString(_interfaces)})"
    }


}
