/*
 * Based in the CH340x driver made by Andreas Butti (https://github.com/mik3y/usb-serial-for-android/blob/master/usbSerialForAndroid/src/main/java/com/hoho/android/usbserial/driver/Ch34xSerialDriver.java)
 * Thanks to Paul Alcock for provide me with one of those Arduino nano clones!!!
 * Also thanks to Lex Wernars for send me a CH340 that didnt work with the former version of this code!!
 * */

package net.eraga.rxusb.serial

import net.eraga.rxusb.UsbConstants
import net.eraga.rxusb.UsbInterface
import net.eraga.rxusb.UsbInterfaceConnection
import net.eraga.rxusb.nio.BulkReadableChannel
import net.eraga.rxusb.nio.BulkWritableChannel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.experimental.and


class CH34xSerialDevice(connection: UsbInterfaceConnection) : UsbSerialDevice(connection) {
    private val log: Logger = LoggerFactory.getLogger(CH34xSerialDevice::class.java)

    // XON/XOFF doesnt appear to be supported directly from hardware


    private lateinit var mInterface: UsbInterface
//    private var inEndpoint: UsbEndpoint? = null
//    private var outEndpoint: UsbEndpoint? = null
//    private var requestIN: UsbRequest? = null

    private var flowControlThread: FlowControlThread? = null
    private var ctsCallback: UsbSerial.UsbCTSCallback? = null
    private var dsrCallback: UsbSerial.UsbDSRCallback? = null
    private var rtsCtsEnabled: Boolean = false
    private var dtrDsrEnabled: Boolean = false
    private var dtr = false
    private var rts = false
    private var ctsState = false
    private var dsrState = false


    override fun open(): Boolean {
        val ret = openCH34X()
        if (ret) {
            // Initialize UsbRequest
//            requestIN = UsbRequest()
//            requestIN!!.initialize(connection, inEndpoint)

            // Restart the working thread if it has been killed before and  get and claim interface
            restartWorkingThread()
            restartWriteThread()

            // Create Flow control thread but it will only be started if necessary
            createFlowControlThread()

            asyncMode = true

            return true
        } else {
            return false
        }
    }

    override fun close() {
        killWorkingThread()
        killWriteThread()
        stopFlowControlThread()
        connection.close()
    }

    override fun syncOpen(): Boolean {
        val ret = openCH34X()
        if (ret) {
            // Create Flow control thread but it will only be started if necessary
            createFlowControlThread()
//            setSyncParams(inEndpoint!!, outEndpoint!!)
            asyncMode = false
            return true
        } else {
            return false
        }
    }

    override fun syncClose() {
        stopFlowControlThread()
        connection.close()
    }

    override fun setBaudRate(baudRate: Int) {
        if (baudRate <= 300) {
            val ret = setBaudRate(CH34X_300_1312, CH34X_300_0f2c) //300
            if (ret == -1)
                log.info("SetBaudRate failed!")
        } else if (baudRate in 301..600) {
            val ret = setBaudRate(CH34X_600_1312, CH34X_600_0f2c) //600
            if (ret == -1)
                log.info("SetBaudRate failed!")

        } else if (baudRate in 601..1200) {
            val ret = setBaudRate(CH34X_1200_1312, CH34X_1200_0f2c) //1200
            if (ret == -1)
                log.info("SetBaudRate failed!")
        } else if (baudRate in 1201..2400) {
            val ret = setBaudRate(CH34X_2400_1312, CH34X_2400_0f2c) //2400
            if (ret == -1)
                log.info("SetBaudRate failed!")
        } else if (baudRate in 2401..4800) {
            val ret = setBaudRate(CH34X_4800_1312, CH34X_4800_0f2c) //4800
            if (ret == -1)
                log.info("SetBaudRate failed!")
        } else if (baudRate in 4801..9600) {
            val ret = setBaudRate(CH34X_9600_1312, CH34X_9600_0f2c) //9600
            if (ret == -1)
                log.info("SetBaudRate failed!")
        } else if (baudRate in 9601..19200) {
            val ret = setBaudRate(CH34X_19200_1312, CH34X_19200_0f2c_rest) //19200
            if (ret == -1)
                log.info("SetBaudRate failed!")
        } else if (baudRate in 19201..38400) {
            val ret = setBaudRate(CH34X_38400_1312, CH34X_19200_0f2c_rest) //38400
            if (ret == -1)
                log.info("SetBaudRate failed!")
        } else if (baudRate in 38401..57600) {
            val ret = setBaudRate(CH34X_57600_1312, CH34X_19200_0f2c_rest) //57600
            if (ret == -1)
                log.info("SetBaudRate failed!")
        } else if (baudRate in 57601..115200) //115200
        {
            val ret = setBaudRate(CH34X_115200_1312, CH34X_19200_0f2c_rest)
            if (ret == -1)
                log.info("SetBaudRate failed!")
        } else if (baudRate in 115201..230400) //230400
        {
            val ret = setBaudRate(CH34X_230400_1312, CH34X_19200_0f2c_rest)
            if (ret == -1)
                log.info("SetBaudRate failed!")
        } else if (baudRate in 230401..460800) //460800
        {
            val ret = setBaudRate(CH34X_460800_1312, CH34X_19200_0f2c_rest)
            if (ret == -1)
                log.info("SetBaudRate failed!")
        } else if (baudRate in 460801..921600) {
            val ret = setBaudRate(CH34X_921600_1312, CH34X_19200_0f2c_rest)
            if (ret == -1)
                log.info("SetBaudRate failed!")
        }
    }

