package net.eraga.rxusb

/**
 * Contains constants for the USB protocol.
 * These constants correspond to definitions in linux/rxusb/ch9.h in the linux kernel.
 */
object UsbConstants {

    /**
     * Bitmask used for extracting the [UsbEndpoint] direction from its address field.
     * @see UsbEndpoint.getAddress
     * @see UsbEndpoint.getDirection
     * @see USB_DIR_OUT
     * @see USB_DIR_IN
     */
    const val USB_ENDPOINT_DIR_MASK = 0x80

    /**
     * Used to signify direction of data for a [UsbEndpoint] is OUT (host to device)
     * @see UsbEndpoint.getDirection
     */
    const val USB_DIR_OUT = 0

    /**
     * Used to signify direction of data for a [UsbEndpoint] is IN (device to host)
     * @see UsbEndpoint.getDirection
     */
    const val USB_DIR_IN = 0x80

    /**
     * Bitmask used for extracting the [UsbEndpoint] number its address field.
     * @see UsbEndpoint.getAddress

     * @see UsbEndpoint.getEndpointNumber
     */
    const val USB_ENDPOINT_NUMBER_MASK = 0x0f

    /**
     * Bitmask used for extracting the [UsbEndpoint] type from its address field.
     * @see UsbEndpoint.getAddress
     * @see UsbEndpoint.getType
     * @see USB_ENDPOINT_XFER_CONTROL
     * @see USB_ENDPOINT_XFER_ISOC
     * @see USB_ENDPOINT_XFER_BULK
     * @see USB_ENDPOINT_XFER_INT
     */
    const val USB_ENDPOINT_XFERTYPE_MASK = 0x03
    /**
     * Control endpoint type (endpoint zero)
     * @see UsbEndpoint.getType
     */
    const val USB_ENDPOINT_XFER_CONTROL = 0
    /**
     * Isochronous endpoint type (currently not supported)
     * @see UsbEndpoint.getType
     */
    const val USB_ENDPOINT_XFER_ISOC = 1
    /**
     * Bulk endpoint type
     * @see UsbEndpoint.getType
     */
    const val USB_ENDPOINT_XFER_BULK = 2
    /**
     * Interrupt endpoint type
     * @see UsbEndpoint.getType
     */
    const val USB_ENDPOINT_XFER_INT = 3

    /**
     * Bitmask used for encoding the request type for a control request on endpoint zero.
     */
    const val USB_TYPE_MASK = 0x03 shl 5
    /**
     * Used to specify that an endpoint zero control request is a standard request.
     */
    const val USB_TYPE_STANDARD = 0x00 shl 5
    /**
     * Used to specify that an endpoint zero control request is a class specific request.
     */
    const val USB_TYPE_CLASS = 0x01 shl 5
    /**
     * Used to specify that an endpoint zero control request is a vendor specific request.
     */
    const val USB_TYPE_VENDOR = 0x02 shl 5
    /**
     * Reserved endpoint zero control request type (currently unused).
     */
    const val USB_TYPE_RESERVED = 0x03 shl 5
    /**
     * USB class indicating that the class is determined on a per-interface basis.
     */
    const val USB_CLASS_PER_INTERFACE = 0
    /**
     * USB class for audio devices.
     */
    const val USB_CLASS_AUDIO = 1
    /**
     * USB class for communication devices.
     */
    const val USB_CLASS_COMM = 2
    /**
     * USB class for human interface devices (for example, mice and keyboards).
     */
    const val USB_CLASS_HID = 3
    /**
     * USB class for physical devices.
     */
    const val USB_CLASS_PHYSICA = 5
    /**
     * USB class for still image devices (digital cameras).
     */
    const val USB_CLASS_STILL_IMAGE = 6
    /**
     * USB class for printers.
     */
    const val USB_CLASS_PRINTER = 7
    /**
     * USB class for mass storage devices.
     */
    const val USB_CLASS_MASS_STORAGE = 8
    /**
     * USB class for USB hubs.
     */
    const val USB_CLASS_HUB = 9
    /**
     * USB class for CDC devices (communications device class).
     */
    const val USB_CLASS_CDC_DATA = 0x0a
    /**
     * USB class for content smart card devices.
     */
    const val USB_CLASS_CSCID = 0x0b
    /**
     * USB class for content security devices.
     */
    const val USB_CLASS_CONTENT_SEC = 0x0d
    /**
     * USB class for video devices.
     */
    const val USB_CLASS_VIDEO = 0x0e
    /**
     * USB class for wireless controller devices.
     */
    const val USB_CLASS_WIRELESS_CONTROLLER = 0xe0
    /**
     * USB class for wireless miscellaneous devices.
     */
    const val USB_CLASS_MISC = 0xef
    /**
     * Application specific USB class.
     */
    const val USB_CLASS_APP_SPEC = 0xfe
    /**
     * Vendor specific USB class.
     */
    const val USB_CLASS_VENDOR_SPEC = 0xff
    /**
     * Boot subclass for HID devices.
     */
    const val USB_INTERFACE_SUBCLASS_BOOT = 1
    /**
     * Vendor specific USB subclass.
     */
    const val USB_SUBCLASS_VENDOR_SPEC = 0xff
}
