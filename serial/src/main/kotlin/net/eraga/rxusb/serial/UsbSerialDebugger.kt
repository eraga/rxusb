package net.eraga.rxusb.serial

import org.slf4j.LoggerFactory


object UsbSerialDebugger {
    val log = LoggerFactory.getLogger(UsbSerialDebugger::class.java)

    fun printLogGet(src: ByteArray, verbose: Boolean) {
        log.debug("Data obtained from write buffer {}", String(src))
        if (verbose) {
            log.trace("Data obtained from write buffer {}", String(src))
            log.trace("Raw data from write buffer {}", HexData.hexToString(src))
            log.trace("Number of bytes obtained from write buffer {}", src.size)
        }
    }

    fun printLogPut(src: ByteArray, verbose: Boolean) {
        log.info("Data obtained pushed to write buffer {}", String(src))

        if (verbose) {
            log.trace("Data obtained pushed to write buffer {}", String(src))
            log.trace("Raw data pushed to write buffer {}", HexData.hexToString(src))
            log.trace("Number of bytes pushed from write buffer {}", src.size)
        }
    }

    fun printReadLogGet(src: ByteArray, verbose: Boolean) {
        log.info("Data obtained from Read buffer {}", String(src))
        if (verbose) {
            log.trace("Data obtained from Read buffer {}", String(src))
            log.trace("Raw data from Read buffer {}", HexData.hexToString(src))
            log.trace("Number of bytes obtained from Read buffer {}", src.size)
        }
    }

    fun printReadLogPut(src: ByteArray, verbose: Boolean) {
        log.info("Data obtained pushed to read buffer {}", String(src))
        if (verbose) {
            log.trace("Data obtained pushed to read buffer {}", String(src))
            log.trace("Raw data pushed to read buffer {}", HexData.hexToString(src))
            log.trace("Number of bytes pushed from read buffer {}", src.size)
        }
    }


}
