package net.eraga.rxusb.serial.deviceids

import net.eraga.rxusb.serial.UsbSerialDevice
import net.eraga.rxusb.serial.CH34xSerialDevice
import kotlin.reflect.KClass


object KnownSerialDevices {
    val deviceTree = HashMap<Int, HashMap<Int, KClass<out UsbSerialDevice>>>()

    init {
        addRecord(0x4348, 0x5523, CH34xSerialDevice::class)
        addRecord(0x1a86, 0x7523, CH34xSerialDevice::class)
        addRecord(0x1a86, 0x5523, CH34xSerialDevice::class)
        addRecord(0x1a86, 0x0445, CH34xSerialDevice::class)
    }




    fun addRecord(vendor: Int, product: Int, clazz: KClass<out UsbSerialDevice>){
        deviceTree.getOrDefault(vendor, HashMap()).put(product, clazz)
    }

    fun getSerialDevice(vendor: Int, product: Int)
            = deviceTree.getOrDefault(vendor, HashMap()).get(product)?.objectInstance as UsbSerialDevice


}
