import kotlin.math.max

fun mergeCounterStates(dest: Array<Int>, src: Array<Int>) {
    for (idx in 0..src.size) {
        dest[idx] = max(dest[idx], src[idx])
    }
}
