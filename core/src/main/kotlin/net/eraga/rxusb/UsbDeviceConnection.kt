package net.eraga.rxusb

import java.io.Closeable

/**
 * Date: 23/06/2017
 * Time: 09:14
 */
interface UsbDeviceConnection : Closeable {

    val usbDevice: UsbDevice

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
    fun controlTransfer(requestType: Int, request: Int, value: Int,
                        index: Int, buffer: ByteArray, length: Int, timeout: Int): Int



    fun claimInterface(usbInterface: UsbInterface, force: Boolean = false): UsbInterfaceConnection
}
