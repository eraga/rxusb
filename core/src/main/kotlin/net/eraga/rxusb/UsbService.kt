package net.eraga.rxusb

import kotlin.reflect.full.primaryConstructor

object UsbService {

    val instance: UsbManager

    init {
        val kClass = Class.forName("net.eraga.rxusb.platform.LibUsbManager").kotlin
        instance = kClass.primaryConstructor!!.call() as UsbManager
    }
}
