interface DistributedObject {
    fun broadcastState()

    fun receiveAndMergeState()
}