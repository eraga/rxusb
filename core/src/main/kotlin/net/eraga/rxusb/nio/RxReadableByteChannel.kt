package net.eraga.rxusb.nio

import io.reactivex.Flowable
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.nio.ByteBuffer
import java.nio.channels.ReadableByteChannel
import java.util.concurrent.atomic.AtomicLong


/**
 * TODO
 */
abstract class RxReadableByteChannel(
        var byteBufferSize: Int = 1024 * 4
) : Flowable<ByteArray>(), ReadableByteChannel {

    class ReadBytesSubscription(
            val subscriber: Subscriber<in ByteArray>,
            val channel: RxReadableByteChannel,
            byteBufferSize: Int) : Subscription {


        var buffer: ByteBuffer = ByteBuffer.allocateDirect(byteBufferSize)

        init {
            channel.isSubscriptionActive = true
        }

        override fun cancel() {
            channel.isSubscriptionActive = false
            subscriber.onComplete()
        }

        @Volatile private var isProducing = false
        private val outStandingRequests = AtomicLong(0)

        override fun request(n: Long) {

            if (!channel.isOpen)
                return

            channel.isSubscriptionActive = true

            outStandingRequests.addAndGet(n)

            while (channel.isOpen && outStandingRequests.get() > 0) {
                if (isProducing) {
                    return
                }
                // start producing
                isProducing = true

                val size = channel.read(buffer)
                val array = ByteArray(size)

                buffer.get(array)
                buffer.clear()

                subscriber.onNext(array)
                outStandingRequests.decrementAndGet()

                isProducing = false
            }
        }

    }

    private lateinit var subscription: Subscription

    override fun subscribeActual(subscriber: Subscriber<in ByteArray>) {
        if (!isSubscriptionActive) {
            subscription = ReadBytesSubscription(subscriber, this, byteBufferSize)
        }

        subscriber.onSubscribe(subscription)
    }

    protected var isSubscriptionActive = false

    override fun isOpen(): Boolean {
        return isSubscriptionActive
    }

    @Synchronized
    override fun close() {
        subscription.cancel()
    }


}
