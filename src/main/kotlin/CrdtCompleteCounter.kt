class CrdtCompleteCounter(
    channelI: CrdtCounterChannel,
    channelD: CrdtCounterChannel,
    processCount: Int,
    thisProcess: Int,
): CompleteCounter, DistributedObject {
    private val growingD = CrdtGrowingCounter(channelD, processCount, thisProcess)
    private val growingI = CrdtGrowingCounter(channelI, processCount, thisProcess)

    override fun get(): Int {
        return growingI.get() - growingD.get()
    }

    override fun inc() {
        growingI.inc()
    }

    override fun dec() {
        growingD.inc()
    }

    override fun broadcastState() {
        growingD.broadcastState()
        growingI.broadcastState()
    }

    override fun receiveAndMergeState() {
        growingD.receiveAndMergeState()
        growingI.receiveAndMergeState()
    }

    fun hasPendingMessages(): Boolean {
        return growingI.hasPendingMessages() || growingD.hasPendingMessages()
    }
}