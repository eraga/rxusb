package net.eraga.rxusb.serial

/**
 * Interface to handle a serial port
 * @author felhr (felhr85@gmail.com)
 */
interface UsbSerial {

    // Common Usb Serial Operations (I/O Asynchronous)
    fun open(): Boolean

    fun write(buffer: ByteArray)
    fun read(mCallback: UsbReadCallback): Int
    fun close()

    // Common Usb Serial Operations (I/O Synchronous)
    fun syncOpen(): Boolean

    fun syncWrite(buffer: ByteArray, timeout: Int): Int
    fun syncRead(buffer: ByteArray, timeout: Int): Int
    fun syncClose()

    // Serial port configuration
    fun setBaudRate(baudRate: Int)

    fun setDataBits(dataBits: Int)
    fun setStopBits(stopBits: Int)
    fun setParity(parity: Int)
    fun setFlowControl(flowControl: Int)

    // Flow control commands and interface callback
    fun setRTS(state: Boolean)

    fun setDTR(state: Boolean)
    fun getCTS(ctsCallback: UsbCTSCallback)
    fun getDSR(dsrCallback: UsbDSRCallback)

    // Status methods
    fun getBreak(breakCallback: UsbBreakCallback)

    fun getFrame(frameCallback: UsbFrameCallback)
    fun getOverrun(overrunCallback: UsbOverrunCallback)
    fun getParity(parityCallback: UsbParityCallback)

    interface UsbCTSCallback {
        fun onCTSChanged(state: Boolean)
    }

    interface UsbDSRCallback {
        fun onDSRChanged(state: Boolean)
    }

    // Error signals callbacks
    interface UsbBreakCallback {
        fun onBreakInterrupt()
    }

    interface UsbFrameCallback {
        fun onFramingError()
    }

    interface UsbOverrunCallback {
        fun onOverrunError()
    }

    interface UsbParityCallback {
        fun onParityError()
    }

    // Usb Read Callback
    interface UsbReadCallback {
        fun onReceivedData(data: ByteArray)
    }

    companion object {
        // Common values
        val DATA_BITS_5 = 5
        val DATA_BITS_6 = 6
        val DATA_BITS_7 = 7
        val DATA_BITS_8 = 8

        val STOP_BITS_1 = 1
        val STOP_BITS_15 = 3
        val STOP_BITS_2 = 2

        val PARITY_NONE = 0
        val PARITY_ODD = 1
        val PARITY_EVEN = 2
        val PARITY_MARK = 3
        val PARITY_SPACE = 4

        val FLOW_CONTROL_OFF = 0
        val FLOW_CONTROL_RTS_CTS = 1
        val FLOW_CONTROL_DSR_DTR = 2
        val FLOW_CONTROL_XON_XOFF = 3
    }

}
