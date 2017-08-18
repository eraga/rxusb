package net.eraga.rxusb.examples.serial

import net.eraga.rxusb.UsbService
import net.eraga.rxusb.serial.CH34xSerialDevice
import net.eraga.rxusb.serial.UsbSerial
import org.slf4j.LoggerFactory
import org.usb4java.*

/**
 * Date: 23/06/2017
 * Time: 15:15
 */
internal object Main {
    val log = LoggerFactory.getLogger(Main::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
        log.info("AAAAAAAAAA")
        val manager = UsbService.instance

//        for(device in manager.getDeviceList())
            println(manager.findDevice(0x1a86, 0x7523))

        val d = manager.findDevice(0x1a86, 0x7523)

        val dc = manager.openDevice(d)

        val ic = dc.claimInterface(d.getInterface(0))

        val serial = CH34xSerialDevice(ic)
//        serial.setBaudRate(115200);
        serial.setDataBits(UsbSerial.DATA_BITS_8);
        serial.setParity(UsbSerial.PARITY_NONE);
        serial.setStopBits(1);
        serial.setFlowControl(UsbSerial.FLOW_CONTROL_OFF);
        serial.setRTS(true)
        serial.setDTR(true)

        serial.open()
//        serial.setBaudRate(230400);
//        serial.setBaudRate(115200);
        serial.setBaudRate(921600);

        val regex = """DEVICE: (.*) ==> \[(.*)\](.*)([\d])    -([\d]{2})""".toRegex()
        val regex2 = """T: (.*) ST: (.*) RSSI: (.*) Ch: (.*)	SRC: (.*)	DST: (.*)""".toRegex()
//                     "T: 0x0 ST: 0x8 RSSI: -72 Ch: 11	SRC: 44:55:b1:ed:66:58	DST: ff:ff:ff:ff:ff:ff"



        serial.read(object : UsbSerial.UsbReadCallback {
            override fun onReceivedData(data: ByteArray) {
//                7802f8fc392a — me

//                val found = data.find { it == '\n'.toByte() }



//                println(String(data, Charsets.US_ASCII).contains("\r\n"))
//                if(input.contains("BAD"))
                val input = String(data, Charsets.US_ASCII).trim()
//                println(data.toHex())
//                print(input)
//                return


                for(line in input.split("\r\n")) {
                    println("\"" + line + "\"")
                    if(line.startsWith("BEACON"))
                        continue
                    val mac = regex2.matchEntire(line)?.groups?.get(5)?.value

                    println(mac)
                    if(mac == "7802f8fc392a") {
//                        log.info(regex.matchEntire(line)?.groups?.get(1)?.value)
                        println(line)
                    }

                }


//                log.info("{} / {}", data.size, String(data, Charsets.US_ASCII))
            }
        })



//        classic()
    }

    fun findCRLF() {

    }

    fun classic() {
        val context = Context()


        if (LibUsb.init(context) != LibUsb.SUCCESS) throw LibUsbException("Unable to initialize libusb.", 0)

        val list = DeviceList()

        if (LibUsb.getDeviceList(null, list) < 0)
            throw LibUsbException("Unable to get device list", LibUsb.getDeviceList(null, list))


        list.forEach {
            val descriptor = DeviceDescriptor()
            val result = LibUsb.getDeviceDescriptor(it, descriptor)
            if (result == 0) {

//                    if (descriptor.bDeviceClass().toInt() != 2) {
                println("=============")
                println(it)
                println(descriptor)
            }
        }
    }

    private val HEX_CHARS = "0123456789ABCDEF".toCharArray()

    fun ByteArray.toHex() : String{
        val result = StringBuffer()

        forEach {
            val octet = it.toInt()
            val firstIndex = (octet and 0xF0).ushr(4)
            val secondIndex = octet and 0x0F
            result.append(HEX_CHARS[firstIndex])
            result.append(HEX_CHARS[secondIndex])
        }

        return result.toString()
    }
}
