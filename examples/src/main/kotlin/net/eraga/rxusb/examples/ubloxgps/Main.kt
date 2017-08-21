package net.eraga.rxusb.examples.ubloxgps

import net.eraga.rxusb.UsbConstants
import net.eraga.rxusb.UsbService
import net.eraga.rxusb.exceptions.UsbEntityNotFound
import net.eraga.rxusb.nio.BulkReadableChannel
import org.slf4j.LoggerFactory

/**
 * Date: 21/08/2017
 * Time: 04:13
 */
internal object Main {
    val log = LoggerFactory.getLogger(Main::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
        val usbManager = UsbService.instance

        try {
            // Find UBLOX GPS USB device
            val device = usbManager.findDevice(0x1546, 0x01a7)

            val deviceConnection = usbManager.openDevice(device)

            val ifaceConnection = deviceConnection.claimInterface(device.interfaces[1])

            var ubloxLocation: BulkReadableChannel? = null

            ifaceConnection.usbInterface.endpoints.forEach {
                if (it.getDirection() == UsbConstants.USB_DIR_IN) {
                    ubloxLocation = ifaceConnection.open(it) as BulkReadableChannel

                    return@forEach
                }
            }

            if (ubloxLocation == null) {
                log.error("No IN endpoint found for device interface. Are you sure this is correct device?")
                return
            }

            val locationService = LocationService(ubloxLocation!!)

            locationService.sharedObservable().subscribe({
                log.info("Location update: {}", it)
            })


        } catch (notFound: UsbEntityNotFound) {
            log.error("No such device {}", notFound.message)
        }
    }
}
