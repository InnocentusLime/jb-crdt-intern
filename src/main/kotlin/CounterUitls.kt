import kotlin.math.max

fun mergeCounterStates(dest: Array<Int>, src: Array<Int>) {
    for (idx in src.indices) {
        dest[idx] = max(dest[idx], src[idx])
    }
}
