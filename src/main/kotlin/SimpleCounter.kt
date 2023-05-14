class SimpleCounter(init: Int) : Counter {
    var value = init

    override fun dec() {
        value -= 1
    }

    override fun inc() {
        value += 1
    }

    override fun get(): Int {
        return value
    }
}