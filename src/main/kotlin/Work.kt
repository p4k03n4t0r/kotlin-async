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

fun workShared(max: Long, sharedClass: SharedClass) {
    for (i in 1..max) {
        sharedClass.increment()
    }
}

suspend fun workAsync(max: Long): Deferred<Long> = coroutineScope {
    async {
        work(max)
    }
}

suspend fun workSharedAsync(max: Long, sharedClass: SharedClass): Deferred<Unit> = coroutineScope {
    async {
        workShared(max, sharedClass)
    }
}

class SharedClass constructor(var counter: Long = 0L) {
    fun increment() {
        counter++
    }
}