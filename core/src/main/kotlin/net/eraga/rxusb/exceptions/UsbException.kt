package net.eraga.rxusb.exceptions

/**
 * Date: 24/04/2017
 * Time: 00:56
 */
open class UsbException : Exception {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
}
