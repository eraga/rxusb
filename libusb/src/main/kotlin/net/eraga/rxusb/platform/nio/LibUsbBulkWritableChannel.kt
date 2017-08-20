package net.eraga.rxusb.platform.nio

import net.eraga.rxusb.UsbEndpoint
import net.eraga.rxusb.UsbInterfaceConnection
import net.eraga.rxusb.nio.BulkWritableChannel
import net.eraga.rxusb.platform.LibUsbInterfaceConnection
import net.eraga.rxusb.throwOnFail
import org.usb4java.BufferUtils
import org.usb4java.LibUsb
import java.nio.ByteBuffer
import java.nio.IntBuffer


class LibUsbBulkWritableChannel(
        interfaceConnection: UsbInterfaceConnection,
        endpoint: UsbEndpoint,
        timeout: Long
) : BulkWritableChannel(interfaceConnection, endpoint, timeout) {


    @Synchronized
    override fun write(src: ByteBuffer): Int {
        val transferred: IntBuffer = BufferUtils.allocateIntBuffer()

        LibUsb.bulkTransfer(
                (interfaceConnection as LibUsbInterfaceConnection).deviceConnection.handle,
                endpoint.address.toByte(),
                src,
                transferred,
                timeout)
                .throwOnFail()

        val t = transferred.get()
        transferred.clear()

        return t
    }

    override fun isOpen(): Boolean = interfaceConnection.isOpen

    @Synchronized
    override fun close() {
        // this won't suppose to close anything.
    }
}
