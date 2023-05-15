import org.junit.jupiter.api.Test
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.test.assertEquals

class Concurrent {
    @Test
    fun eventualConsistency() {
        val (count1, count2) = setupCounters()

        // We launch 2 threads. One increments the counter 100 times,
        // the other decrements. The idea is that the counter will be eventually zero
        val t1 = thread {
            for (x in 0..100) {
                count1.inc()

                if (x % 5 == 0) {
                    count1.broadcastState()
                    count1.receiveAndMergeState()
                }
            }

            for (x in 0..100) { count1.receiveAndMergeState() }
        }

        val t2 = thread {
            for (x in 0..100) {
                count2.dec()

                if (x % 20 == 0) {
                    count2.broadcastState()
                    count2.receiveAndMergeState()
                }
            }

            for (x in 0..100) { count2.receiveAndMergeState() }
        }

        // Wait for both threads to finish
        t1.join()
        t2.join()

        // Check
        assertEquals(0, count1.get())
        assertEquals(0, count2.get())
    }

    @Test
    fun eventualConsistencyPartition() {
        // We will occasionally lock one of the threads to imitate partition
        val lock = ReentrantLock()
        val (count1, count2) = setupCounters()

        // We launch 2 threads. One increments the counter 100 times,
        // the other decrements. The idea is that the counter will be eventually zero
        val t1 = thread {
            for (x in 0..100) {
                count1.inc()

                if (x % 5 == 0) {
                    count1.broadcastState()
                    count1.receiveAndMergeState()
                }

                if (x % 10 == 0) {
                    if (!lock.isLocked) {
                        lock.lock()
                    } else {
                        lock.unlock()
                    }
                }
            }

            while (count1.get() != 0 || count1.hasPendingMessages()) {
                count1.broadcastState()
                count1.receiveAndMergeState()
            }
        }

        val t2 = thread {
            for (x in 0..100) {
                count2.dec()

                if (x % 20 == 0) {
                    count2.broadcastState()
                    count2.receiveAndMergeState()
                }
            }

            while (count2.get() != 0 || count2.hasPendingMessages()) {
                count2.broadcastState()
                count2.receiveAndMergeState()
            }
        }

        // Wait for both threads to finish
        t1.join(10000)
        t2.join(10000)
    }

    private fun setupCounters(): Pair<CrdtCompleteCounter, CrdtCompleteCounter> {
        val queue1D = ArrayBlockingQueue<Array<Int>>(20)
        val queue2D = ArrayBlockingQueue<Array<Int>>(20)
        val queue1I = ArrayBlockingQueue<Array<Int>>(20)
        val queue2I = ArrayBlockingQueue<Array<Int>>(20)


        val count1 = CrdtCompleteCounter(
            CrossThreadCrdtCounterChannel(queue2I, queue1I),
            CrossThreadCrdtCounterChannel(queue2D, queue1D),
            2,
            0,
        )
        val count2 = CrdtCompleteCounter(
            CrossThreadCrdtCounterChannel(queue1I, queue2I),
            CrossThreadCrdtCounterChannel(queue1D, queue2D),
            2,
            1,
        )

        return Pair(count1, count2)
    }
}