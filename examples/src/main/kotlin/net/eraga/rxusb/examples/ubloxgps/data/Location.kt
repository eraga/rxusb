package net.eraga.rxusb.examples.ubloxgps.data

/**
 * Date: 23/04/2017
 * Time: 18:47
 */
data class Location(
        /**
         * Latitude, positive is North
         */
        val lat: Double,
        /**
         * Latitude, positive is East
         */
        val lon: Double,
        /**
         * Altitude above mean sea level. In meters.
         */
        val altitude: Double? = null,
        /**
         * Geoid (in most cases surface) separation:
         * difference between geoid and mean sea level. In meters.
         */
        var separation: Double? = null,
        /**
         * Horizontal Dilution of Precision
         */
        val hdop: Double? = null,
        /**
         * Vertical Dilution of Precision
         */
        val vdop: Double? = null,
        /**
         * Accuracy calculated based on various satellite parameters
         */
        val accuracy: Double? = null
)
