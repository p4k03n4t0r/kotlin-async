package isolated

import kotlinx.coroutines.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import kotlin.system.measureTimeMillis

fun main() {
    val totalWork = 10000000000L
    val threads = 10

    println("SingleThreaded")
    val elapsedSt = measureTimeMillis {
        work(totalWork)
    }
    println("$elapsedSt ms")

    println("Multithreaded")
    val elapsedMt = measureTimeMillis {
        val executor = Executors.newFixedThreadPool(threads)
        val works = mutableListOf<Callable<Long>>()
        val workPerThread = totalWork/threads
        for (i in 1..threads) {
            val worker = Runnable { work(workPerThread) }
            executor.submit(worker)
        }
        executor.shutdown()
        while (!executor.isTerminated) {
        }
    }
    println("$elapsedMt ms")

    println("Coroutines")
    val elapsedCr = measureTimeMillis {
        runBlocking {
            val workPerThread = totalWork/threads
            val coroutines = mutableListOf<Deferred<Unit>>()
            for (i in 1..threads) {
                val job = async {
                    return@async workAsync(workPerThread).await()
                }
                coroutines.add(job)
            }


            coroutines.awaitAll()
        }
    }
    println("$elapsedCr ms")
}

fun work(max: Long) {
    var counter = 0L
    for (i in 1..max) {
        counter++
    }
    if (counter != max) {
        println("Counter mismatch")
    }
}


suspend fun workAsync(max: Long): Deferred<Unit> = coroutineScope {
    async {
        var counter = 0L
        for (i in 1..max) {
            counter++
        }
        if (counter != max) {
            println("Counter mismatch")
        }

        return@async
    }
}