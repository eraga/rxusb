package net.eraga.rxusb.platform

import net.eraga.rxusb.device
import net.eraga.rxusb.throwOnFail
import java.nio.ByteBuffer

/**
 * Date: 22/06/2017
 * Time: 10:04
 */
class LibUsbDeviceConnection internal constructor(
        override val usbDevice: net.eraga.rxusb.UsbDevice
) : net.eraga.rxusb.UsbDeviceConnection {
    private var _handle: org.usb4java.DeviceHandle?
    internal val handle: org.usb4java.DeviceHandle
        get() {
            return _handle ?: throw net.eraga.rxusb.exceptions.UsbException("Closed device connection")
        }

    init {
        val device = usbDevice.device
        val newHandle = org.usb4java.DeviceHandle()
        org.usb4java.LibUsb.open(device, newHandle).throwOnFail("Failed to open $device ${usbDevice.productName}")
        _handle = newHandle
    }

    internal val interfaceConnections = ArrayList<net.eraga.rxusb.UsbInterfaceConnection>()

    /**
     * Performs a control transaction on endpoint zero for this device.
     * The direction of the transfer is determined by the request type.
     * If requestType & [UsbConstants.USB_ENDPOINT_DIR_MASK] is
     * [UsbConstants.USB_DIR_OUT], then the transfer is a write,
     * and if it is [UsbConstants.USB_DIR_IN], then the transfer
     * is a read.
     *
     *
     * This method transfers data starting from index 0 in the buffer.
     * To specify a different offset, use
     * [controlTransfer].
     *

     * @param requestType request type for this transaction
     * *
     * @param request request ID for this transaction
     * *
     * @param value value field for this transaction
     * *
     * @param index index field for this transaction
     * *
     * @param buffer buffer for data portion of transaction,
     * * or null if no data needs to be sent or received
     * *
     * @param length the length of the data to send or receive
     * *
     * @param timeout in milliseconds
     * *
     * @return length of data transferred (or zero) for success,
     * * or negative value for failure
     */
    override fun controlTransfer(requestType: Int, request: Int, value: Int,
                        index: Int, buffer: ByteArray, length: Int, timeout: Int): Int {

        val byteBuffer = ByteBuffer.allocateDirect(length);
        byteBuffer.put(buffer)

        val result = org.usb4java.LibUsb
                .controlTransfer(
                        handle,
                        requestType.toByte(),
                        request.toByte(),
                        value.toShort(),
                        index.toShort(),
                        byteBuffer,
                        timeout.toLong())

        if(result < 0)
            result.throwOnFail()


        return result
    }


    override fun claimInterface(usbInterface: net.eraga.rxusb.UsbInterface, force: Boolean): net.eraga.rxusb.UsbInterfaceConnection {
        val conn = LibUsbInterfaceConnection(usbInterface, this, force)
        interfaceConnections.add(conn)

        return conn
    }

    override fun close() {
        val connections = interfaceConnections.toTypedArray()
        for (conn in connections) {
            conn.close()
        }

        org.usb4java.LibUsb.close(handle)

        _handle = null
    }
}
