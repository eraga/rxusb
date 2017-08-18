package net.eraga.rxusb.platform

import net.eraga.rxusb.throwOnFail
import net.eraga.rxusb.UsbConstants
import net.eraga.rxusb.UsbEndpoint
import net.eraga.rxusb.UsbInterface
import net.eraga.rxusb.UsbInterfaceConnection
import net.eraga.rxusb.platform.nio.LibUsbBulkReadableChannel
import net.eraga.rxusb.platform.nio.LibUsbBulkWritableChannel
import org.usb4java.LibUsb
import java.lang.IllegalStateException
import java.nio.channels.Channel


class LibUsbInterfaceConnection(
        override val usbInterface: UsbInterface,
        override val deviceConnection: LibUsbDeviceConnection,
        force: Boolean): UsbInterfaceConnection {

    private var isOpen = false

    private var attachKernelDriver = false

    init {
        if(force) {
            val result = LibUsb.kernelDriverActive(deviceConnection.handle, usbInterface.id)
            if(result == 1) {
                // Kernel Driver is active. Let's detach it.
                LibUsb.detachKernelDriver(deviceConnection.handle, usbInterface.id).throwOnFail()
                attachKernelDriver = true
            } else {
                result.throwOnFail()
            }
        }

        LibUsb.claimInterface(deviceConnection.handle, usbInterface.id).throwOnFail()
        isOpen = true
    }


    override fun open(endpoint: UsbEndpoint, timeout: Long): Channel {
        when(endpoint.getDirection()) {
            UsbConstants.USB_DIR_IN -> return openReadChannel(endpoint, timeout)
            UsbConstants.USB_DIR_OUT -> return openWriteChannel(endpoint, timeout)
        }

        throw IllegalStateException("${endpoint.getDirection()} is neither IN nor OUT")
    }


    private fun openReadChannel(endpoint: UsbEndpoint, timeout: Long) : Channel {
        return LibUsbBulkReadableChannel(this, endpoint, timeout)
    }

    private fun openWriteChannel(endpoint: UsbEndpoint, timeout: Long) : Channel {
        //todo
        return LibUsbBulkWritableChannel(this, endpoint, timeout)
    }

    override fun isOpen() = isOpen

    @Synchronized
    override fun close() {
        LibUsb.releaseInterface(deviceConnection.handle, usbInterface.id).throwOnFail()
        deviceConnection.interfaceConnections.remove(this)
        isOpen = false

        if(attachKernelDriver) {
            LibUsb.attachKernelDriver(deviceConnection.handle, usbInterface.id)
        }
    }
}
