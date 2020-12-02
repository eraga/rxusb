package net.eraga.rxusb.serial

import java.nio.BufferOverflowException
import java.nio.ByteBuffer
import java.util.Arrays

class SerialBuffer {
    private var readBuffer: ByteBuffer
    private val writeBuffer: SynchronizedBuffer

    private var debugging = false

    init {
        writeBuffer = SynchronizedBuffer()
        readBuffer = ByteBuffer.allocateDirect(DEFAULT_READ_BUFFER_SIZE)
    }

    /*
     * Print debug messages
     */
    fun debug(value: Boolean) {
        debugging = value
    }

    fun putReadBuffer(data: ByteBuffer) {
        synchronized(this) {
            try {
                readBuffer.put(data)
            } catch (e: BufferOverflowException) {
                // TO-DO
            }

        }
    }

    fun getReadBuffer(): ByteBuffer {
        synchronized(this) {
            return readBuffer
        }
    }


    val dataReceived: ByteArray
        get() = synchronized(this) {
            val dst = ByteArray(readBuffer.position())
            readBuffer.position(0)
            readBuffer.get(dst, 0, dst.size)
            if (debugging)
                UsbSerialDebugger.printReadLogGet(dst, true)
            return dst
        }

    fun clearReadBuffer() {
        synchronized(this) {
            readBuffer.clear()
        }
    }

    fun getWriteBuffer(): ByteArray {
        return writeBuffer.get()
    }

    fun putWriteBuffer(data: ByteArray) {
        writeBuffer.put(data)
    }


    fun resetWriteBuffer() {
        writeBuffer.reset()
    }


    private inner class SynchronizedBuffer {
        private val lock = java.lang.Object()

        private val buffer: ByteArray
        private var position: Int = 0

        init {
            this.buffer = ByteArray(DEFAULT_WRITE_BUFFER_SIZE)
            position = -1
        }

        @Synchronized fun put(src: ByteArray) {
            if (position == -1)
                position = 0
            if (debugging)
                UsbSerialDebugger.printLogPut(src, true)
            if (position + src.size > DEFAULT_WRITE_BUFFER_SIZE - 1)
            //Checking bounds. Source data does not fit in buffer
            {
                if (position < DEFAULT_WRITE_BUFFER_SIZE)
                    System.arraycopy(src, 0, buffer, position, DEFAULT_WRITE_BUFFER_SIZE - position)
                position = DEFAULT_WRITE_BUFFER_SIZE
                lock.notify()
            } else
            // Source data fits in buffer
            {
                System.arraycopy(src, 0, buffer, position, src.size)
                position += src.size
                lock.notify()
            }
        }

        @Synchronized fun get(): ByteArray {
            if (position == -1) {
                try {
                    lock.wait()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }
            val dst = Arrays.copyOfRange(buffer, 0, position)
            if (debugging)
                UsbSerialDebugger.printLogGet(dst, true)
            position = -1
            return dst
        }

        @Synchronized fun reset() {
            position = -1
        }
    }

    companion object {
        const val DEFAULT_READ_BUFFER_SIZE = 16 * 1024
        const val DEFAULT_WRITE_BUFFER_SIZE = 16 * 1024
    }

}
