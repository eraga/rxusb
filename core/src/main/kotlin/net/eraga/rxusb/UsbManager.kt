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
     * data using {@link android.hardware.rxusb.UsbRequest}.
     *
     * @param device the device to open
     * @return a {@link UsbDeviceConnection}, or {@code null} if open failed
     */
    fun openDevice(device: UsbDevice): UsbDeviceConnection


    fun findDevice(vendorId: Short, productId: Short): UsbDevice
    fun findDevices(vendorId: Short, productId: Short): Array<UsbDevice>
}
