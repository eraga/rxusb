package net.eraga.rxusb.platform

import net.eraga.rxusb.*
import net.eraga.rxusb.exceptions.UsbEntityNotFound
import net.eraga.rxusb.exceptions.UsbException
import org.usb4java.*


class LibUsbManager internal constructor()
    : UsbManager {

    override fun getDeviceList(): Array<UsbDevice> {
        return filterDevices(null, null)
    }


    val context: Context = Context()

    init {
        LibUsb.init(context).throwOnFail()
    }

    override fun openDevice(device: UsbDevice): UsbDeviceConnection {
        return LibUsbDeviceConnection(device)
    }

    override fun close() {
        LibUsb.exit(context)
    }

    @Throws(UsbEntityNotFound::class)
    override fun findDevice(vendorId: Short, productId: Short): UsbDevice {
        val devices = filterDevices(vendorId, productId).takeIf {
            it.isNotEmpty()
        } ?: throw UsbEntityNotFound("No device with vid=$vendorId, pid=$productId")

        return devices[0]
    }

    private fun filterDevices(vendorId: Short?, productId: Short?): Array<UsbDevice> {
        val list = DeviceList()

        val result = LibUsb.getDeviceList(null, list)

        if (result < 0)
            result.throwOnFail("Unable to get device list")
        val listFiltered: List<Device>
        if (vendorId != null && productId != null) {
            listFiltered = list.filter { device ->
                val descriptor = DeviceDescriptor()
                LibUsb.getDeviceDescriptor(device, descriptor)
                        .throwOnFail("Unable to read device descriptor")

                val filter = (descriptor.idVendor() == vendorId &&
                        descriptor.idProduct() == productId)

                return@filter filter
            }
        } else {
            listFiltered = list.map { it }
        }

        try {
            return Array(
                    listFiltered.size,
                    { assembleDevice(listFiltered[it]) }
            )
        } finally {
            LibUsb.freeDeviceList(list, true)
        }
    }

    //    @Throws(UsbEntityNotFound::class)
    override fun findDevices(vendorId: Short, productId: Short): Array<UsbDevice> {
        return filterDevices(vendorId, productId)

//        LibUsb.getDeviceList(null, list).throwOnFail("Unable to get device list")
//
//        try {
//            // Iterate over all devices and scan for the right one
//            for (device in list) {
//
//                val descriptor = DeviceDescriptor()
//
//                LibUsb.getDeviceDescriptor(device, descriptor).throwOnFail("Unable to read device descriptor")
//
//                if (descriptor.idVendor() == vendorId && descriptor.idProduct() == productId) {
//                    return assembleDevice(device)
//                }
//            }
//        } finally {
//            // Ensure the allocated device list is freed
//            LibUsb.freeDeviceList(list, true)
//        }

//        throw UsbEntityNotFound("productId: $vendorId, productId: $productId")
    }

    private fun assembleDevice(
            device: Device): UsbDevice {

        val descriptor = device.getDescriptor()


        val configDesc = ConfigDescriptor()
        LibUsb.getConfigDescriptor(device, 0, configDesc).throwOnFail()


        var productName =
                "USB ${descriptor.bcdUSB()} ${DescriptorUtils.getUSBClassName(descriptor.bDeviceClass())} Device"
        var manufacturerName = "USB Device"
        var serialNumber = "UNKNOWN"

        var handle: DeviceHandle? = null

        try {
            handle = device.getHandle()

            if (descriptor.iProduct().toInt() != 0)
                productName = LibUsb.getStringDescriptor(handle, descriptor.iProduct()) ?: productName


            if (descriptor.iProduct().toInt() != 0)
                manufacturerName = LibUsb.getStringDescriptor(handle, descriptor.iManufacturer()) ?: manufacturerName


            if (descriptor.iProduct().toInt() != 0)
                serialNumber = LibUsb.getStringDescriptor(handle, descriptor.iSerialNumber()) ?: serialNumber
        } catch (e: UsbException) {
            serialNumber = "$serialNumber (${e.message.toString()})"
            manufacturerName = "$manufacturerName (${e.message.toString()})"
            productName = "$productName (${e.message.toString()})"
        }


        val configs = device.getConfigs()


        val usbDevice = UsbDevice(
                descriptor.idVendor().toInt(),
                descriptor.idProduct().toInt(),
                descriptor.bDeviceClass().toPositiveInt(),
                descriptor.bDeviceSubClass().toPositiveInt(),
                descriptor.bDeviceProtocol().toPositiveInt(),
                descriptor.bcdDevice().toString(),
                productName,
                manufacturerName,
                serialNumber
        )

        usbDevice.device = device

        val defaultConfName: String
        if (handle == null) {
            defaultConfName = "Permission denied"
        } else {
            defaultConfName = "UNKNOWN"
        }

        val usbConfigurations = Array<UsbConfiguration>(configs.size, {
            val usbConfiguration = UsbConfiguration(
                    configs[it].bConfigurationValue().toPositiveInt(),
                    LibUsb.getStringDescriptor(handle, configs[it].iConfiguration()) ?: defaultConfName,
                    configs[it].bmAttributes().toInt(),
                    configs[it].bMaxPower().toInt()

            )

            usbConfiguration.interfaces = convertInterfaces(configs[it].iface(), usbDevice, handle)

            return@Array usbConfiguration
        })

        usbDevice.configurations = usbConfigurations

        if(handle != null)
            LibUsb.close(handle)

        return usbDevice
    }

    private fun convertInterfaces(iface: Array<Interface>, usbDevice: UsbDevice, handle: DeviceHandle?): Array<UsbInterface> {
        val list = ArrayList<UsbInterface>(iface.size)

        if (iface.isNotEmpty()) {
            iface.forEach {
                it.altsetting().forEach { ifaceDescriptor ->
                    val usbInterface = UsbInterface(
                            usbDevice,
                            ifaceDescriptor.bInterfaceNumber().toPositiveInt(),
                            ifaceDescriptor.bAlternateSetting().toInt(),
                            LibUsb.getStringDescriptor(handle,
                                    ifaceDescriptor.iInterface()) ?: "UNKNOWN",
                            ifaceDescriptor.bInterfaceClass().toInt(),
                            ifaceDescriptor.bInterfaceSubClass().toInt(),
                            ifaceDescriptor.bInterfaceProtocol().toInt()
                    )

                    val endpoints = ArrayList<UsbEndpoint>(ifaceDescriptor.bNumEndpoints().toPositiveInt())
                    ifaceDescriptor.endpoint().forEach { endpoint ->
                        val usbEndpoint = UsbEndpoint(
                                endpoint.bEndpointAddress().toInt(),
                                endpoint.bmAttributes().toInt(),
                                endpoint.wMaxPacketSize().toInt(),
                                endpoint.bInterval().toPositiveInt()
                        )
                        usbEndpoint.usbInterface = usbInterface
                        endpoints.add(usbEndpoint)
                    }

                    usbInterface.endpoints = endpoints.toTypedArray()
                    list.add(usbInterface)
                }

            }
        }
        return list.toTypedArray()
    }
}
