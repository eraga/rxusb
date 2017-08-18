package net.eraga.rxusb.nio

import net.eraga.rxusb.UsbConstants
import net.eraga.rxusb.UsbEndpoint
import net.eraga.rxusb.UsbInterfaceConnection

abstract class BulkReadableChannel(
        val interfaceConnection: UsbInterfaceConnection,
        val endpoint: UsbEndpoint,
        var timeout: Long
) : RxReadableByteChannel() {

    init {
        if (endpoint.getDirection() != UsbConstants.USB_DIR_IN)
            throw IllegalArgumentException(
                    "Endpoint direction is not IN"
            )

        if (endpoint.address > 255)
            throw IllegalArgumentException(
                    "Address value ${endpoint.address} should not exceed 1 byte"
            )


    }


    override fun isOpen(): Boolean {
        return interfaceConnection.isOpen && super.isOpen()
    }
}
