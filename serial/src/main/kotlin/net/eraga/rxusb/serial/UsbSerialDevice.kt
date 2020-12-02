package net.eraga.rxusb.serial

import net.eraga.rxusb.*
import net.eraga.rxusb.exceptions.UsbException
import net.eraga.rxusb.nio.BulkReadableChannel
import net.eraga.rxusb.nio.BulkWritableChannel
import net.eraga.rxusb.serial.UsbSerial.UsbReadCallback
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.atomic.AtomicBoolean

//todo rename to rxusb serial interface
abstract class UsbSerialDevice(
        protected val connection: UsbInterfaceConnection) : UsbSerial {

    private val log: Logger = LoggerFactory.getLogger(UsbSerialDevice::class.java)

    protected lateinit var serialBuffer: SerialBuffer

    //    protected var workerThread: WorkerThread? = null
    protected lateinit var writeThread: WriteThread
    protected lateinit var readThread: ReadThread

    // Endpoints for synchronous read and write operations
    lateinit var inEndpoint: BulkReadableChannel
    lateinit var outEndpoint: BulkWritableChannel

    protected var asyncMode: Boolean = false

    init {
        this.asyncMode = true
        serialBuffer = SerialBuffer()
    }

    // Common Usb Serial Operations (I/O Asynchronous)
    abstract override fun open(): Boolean

    override fun write(buffer: ByteArray) {
        if (asyncMode)
            serialBuffer.putWriteBuffer(buffer)
    }

    override fun read(mCallback: UsbReadCallback): Int {
        if (!asyncMode)
            return -1


        readThread.setCallback(mCallback)
        return 0
    }


    abstract override fun close()

    // Common Usb Serial Operations (I/O Synchronous)
    abstract override fun syncOpen(): Boolean

    abstract override fun syncClose()

    override fun syncWrite(buffer: ByteArray, timeout: Int): Int {
        return if (!asyncMode) {
            outEndpoint.write(ByteBuffer.wrap(buffer))
        } else {
            -1
        }
    }

    override fun syncRead(buffer: ByteArray, timeout: Int): Int {
        if (asyncMode) {
            return -1
        }
        val byteBuffer = ByteBuffer.allocate(buffer.size)
        val res = inEndpoint.read(byteBuffer)

        byteBuffer.get(buffer)

        return res
    }

    // Serial port configuration
    abstract override fun setBaudRate(baudRate: Int)

    abstract override fun setDataBits(dataBits: Int)
    abstract override fun setStopBits(stopBits: Int)
    abstract override fun setParity(parity: Int)
    abstract override fun setFlowControl(flowControl: Int)

    //Debug options
    fun debug(value: Boolean) {
        serialBuffer.debug(value)
    }

    private val isFTDIDevice: Boolean
        get() = false
//        get() = this is FTDISerialDevice


    /*
     * WorkerThread waits for request notifications from IN endpoint
     */
//    protected inner class WorkerThread(private val usbSerialDevice: UsbSerialDevice) : Thread() {
//
//        private var callback: UsbReadCallback? = null
//        var usbRequest: UsbRequest? = null
//        private val working: AtomicBoolean
//
//        init {
//            working = AtomicBoolean(true)
//        }
//
//        override fun run() {
//            while (working.get()) {
//                val request = connection.requestWait()
//                if (request != null && request!!.getEndpoint().getType() == UsbConstants.USB_ENDPOINT_XFER_BULK
//                        && request!!.getEndpoint().getDirection() == UsbConstants.USB_DIR_IN) {
//                    var data = serialBuffer!!.dataReceived
//
//                    // FTDI devices reserves two first bytes of an IN endpoint with info about
//                    // modem and Line.
//                    if (isFTDIDevice) {
////                        TODO
////                        (usbSerialDevice as FTDISerialDevice).ftdiUtilities.checkModemStatus(data) //Check the Modem status
////                        serialBuffer!!.clearReadBuffer()
////
////                        if (data.size > 2) {
////                            data = (usbSerialDevice as FTDISerialDevice).ftdiUtilities.adaptArray(data)
////                            onReceivedData(data)
////                        }
//                    } else {
//                        // Clear buffer, execute the callback
//                        serialBuffer!!.clearReadBuffer()
//                        onReceivedData(data)
//                    }
//                    // Queue a new request
//                    usbRequest!!.queue(serialBuffer!!.getReadBuffer(), SerialBuffer.DEFAULT_READ_BUFFER_SIZE)
//                }
//            }
//        }
//
//        fun setCallback(callback: UsbReadCallback) {
//            this.callback = callback
//        }
//
//        private fun onReceivedData(data: ByteArray) {
//            if (callback != null)
//                callback!!.onReceivedData(data)
//        }
//
//        fun stopWorkingThread() {
//            working.set(false)
//        }
//    }

    protected inner class WriteThread : Thread() {
        private val working: AtomicBoolean

        init {
            working = AtomicBoolean(true)
        }

        override fun run() {
//            while (working.get()) {
//                val data = serialBuffer.getWriteBuffer()
//                outEndpoint.write(ByteBuffer.wrap(data))
//                connection.bulkTransfer(outEndpoint, data, data.size, USB_TIMEOUT)
//            }
        }

        fun stopWriteThread() {
            working.set(false)
        }
    }

    protected inner class ReadThread(private val usbSerialDevice: UsbSerialDevice) : Thread() {

        private var callback: UsbReadCallback? = null
        private val working: AtomicBoolean = AtomicBoolean(true)

        fun setCallback(callback: UsbReadCallback) {
            this.callback = callback
        }

        override fun run() {
            return
            log.info("Starting read thread")
            var dataReceived: ByteArray? = null

            val buffer = ByteBuffer.allocateDirect(1024*16).order(
                    ByteOrder.LITTLE_ENDIAN)

            while (working.get()) {
                try {
                    val array = ByteArray(inEndpoint.read(buffer))

//                    numberBytes = connection.bulkTransfer(inEndpoint, serialBuffer!!.dataReceived,
//                            SerialBuffer.DEFAULT_READ_BUFFER_SIZE, 0)
                    buffer.get(array)



                    if (array.isNotEmpty()) {

//                        val DELIMITER = '\r'.toByte()
//
//                        val lfi = array.indexOf(DELIMITER)
//
////                        log.info("{} — {} / {}", lfi, array.size, String(array))
//
//                        if (lfi == -1)
//                            continue


//                    dataReceived = serialBuffer.dataReceived

                        // TODO FTDI devices reserve two first bytes of an IN endpoint with info about
                        // modem and Line.
//                        if (isFTDIDevice) {
//                        (usbSerialDevice as FTDISerialDevice).ftdiUtilities.checkModemStatus(dataReceived)
//
//                        if (dataReceived!!.size > 2) {
//                            dataReceived = (usbSerialDevice as FTDISerialDevice).ftdiUtilities.adaptArray(dataReceived)
//                            onReceivedData(dataReceived)
//                        }
//                        }
//                        else {
                            onReceivedData(array)
                            buffer.clear()
//                        }
                    }
                } catch (ex: UsbException) {
                    log.warn(ex.message)
                }
            }
        }

        fun stopReadThread() {
            working.set(false)
        }

        private fun onReceivedData(data: ByteArray) {
            if (callback != null)
                callback!!.onReceivedData(data)
        }
    }

//    protected fun setSyncParams(inEndpoint: BulkReadableChannel, outEndpoint: BulkWritableChannel) {
//        this.inEndpoint = inEndpoint
//        this.outEndpoint = outEndpoint
//    }


    /*
     * Kill workingThread; This must be called when closing a device
     */
    protected fun killWorkingThread() {
//        if (mr1Version && workerThread != null) {
//            workerThread!!.stopWorkingThread()
//            workerThread = null
//        } else if (!mr1Version && readThread != null) {
        readThread.stopReadThread()
//        readThread = null
//        }
    }

    /*
     * Restart workingThread if it has been killed before
     */
    protected fun restartWorkingThread() {
//        if (mr1Version && workerThread == null) {
//            workerThread = WorkerThread(this)
//            workerThread!!.start()
//            while (!workerThread!!.isAlive) {
//            } // Busy waiting
//        } else if (!mr1Version && readThread == null) {
        readThread = ReadThread(this)
        readThread!!.start()
        while (!readThread!!.isAlive) {
        } // Busy waiting
//        }
    }

    protected fun killWriteThread() {
        if (writeThread != null) {
            writeThread.stopWriteThread()
//            writeThread = null
            serialBuffer.resetWriteBuffer()
        }
    }

    protected fun restartWriteThread() {
        writeThread = WriteThread()
        writeThread.start()
        while (!writeThread.isAlive) {
        } // Busy waiting
    }

    companion object {
        private val CLASS_ID = UsbSerialDevice::class.java.simpleName

//        private var mr1Version: Boolean = false

        val USB_TIMEOUT = 5000

        // Get Android version if version < 4.3 It is not going to be asynchronous read operations
        init {
//            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.JELLY_BEAN_MR1)
//                mr1Version = true
//            else
//                mr1Version = false
        }

//        @JvmOverloads
//        fun createUsbSerialDevice(device: UsbDevice, connection: UsbDeviceConnection, iface: Int = -1): UsbSerialDevice? {
//            /*
//		 * It checks given vid and pid and will return a custom driver or a CDC serial driver.
//		 * When CDC is returned open() method is even more important, its response will inform about if it can be really
//		 * opened as a serial device with a generic CDC serial driver
//		 */
//            val vid = device.vendorId
//            val pid = device.productId
//
////            if (FTDISioIds.isDeviceSupported(vid, pid))
////                return FTDISerialDevice(device, connection, iface)
////            else if (CP210xIds.isDeviceSupported(vid, pid))
////                return CP2102SerialDevice(device, connection, iface)
////            else if (PL2303Ids.isDeviceSupported(vid, pid))
////                return PL2303SerialDevice(device, connection, iface)
////            else
//                if (CH34xIds.isDeviceSupported(vid, pid))
//                return CH34xSerialDevice(device, connection, iface)
//            else if (isCdcDevice(device))
//                return CDCSerialDevice(device, connection, iface)
//            else
//                return null
//        }

//        fun isSupported(device: UsbDevice): Boolean {
//            val vid = device.vendorId
//            val pid = device.productId
//
////            if (FTDISioIds.isDeviceSupported(vid, pid))
////                return true
////            else if (CP210xIds.isDeviceSupported(vid, pid))
////                return true
////            else if (PL2303Ids.isDeviceSupported(vid, pid))
////                return true
////            else if (CH34xIds.isDeviceSupported(vid, pid))
////                return true
////            else
//                if (isCdcDevice(device))
//                return true
//            else
//                return false
//        }

//        fun isCdcDevice(device: UsbDevice): Boolean {
//            val iIndex = device.getInterfaceCount()
//            for (i in 0..iIndex - 1) {
//                val iface = device.getInterface(i)
//                if (iface.interfaceClass == UsbConstants.USB_CLASS_CDC_DATA)
//                    return true
//            }
//            return false
//        }
    }
}