    override fun setDataBits(dataBits: Int) {
        // TODO Auto-generated method stub

    }

    override fun setStopBits(stopBits: Int) {
        // TODO Auto-generated method stub

    }

    override fun setParity(parity: Int) {
        when (parity) {
            UsbSerial.PARITY_NONE -> setCh340xParity(CH34X_PARITY_NONE)
            UsbSerial.PARITY_ODD -> setCh340xParity(CH34X_PARITY_ODD)
            UsbSerial.PARITY_EVEN -> setCh340xParity(CH34X_PARITY_EVEN)
            UsbSerial.PARITY_MARK -> setCh340xParity(CH34X_PARITY_MARK)
            UsbSerial.PARITY_SPACE -> setCh340xParity(CH34X_PARITY_SPACE)
            else -> {
            }
        }
    }

    override fun setFlowControl(flowControl: Int) {
        when (flowControl) {
            UsbSerial.FLOW_CONTROL_OFF -> {
                rtsCtsEnabled = false
                dtrDsrEnabled = false
                setCh340xFlow(CH34X_FLOW_CONTROL_NONE)
            }
            UsbSerial.FLOW_CONTROL_RTS_CTS -> {
                rtsCtsEnabled = true
                dtrDsrEnabled = false
                setCh340xFlow(CH34X_FLOW_CONTROL_RTS_CTS)
                ctsState = checkCTS()
                startFlowControlThread()
            }
            UsbSerial.FLOW_CONTROL_DSR_DTR -> {
                rtsCtsEnabled = false
                dtrDsrEnabled = true
                setCh340xFlow(CH34X_FLOW_CONTROL_DSR_DTR)
                dsrState = checkDSR()
                startFlowControlThread()
            }
            else -> {
            }
        }
    }

    override fun setRTS(state: Boolean) {
        rts = state
        writeHandshakeByte()
    }

    override fun setDTR(state: Boolean) {
        dtr = state
        writeHandshakeByte()
    }

    override fun getCTS(ctsCallback: UsbSerial.UsbCTSCallback) {
        this.ctsCallback = ctsCallback
    }

    override fun getDSR(dsrCallback: UsbSerial.UsbDSRCallback) {
        this.dsrCallback = dsrCallback
    }

    override fun getBreak(breakCallback: UsbSerial.UsbBreakCallback) {
        //TODO
    }

    override fun getFrame(frameCallback: UsbSerial.UsbFrameCallback) {
        //TODO
    }

    override fun getOverrun(overrunCallback: UsbSerial.UsbOverrunCallback) {
        //TODO
    }

    override fun getParity(parityCallback: UsbSerial.UsbParityCallback) {
        //TODO
    }

    private fun openCH34X(): Boolean {
        // Assign endpoints
        for (endpoint in mInterface.endpoints) {
            if (endpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK && endpoint.getDirection() == UsbConstants.USB_DIR_IN) {
                inEndpoint = connection.open(endpoint, USB_TIMEOUT.toLong()) as BulkReadableChannel
            } else if (endpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK && endpoint.getDirection() == UsbConstants.USB_DIR_OUT) {
                outEndpoint = connection.open(endpoint, USB_TIMEOUT.toLong()) as BulkWritableChannel
            }
        }

        return init() == 0
    }

