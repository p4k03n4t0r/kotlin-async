import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

fun work(max: Long): Long {
    var counter = 0L
    for (i in 1..max) {
        counter++
    }
    if (counter != max) {
        println("Counter mismatch")
    }
    return max
}

fun workShared(max: Long, sharedCounter: Counter) {
    for (i in 1..max) {
        sharedCounter.increment()
    }
}

suspend fun workAsync(max: Long): Deferred<Long> = coroutineScope {
    async {
        return@async work(max)
    }
}

suspend fun workSharedAsync(max: Long, sharedCounter: Counter): Deferred<Unit> = coroutineScope {
    async {
        workShared(max, sharedCounter)
    }
}

class Counter constructor(var total: Long = 0L) {
    fun increment() {
        total++
    }
}