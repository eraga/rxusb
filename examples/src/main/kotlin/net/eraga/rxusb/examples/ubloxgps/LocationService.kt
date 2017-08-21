package net.eraga.rxusb.examples.ubloxgps

import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import net.eraga.rxusb.examples.ubloxgps.data.Location
import net.eraga.rxusb.nio.RxReadableByteChannel


class LocationService(
        sourceChannel: RxReadableByteChannel
) {

    private lateinit var emitter: ObservableEmitter<Location>
    private var sharedObservable: Observable<Location>

    fun sharedObservable() = sharedObservable

    init {
        sourceChannel.subscribe { array ->
            freshData(String(array, Charsets.US_ASCII))
        }

        sharedObservable = Observable.create<Location>({
            emitter = it
        }).share()

    }



    private fun freshData(string: String) {

        if (string.isNotEmpty()) {
            print(string)
            if (string.startsWith("\$GPGGA")) {
                val location = string.toLocation()
                if (location != null)
                    emitter.onNext(location)
            }

        } else
            println(string)
    }

    private fun String.toLocation(): Location? {
        try {
            val values = this.split(",")

            var lat = values.get(2).substring(0,2).toDouble() + values.get(2).substring(2).toDouble()/60
            if (values.get(3) == "S")
                lat *= -1

            var lon = values.get(4).substring(0,3).toDouble() + values.get(4).substring(3).toDouble()/60
            if (values.get(5) == "W")
                lon *= -1

            val alt: Double = values.get(9).toDouble()
            /**
             * Geoid (in most cases surface) separation:
             * difference between geoid and mean sea level. In meters.
             */
            val sep: Double = values.get(11).toDouble()
            val hdop: Double = values.get(8).toDouble()

            return Location(lat, lon, alt, sep, hdop)
        } catch (e: Exception) {
            println(e)
        }
        return null
    }
}
