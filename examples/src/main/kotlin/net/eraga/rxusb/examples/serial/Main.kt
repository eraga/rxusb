package net.eraga.rxusb.examples.serial

import io.reactivex.schedulers.Schedulers
import net.eraga.rxusb.UsbService
import net.eraga.rxusb.nio.BulkReadableChannel
import net.eraga.rxusb.nio.BulkWritableChannel
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer

/**
 * Date: 23/06/2017
 * Time: 15:15
 */
internal object Main {
    val log = LoggerFactory.getLogger(Main::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
        val usbManager = UsbService.instance

        val device = usbManager.findDevice(0x1a86, 0x7523)

        val deviceConnection = usbManager.openDevice(device)

        val interfaceConnection = deviceConnection.claimInterface(device.getInterface(0))

        val bulkEndpoint = interfaceConnection.open(
                device.getInterface(0).getEndpoint(0)
        ) as BulkReadableChannel

        bulkEndpoint.subscribeOn(Schedulers.io())
                .map<String> { byteArray ->
                    // Lets think that this usb device sends UTF-8 text
                    byteArray.toString(Charsets.UTF_8)
                }
                .observeOn(Schedulers.single())
                .subscribe({ text ->
                    // output incoming text
                    log.info("Incoming text $text")
                })

        val bulkOutEndpoint = interfaceConnection.open(
                interfaceConnection.usbInterface.getEndpoint(1)
        ) as BulkWritableChannel

        val text = "Hello World!"

        val textByteBuffer = ByteBuffer.allocateDirect(text.length)
        textByteBuffer.put(text.toByteArray())

        bulkOutEndpoint.send(textByteBuffer)
                .subscribe ({
                    log.info("Data successfully sent")
                },{
                    log.error("Error: {}", it)
                })

    }
}
