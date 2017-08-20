package net.eraga.rxusb.nio

import io.reactivex.Completable
import java.nio.ByteBuffer
import java.nio.channels.WritableByteChannel

/**
 * Date: 21/08/2017
 * Time: 00:00
 */
abstract class RxWritableByteChannel : WritableByteChannel{
    fun send(data: ByteBuffer): Completable {
        return Completable.create({
            try {
                write(data)
                it.onComplete()
            } catch (e: Exception) {
                it.onError(e)
            }
        })
    }
}
