package net.eraga.rxusb.platform.nio

import net.eraga.rxusb.platform.LibUsbInterfaceConnection
import net.eraga.rxusb.throwOnFail
import net.eraga.rxusb.UsbConstants
import net.eraga.rxusb.UsbEndpoint
import net.eraga.rxusb.UsbInterfaceConnection
import net.eraga.rxusb.exceptions.UsbException
import net.eraga.rxusb.nio.BulkReadableChannel
import org.usb4java.BufferUtils
import org.usb4java.LibUsb
import java.nio.ByteBuffer
import java.nio.IntBuffer

class LibUsbBulkReadableChannel(
        interfaceConnection: UsbInterfaceConnection,
        endpoint: UsbEndpoint,
        timeout: Long
) : BulkReadableChannel(interfaceConnection, endpoint, timeout) {


    init {
        if(endpoint.getDirection() != UsbConstants.USB_DIR_IN)
            throw IllegalArgumentException(
                    "Endpoint direction is not IN"
            )

        if (endpoint.address > 255)
            throw IllegalArgumentException(
                    "Address value ${endpoint.address} should not exceed 1 byte"
            )
    }

    private val transferred: IntBuffer = BufferUtils.allocateIntBuffer()

    @Synchronized
    @Throws(UsbException::class)
    override fun read(dst: ByteBuffer): Int {

        LibUsb.bulkTransfer(
                (interfaceConnection as LibUsbInterfaceConnection).deviceConnection.handle,
                endpoint.address.toByte(),
                dst,
                transferred,
                timeout).throwOnFail()

        val t = transferred.get()
        transferred.clear()

        return t
    }
}
