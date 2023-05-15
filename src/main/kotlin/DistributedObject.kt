interface DistributedObject {
    fun broadcastState()

    fun pollAndMergeState(): Boolean
}