import java.util.concurrent.BlockingQueue

class CrossThreadCrdtCounterChannel(
    send: BlockingQueue<Array<Int>>,
    receiver: BlockingQueue<Array<Int>>,
): CrdtCounterChannel {
    private val mySend = send
    private val myRecv = receiver

    override fun broadCastSate(state: Array<Int>) {
        mySend.put(state)
    }

    override fun pollState(): Array<Int>? {
        return myRecv.poll()
    }

    override fun isEmpty(): Boolean {
        return myRecv.isEmpty()
    }
}