    private fun init(): Int {
        /*
            Init the device at 9600 bauds
         */

        if (setControlCommandOut(0xa1, 0xc29c, 0xb2b9, null) < 0) {
            log.info("init failed! #1")
            return -1
        }

        if (setControlCommandOut(0xa4, 0xdf, 0, null) < 0) {
            log.info("init failed! #2")
            return -1
        }

        if (setControlCommandOut(0xa4, 0x9f, 0, null) < 0) {
            log.info("init failed! #3")
            return -1
        }

        if (checkState("init #4", 0x95, 0x0706, intArrayOf(0x9f, 0xee)) == -1)
            return -1

        if (setControlCommandOut(0x9a, 0x2727, 0x0000, null) < 0) {
            log.info("init failed! #5")
            return -1
        }

        if (setControlCommandOut(0x9a, 0x1312, 0xb282, null) < 0) {
            log.info("init failed! #6")
            return -1
        }

        if (setControlCommandOut(0x9a, 0x0f2c, 0x0008, null) < 0) {
            log.info("init failed! #7")
            return -1
        }

        if (setControlCommandOut(0x9a, 0x2518, 0x00c3, null) < 0) {
            log.info("init failed! #8")
            return -1
        }

        if (checkState("init #9", 0x95, 0x0706, intArrayOf(0x9f, 0xee)) == -1)
            return -1

        if (setControlCommandOut(0x9a, 0x2727, 0x0000, null) < 0) {
            log.info("init failed! #10")
            return -1
        }

        return 0
    }

    private fun setBaudRate(index1312: Int, index0f2c: Int): Int {
        if (setControlCommandOut(CH341_REQ_WRITE_REG, 0x1312, index1312, null) < 0)
            return -1
        if (setControlCommandOut(CH341_REQ_WRITE_REG, 0x0f2c, index0f2c, null) < 0)
            return -1
        if (checkState("set_baud_rate", 0x95, 0x0706, intArrayOf(0x9f, 0xee)) == -1)
            return -1
        if (setControlCommandOut(CH341_REQ_WRITE_REG, 0x2727, 0, null) < 0)
            return -1
        return 0
    }

    private fun setCh340xParity(indexParity: Int): Int {
        if (setControlCommandOut(CH341_REQ_WRITE_REG, 0x2518, indexParity, null) < 0)
            return -1
        if (checkState("set_parity", 0x95, 0x0706, intArrayOf(0x9f, 0xee)) == -1)
            return -1
        if (setControlCommandOut(CH341_REQ_WRITE_REG, 0x2727, 0, null) < 0)
            return -1
        return 0
    }

    private fun setCh340xFlow(flowControl: Int): Int {
        if (checkState("set_flow_control", 0x95, 0x0706, intArrayOf(0x9f, 0xee)) == -1)
            return -1
        if (setControlCommandOut(CH341_REQ_WRITE_REG, 0x2727, flowControl, null) == -1)
            return -1
        return 0
    }

    private fun checkState(msg: String, request: Int, value: Int, expected: IntArray): Int {
        val buffer = ByteArray(expected.size)
        val ret = setControlCommandIn(request, value, 0, buffer)

        if (ret != expected.size) {
            log.info("Expected {} bytes, but get {} [{}]", expected.size, ret, msg)
            return -1
        } else {
            return 0
        }
    }

    private fun checkCTS(): Boolean {
        val buffer = ByteArray(2)
        val ret = setControlCommandIn(CH341_REQ_READ_REG, 0x0706, 0, buffer)

        if (ret != 2) {
            log.info("Expected " + "2" + " bytes, but get " + ret)
            return false
        }

        return buffer[0].and(0x01) == 0x00.toByte()
    }

    private fun checkDSR(): Boolean {
        val buffer = ByteArray(2)
        val ret = setControlCommandIn(CH341_REQ_READ_REG, 0x0706, 0, buffer)

        if (ret != 2) {
            log.info("Expected 2 bytes, but get {}", ret)
            return false
        }

        return buffer[0].and(0x02) == 0x00.toByte()
    }

    private fun writeHandshakeByte(): Int {
        if (setControlCommandOut(0xa4, ((if (dtr) 1 shl 5 else 0) or if (rts) 1 shl 6 else 0).inv(), 0, null) < 0) {
            log.info("Failed to set handshake byte")
            return -1
        }
        return 0
    }

    private fun setControlCommandOut(request: Int, value: Int, index: Int, data: ByteArray?): Int {
        var dataLength = 0
        val dataToSend: ByteArray
        if (data != null) {
            dataLength = data.size
            dataToSend = data
        } else {
            dataToSend = ByteArray(dataLength)
        }
        val response = connection.deviceConnection.controlTransfer(
                REQTYPE_HOST_TO_DEVICE, request, value, index,
                dataToSend, dataLength, UsbSerialDevice.USB_TIMEOUT)
        log.info("Control Transfer Response (TO DEVICE): {}", response.toString())
        return response
    }

    private fun setControlCommandIn(request: Int, value: Int, index: Int, data: ByteArray?): Int {
        var dataLength = 0
        val dataToSend: ByteArray
        if (data != null) {
            dataLength = data.size
            dataToSend = data
        } else {
            dataToSend = ByteArray(dataLength)
        }
        val response = connection.deviceConnection.controlTransfer(
                REQTYPE_HOST_FROM_DEVICE, request, value, index,
                dataToSend, dataLength, UsbSerialDevice.USB_TIMEOUT)
        log.info("Control Transfer Response (TO DEVICE): {}", response.toString())
        return response
    }

