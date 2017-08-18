package net.eraga.rxusb

/**
 * A class representing an endpoint on a [UsbInterface].
 * Endpoints are the channels for sending and receiving data over USB.
 * Typically bulk endpoints are used for sending non-trivial amounts of data.
 * Interrupt endpoints are used for sending small amounts of data, typically events,
 * separately from the main data streams.
 *
 * The endpoint zero is a special endpoint for control messages sent from the host
 * to device.
 *
 * Isochronous endpoints are currently unsupported.
 *
 * UsbEndpoint should only be instantiated by UsbService implementation
 * @hide
 */
class UsbEndpoint(
        val address: Int,
        val attributes: Int,
        val maxPacketSize: Int,
        val interval: Int
) {

    private var _usbInterface: UsbInterface? = null
    var usbInterface: UsbInterface
        set(value) = if (_usbInterface == null) _usbInterface = value else throw IllegalStateException("Field can be " +
                "initialized only once")
        get() = _usbInterface ?: throw IllegalStateException("Initialization is not completed")



//    private var _inChannel: ReadableByteChannel? = null
//    private var _outChannel: WritableByteChannel? = null
//
//    val readableChannel: ReadableByteChannel
//        get() {
//            return _inChannel ?: throw NonReadableChannelException()
//        }
//
//    val writableChannel: WritableByteChannel
//        get() {
//            return _outChannel ?: throw NonWritableChannelException()
//        }



    /**
     * Extracts the endpoint's endpoint number from its address
     *
     * @return the endpoint's endpoint number
     */
    fun getEndpointNumber(): Int {
        return address and UsbConstants.USB_ENDPOINT_NUMBER_MASK
    }

    /**
     * Returns the endpoint's direction.
     * Returns [UsbConstants.USB_DIR_OUT]
     * if the direction is host to device, and
     * [UsbConstants.USB_DIR_IN] if the
     * direction is device to host.
     *
     * @see UsbConstants.USB_DIR_IN
     * @see UsbConstants.USB_DIR_OUT
     *
     * @return the endpoint's direction
     */
    fun getDirection(): Int {
        return address and UsbConstants.USB_ENDPOINT_DIR_MASK
    }

    /**
     * Returns the endpoint's type.
     * Possible results are:
     *
     *  * [UsbConstants.USB_ENDPOINT_XFER_CONTROL] (endpoint zero)
     *  * [UsbConstants.USB_ENDPOINT_XFER_ISOC] (isochronous endpoint)
     *  * [UsbConstants.USB_ENDPOINT_XFER_BULK] (bulk endpoint)
     *  * [UsbConstants.USB_ENDPOINT_XFER_INT] (interrupt endpoint)
     *

     * @return the endpoint's type
     */
    fun getType(): Int {
        return attributes and UsbConstants.USB_ENDPOINT_XFERTYPE_MASK
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UsbEndpoint) return false

        if (address != other.address) return false
        if (attributes != other.attributes) return false
        if (maxPacketSize != other.maxPacketSize) return false
        if (interval != other.interval) return false
        if (_usbInterface != other._usbInterface) return false

        return true
    }

    override fun hashCode(): Int {
        var result = address
        result = 31 * result + attributes
        result = 31 * result + maxPacketSize
        result = 31 * result + interval
        result = 31 * result + (_usbInterface?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "\n\t\tUsbEndpoint(\n" +
                "\t\t\taddress=$address\n" +
                "\t\t\tattributes=$attributes\n" +
                "\t\t\tmaxPacketSize=$maxPacketSize\n" +
                "\t\t\tinterval=$interval)"
    }


}
