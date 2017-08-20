package net.eraga.rxusb

import java.nio.channels.Channel

interface UsbInterfaceConnection : Channel {

    val usbInterface: UsbInterface
    val deviceConnection: UsbDeviceConnection

    /**
     * Open an endpoint to send or receive data. Endpoint is a readable
     * or writable channel.
     */
    fun open(endpoint: UsbEndpoint, timeout: Long = 0): Channel
}
