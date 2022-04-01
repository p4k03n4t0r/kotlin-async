package returned

import kotlinx.coroutines.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import kotlin.system.measureTimeMillis

fun main() {
    val totalWork = 10000000000L
    val threads = 10
    var actualCount = 0L

    println("SingleThreaded")
    val elapsedSt = measureTimeMillis {
        actualCount = work(totalWork)
    }
    println("$elapsedSt ms ($actualCount)")

    println("Multithreaded")
    actualCount = 0
    val elapsedMt = measureTimeMillis {
        val executor = Executors.newFixedThreadPool(threads)
        val works = mutableListOf<Callable<Long>>()
        val workPerThread = totalWork/threads
        for (i in 1..threads) {
            val worker = Callable { work(workPerThread) }
            executor.submit(worker)
            works.add(worker)
        }
        executor.shutdown()
        while (!executor.isTerminated) {
        }
        for (task in works) {
            actualCount += task.call()
        }
    }
    println("$elapsedMt ms ($actualCount)")

    println("Coroutines")
    actualCount = 0
    val elapsedCr = measureTimeMillis {
        runBlocking {
            val workPerThread = totalWork/threads
            val coroutines = mutableListOf<Deferred<Long>>()
            for (i in 1..threads) {
                val job = async {
                    return@async workAsync(workPerThread).await()
                }
                coroutines.add(job)
            }


            actualCount = coroutines.awaitAll().sum()
        }
    }
    println("$elapsedCr ms ($actualCount)")
}

fun work(max: Long): Long {
    var counter = 0L
    for (i in 1..max) {
        counter++
    }
    if (counter != max) {
        println("Counter mismatch")
    }
    return counter
}


suspend fun workAsync(max: Long): Deferred<Long> = coroutineScope {
    async {
        var counter = 0L
        for (i in 1..max) {
            counter++
        }
        if (counter != max) {
            println("Counter mismatch")
        }

        return@async counter
    }
}