package net.eraga.rxusb

import java.nio.channels.Channel

/**
 * Date: 23/06/2017
 * Time: 09:16
 */
interface UsbInterfaceConnection : Channel {

    val usbInterface: UsbInterface
    val deviceConnection: UsbDeviceConnection

    fun open(endpoint: UsbEndpoint, timeout: Long = 0): Channel
}
