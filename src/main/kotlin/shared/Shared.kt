package shared

import kotlin.system.measureTimeMillis

fun main() {
    val counter = Counter()

    val elapsed = measureTimeMillis {
        work(100000000000, counter)
    }

    println("$elapsed ms")
    println("Counter ${counter.total}")
}

fun work(max: Long, counter: Counter) {
    for(i in 1 .. max) {
        counter.increment()
    }
}

class Counter {
    var total = 0

    fun increment() {
        total++
    }
}