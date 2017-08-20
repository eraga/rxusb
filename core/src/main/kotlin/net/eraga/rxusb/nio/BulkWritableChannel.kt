package net.eraga.rxusb.nio

import net.eraga.rxusb.UsbEndpoint
import net.eraga.rxusb.UsbInterfaceConnection
import java.nio.channels.WritableByteChannel

abstract class BulkWritableChannel(
        val interfaceConnection: UsbInterfaceConnection,
        val endpoint: UsbEndpoint,
        var timeout: Long
) : RxWritableByteChannel() {
    override fun isOpen(): Boolean = interfaceConnection.isOpen

    @Synchronized
    override fun close() {

    }
}
