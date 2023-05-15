interface CrdtCounterChannel {
    fun pollState(): Array<Int>?
    fun broadCastSate(state: Array<Int>)
    fun isEmpty(): Boolean
}