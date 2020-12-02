package net.eraga.rxusb

import net.eraga.rxusb.exceptions.UsbDeviceBusy
import net.eraga.rxusb.exceptions.UsbEntityNotFound
import net.eraga.rxusb.exceptions.UsbException
import org.usb4java.*
import org.usb4java.ConfigDescriptor


/**
 * Throws appropriate exception on LibUsb result
 *
 * @param errorMessage to include in
 */
@Throws(UsbException::class)
internal fun Int.throwOnFail(errorMessage: String? = null) {
    if (this != LibUsb.SUCCESS) {
        val fullErrorMessage = errorMessage ?: ""

        val cause = LibUsbException(this)
        when (this) {
            LibUsb.ERROR_BUSY -> throw UsbDeviceBusy(fullErrorMessage, cause)
            LibUsb.ERROR_NOT_FOUND -> throw UsbEntityNotFound(fullErrorMessage, cause)
        }
        throw UsbException(LibUsb.errorName(this), cause)
    }
}


@Throws(UsbException::class)
internal fun Device.getHandle(): DeviceHandle {
    val handle = DeviceHandle()
    LibUsb.open(this, handle).throwOnFail("Unable to open device $this handle")

    return handle
}

@Throws(UsbException::class)
internal fun Device.getDescriptor(): DeviceDescriptor {
    val descriptor = DeviceDescriptor()

    LibUsb.getDeviceDescriptor(this, descriptor)
            .throwOnFail("Unable to read device $this descriptor")

    return descriptor
}

@Throws(UsbException::class)
internal fun Device.getConfigs(): Array<ConfigDescriptor> {
    val numConfigs = this.getDescriptor().bNumConfigurations().toPositiveInt()

    return Array(numConfigs) { index ->
        val configDesc = ConfigDescriptor()
        LibUsb.getConfigDescriptor(this, index.toByte(), configDesc)
                .throwOnFail("Configuration $index does not exist on device $this")
        return@Array configDesc
    }
}


internal fun Byte.toPositiveInt() = toInt() and 0xFF

internal var UsbDevice.device: Device
    get() = _device as Device
    set(value) {_device = value}


