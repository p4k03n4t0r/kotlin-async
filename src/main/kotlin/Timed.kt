import kotlin.system.measureTimeMillis

fun time(totalWork: Long, name: String, call: (Long) -> Unit): Result {
    val elapsed = measureTimeMillis {
        call.invoke(totalWork)
    }
    return Result(name, elapsed, -1L) // -1 since we don't know what's returned
}

fun timeWithReturn(totalWork: Long, name: String, call: (Long) -> Long): Result {
    val returned: Long
    val elapsed = measureTimeMillis {
        returned = call.invoke(totalWork)
    }
    return Result(name, elapsed, returned)
}
