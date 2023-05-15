import org.junit.jupiter.api.Test
import java.util.concurrent.ArrayBlockingQueue
import kotlin.test.assertEquals

class SingleThreaded {
    @Test
    fun parityWithSimpleCounter() {
        val dummy = ArrayBlockingQueue<Array<Int>>(1)
        val count1: CompleteCounter = SimpleCompleteCounter(0)
        val count2: CompleteCounter = CrdtCompleteCounter(
            CrossThreadCrdtCounterChannel(dummy, dummy),
            CrossThreadCrdtCounterChannel(dummy, dummy),
            3,
            0,
        )

        count1.inc()
        count2.inc()
        assertEquals(count1.get(), count2.get())

        count1.inc()
        count2.inc()
        assertEquals(count1.get(), count2.get())

        count1.dec()
        count2.dec()
        assertEquals(count1.get(), count2.get())

        count1.inc()
        count2.inc()
        assertEquals(count1.get(), count2.get())
    }

    @Test
    fun basicReproduction() {
        val (count1, count2) = setupCounters()

        // Count 1 does some work
        count1.inc()
        count1.inc()

        // It broadcasts its state and count 2 fetches it
        count1.broadcastState()
        count2.receiveAndMergeState()

        // count 2 is now supposed to have the same value
        assertEquals(count1.get(), count2.get())
    }

    @Test
    fun temporaryPartition() {
        val (count1, count2) = setupCounters()

        // Count 1 does some work
        count1.inc()
        count1.inc()

        // It broadcasts its state and count 2 fetches it
        count1.broadcastState()
        count2.receiveAndMergeState()

        // Then let's assume that the connection dies and both counters do some work
        count1.inc()
        count2.dec()

        // Now the counters exchange with their states
        count1.broadcastState()
        count2.broadcastState()
        count1.receiveAndMergeState()
        count2.receiveAndMergeState()

        // Their states must be both 2
        assertEquals(2, count1.get())
        assertEquals(2, count2.get())
    }

    private fun setupCounters(): Pair<CrdtCompleteCounter, CrdtCompleteCounter> {
        val queue1D = ArrayBlockingQueue<Array<Int>>(3)
        val queue2D = ArrayBlockingQueue<Array<Int>>(3)
        val queue1I = ArrayBlockingQueue<Array<Int>>(3)
        val queue2I = ArrayBlockingQueue<Array<Int>>(3)


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