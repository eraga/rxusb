package net.eraga.rxusb.examples.ubloxgps.data

/**
 * Date: 23/04/2017
 * Time: 19:52
 */
data class Satellite(
        val elevation: Int,
        val azimuth: Int,
        val signal: Int? = null
)
