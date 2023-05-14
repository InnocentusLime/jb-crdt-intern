import kotlin.math.max

interface CrdtCounterChannel {
    fun pollState(): Array<Int>
    fun broadCastSate(state: Array<Int>)
}

class CrdtGrowingCounter(
    channel: CrdtCounterChannel,
    processCount: Int,
    thisProcess: Int,
): GrowingCounter, DistributedObject {
    private val myChannel = channel
    private val myId = thisProcess
    private val myState: Array<Int> = Array(processCount) { _ -> 0 }

    override fun get(): Int {
        return myState.sumOf { x -> x }
    }

    override fun inc() {
        myState[myId] += 1
    }

    override fun broadcastState() {
        myChannel.broadCastSate(myState.clone())
    }

    override fun receiveAndMergeState() {
        val otherState = myChannel.pollState()

        for (idx in 0..otherState.size) {
            myState[idx] = max(myState[idx], otherState[idx])
        }
    }
}