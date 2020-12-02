package net.eraga.rxusb.examples.ubloxgps

import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import net.eraga.rxusb.examples.ubloxgps.data.Location
import net.eraga.rxusb.nio.RxReadableByteChannel
import java.util.concurrent.TimeUnit


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

        val asd = ObservableOnSubscribe<Location> { e ->
            emitter = e
            println("emitter")
        }


        sharedObservable = Observable.create<Location>(asd).share()


        sharedObservable.subscribe()
    }



    private fun freshData(string: String) {

        if (string.isNotEmpty()) {
//            print(string)
            if (string.startsWith("\$GPGGA")) {
                val location = string.toLocation()
                try {
                    if (location != null)
                        println(location)
//                        emitter.onNext(location)
                } catch (e: UninitializedPropertyAccessException) {
//                    println("emitter not initialized")
                }

            }

        } else
            println(string)
    }

    private fun String.toLocation(): Location? {
        try {
            val values = this.split(",")

            var lat = values[2].substring(0,2).toDouble() + values[2].substring(2).toDouble()/60
            if (values[3] == "S")
                lat *= -1

            var lon = values[4].substring(0,3).toDouble() + values[4].substring(3).toDouble()/60
            if (values[5] == "W")
                lon *= -1

            val alt: Double = values[9].toDouble()
            /**
             * Geoid (in most cases surface) separation:
             * difference between geoid and mean sea level. In meters.
             */
            val sep: Double = values[11].toDouble()
            val hdop: Double = values[8].toDouble()

            return Location(lat, lon, alt, sep, hdop)
        } catch (e: Exception) {
            println(e)
            Observable.interval(0,0, TimeUnit.SECONDS);
        }
        return null
    }
}
