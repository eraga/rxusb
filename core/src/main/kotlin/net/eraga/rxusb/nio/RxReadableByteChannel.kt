package net.eraga.rxusb.nio

import io.reactivex.Flowable
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer
import java.nio.channels.ReadableByteChannel
import java.util.concurrent.atomic.AtomicLong


/**
 * TODO
 */
abstract class RxReadableByteChannel(
        var byteBufferSize: Int = 1024 * 4
) : Flowable<ByteArray>(), ReadableByteChannel {
    private val log = LoggerFactory.getLogger(RxReadableByteChannel::class.java)

    class ReadBytesSubscription(
            val subscriber: Subscriber<in ByteArray>,
            val channel: RxReadableByteChannel,
            byteBufferSize: Int = 1024 * 4) : Subscription {

        private val log = LoggerFactory.getLogger(ReadBytesSubscription::class.java)


        var buffer: ByteBuffer = ByteBuffer.allocateDirect(byteBufferSize)

        init {
            log.info("init {}", channel.isSubscriptionActive)
            channel.isSubscriptionActive = true
            log.info("init {}", channel.isSubscriptionActive)
//            private AtomicLong outStandingRequests = new AtomicLong(0);
        }

        override fun cancel() {

            log.info("cancel {}", channel.isSubscriptionActive)
            channel.isSubscriptionActive = false
            log.info("cancel {}", channel.isSubscriptionActive)
            subscriber.onComplete()
        }

        val lock: Any = Any()

        @Volatile private var isProducing = false
        private val outStandingRequests = AtomicLong(0)

        override fun request(n: Long) {

            if (!channel.isOpen)
                return

            channel.isSubscriptionActive = true
//            log.info("request {}", n)

            outStandingRequests.addAndGet(n);

            while (channel.isOpen && outStandingRequests.get() > 0) {
                if (isProducing) {
                    return;
                }
                // start producing
                isProducing = true

                val size = channel.read(buffer)
                val array = ByteArray(size)

                buffer.get(array)
                buffer.clear()

                subscriber.onNext(array)
                outStandingRequests.decrementAndGet();

                isProducing = false;
            }
        }

    }

    lateinit var subscription: Subscription

    override fun subscribeActual(subscriber: Subscriber<in ByteArray>) {
        log.info("subscribeActual {}", isSubscriptionActive)
        if (!isSubscriptionActive) {
            log.info("subscribeActual {}", isSubscriptionActive)
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
