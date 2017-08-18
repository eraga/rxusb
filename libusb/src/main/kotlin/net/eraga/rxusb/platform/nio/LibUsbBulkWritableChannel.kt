package net.eraga.rxusb.platform.nio

import net.eraga.rxusb.UsbEndpoint
import net.eraga.rxusb.UsbInterfaceConnection
import net.eraga.rxusb.nio.BulkWritableChannel
import java.nio.ByteBuffer

/**
 * Date: 23/04/2017
 * Time: 23:39
 */
class LibUsbBulkWritableChannel(
        interfaceConnection: UsbInterfaceConnection,
        endpoint: UsbEndpoint,
        timeout: Long
) : BulkWritableChannel(interfaceConnection, endpoint, timeout) {



    @Synchronized
    override fun write(src: ByteBuffer): Int {
        throw UnsupportedOperationException("not implemented") //TODO
    }

    override fun isOpen(): Boolean = interfaceConnection.isOpen

    @Synchronized
    override fun close() {
        // this won't suppose to close anything.
    }
}