    private fun createFlowControlThread() {
        flowControlThread = FlowControlThread()
    }

    private fun startFlowControlThread() {
        if (!flowControlThread!!.isAlive)
            flowControlThread!!.start()
    }

    private fun stopFlowControlThread() {
        if (flowControlThread != null) {
            flowControlThread!!.stopThread()
            flowControlThread = null
        }
    }

    private inner class FlowControlThread : Thread() {
        private val time: Long = 100 // 100ms

        private var firstTime: Boolean = false

        private val keep: AtomicBoolean

        init {
            keep = AtomicBoolean(true)
            firstTime = true
        }

        override fun run() {
            while (keep.get()) {
                if (!firstTime) {
                    // Check CTS status
                    if (rtsCtsEnabled) {
                        val cts = pollForCTS()
                        if (ctsState != cts) {
                            ctsState = !ctsState
                            if (ctsCallback != null)
                                ctsCallback!!.onCTSChanged(ctsState)
                        }
                    }

                    // Check DSR status
                    if (dtrDsrEnabled) {
                        val dsr = pollForDSR()
                        if (dsrState != dsr) {
                            dsrState = !dsrState
                            if (dsrCallback != null)
                                dsrCallback!!.onDSRChanged(dsrState)
                        }
                    }
                } else {
                    if (rtsCtsEnabled && ctsCallback != null)
                        ctsCallback!!.onCTSChanged(ctsState)

                    if (dtrDsrEnabled && dsrCallback != null)
                        dsrCallback!!.onDSRChanged(dsrState)

                    firstTime = false
                }
            }
        }

        fun stopThread() {
            keep.set(false)
        }

        private val lock = java.lang.Object()

        fun pollForCTS(): Boolean {
            synchronized(this) {
                try {
                    lock.wait(time)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }

            return checkCTS()
        }

        fun pollForDSR(): Boolean {
            synchronized(this) {
                try {
                    lock.wait(time)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }

            return checkDSR()
        }
    }

    companion object {
        private val DEFAULT_BAUDRATE = 9600

        private val REQTYPE_HOST_FROM_DEVICE = UsbConstants.USB_TYPE_VENDOR or UsbConstants.USB_DIR_IN
        private val REQTYPE_HOST_TO_DEVICE = 0x40

        private val CH341_REQ_WRITE_REG = 0x9A
        private val CH341_REQ_READ_REG = 0x95
        private val CH341_REG_BREAK1 = 0x05
        private val CH341_REG_BREAK2 = 0x18
        private val CH341_NBREAK_BITS_REG1 = 0x01
        private val CH341_NBREAK_BITS_REG2 = 0x40

        // Baud rates values
        private val CH34X_300_1312 = 0xd980
        private val CH34X_300_0f2c = 0xeb

        private val CH34X_600_1312 = 0x6481
        private val CH34X_600_0f2c = 0x76

        private val CH34X_1200_1312 = 0xb281
        private val CH34X_1200_0f2c = 0x3b

        private val CH34X_2400_1312 = 0xd981
        private val CH34X_2400_0f2c = 0x1e

        private val CH34X_4800_1312 = 0x6482
        private val CH34X_4800_0f2c = 0x0f

        private val CH34X_9600_1312 = 0xb282
        private val CH34X_9600_0f2c = 0x08

        private val CH34X_19200_1312 = 0xd982
        private val CH34X_19200_0f2c_rest = 0x07

        private val CH34X_38400_1312 = 0x6483

        private val CH34X_57600_1312 = 0x9883

        private val CH34X_115200_1312 = 0xcc83

        private val CH34X_230400_1312 = 0xe683

        private val CH34X_460800_1312 = 0xf383

        private val CH34X_921600_1312 = 0xf387

        // Parity values
        private val CH34X_PARITY_NONE = 0xc3
        private val CH34X_PARITY_ODD = 0xcb
        private val CH34X_PARITY_EVEN = 0xdb
        private val CH34X_PARITY_MARK = 0xeb
        private val CH34X_PARITY_SPACE = 0xfb

        //Flow control values
        private val CH34X_FLOW_CONTROL_NONE = 0x0000
        private val CH34X_FLOW_CONTROL_RTS_CTS = 0x0101
        private val CH34X_FLOW_CONTROL_DSR_DTR = 0x0202
    }

    init {
        rtsCtsEnabled = false
        dtrDsrEnabled = false
        mInterface = connection.usbInterface
    }
}
