package net.eraga.rxusb

import java.io.Closeable



interface UsbManager : Closeable {


    /**
     * Returns a [Array] containing all USB devices currently attached.
     * The result will be empty if no devices are attached.

     * @return [Array] containing all connected USB devices.
     */
    fun getDeviceList(): Array<UsbDevice>


    /**
     * Opens the device so it can be used to send and receive
     * data using [UsbInterfaceConnection].
     *
     * @param device the device to open
     * @return a {@link UsbDeviceConnection}, or {@code null} if open failed
     */
    fun openDevice(device: UsbDevice): UsbDeviceConnection


    /**
     * Find the device by [vendorId] and [productId]. If there are more than one device
     * with the same ids it will return the firs one. Use [findDevices] to get full list.
     */
    fun findDevice(vendorId: Short, productId: Short): UsbDevice

    /**
     * Find all devices by [vendorId] and [productId].
     */
    fun findDevices(vendorId: Short, productId: Short): Array<UsbDevice>
}
