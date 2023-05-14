class SimpleCompleteCounter(init: Int): CompleteCounter {
    private var inner = init

    override fun dec() {
        inner -= 1
    }

    override fun inc() {
        inner += 1
    }

    override fun get(): Int {
        return inner
    }
}