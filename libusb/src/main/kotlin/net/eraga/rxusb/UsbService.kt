package net.eraga.rxusb

import net.eraga.rxusb.platform.LibUsbManager

object UsbService {
    val instance: UsbManager = LibUsbManager()
}
