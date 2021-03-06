
import kotlinx.coroutines.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Runs and processes the result
 */
fun returned(totalWork: Long, concurrent: Int, cpus: Int): List<Result> {
    val results = mutableListOf<Result>()
    val workPerThread = totalWork / concurrent

    results.add(timeWithReturn(totalWork, "Returned-SingleThreaded-$cpus") { w ->
        work(w)
    })

    results.add(timeWithReturn(workPerThread, "Returned-Multithreaded-$cpus") { w ->
        val executor = Executors.newFixedThreadPool(concurrent)
        val works = mutableListOf<Callable<Long>>()
        for (i in 1..concurrent) {
            val worker = Callable { work(w) }
            executor.submit(worker)
            works.add(worker)
        }
        executor.shutdown()
        executor.awaitTermination(1, TimeUnit.DAYS)
        works.sumOf { it.call() }
    })

    results.add(timeWithReturn(workPerThread, "Returned-CoroutinesDefault-$cpus") { w ->
        runBlocking {
            val coroutines = mutableListOf<Deferred<Long>>()
            for (i in 1..concurrent) {
                val job = async {
                    workAsync(w).await()
                }
                coroutines.add(job)
            }
            coroutines.awaitAll().sum()
        }
    })

    results.add(timeWithReturn(workPerThread, "Returned-CoroutinesIO-$cpus") { w ->
        runBlocking {
            withContext(Dispatchers.IO) {
                val coroutines = mutableListOf<Deferred<Long>>()
                for (i in 1..concurrent) {
                    val job = async {
                        workAsync(w).await()
                    }
                    coroutines.add(job)
                }
                coroutines.awaitAll().sum()
            }
        }
    })

    results.add(timeWithReturn(workPerThread, "Returned-CoroutinesST-$cpus") { w ->
        runBlocking {
            withContext(newSingleThreadContext("MyOwnThread")) {
                val coroutines = mutableListOf<Deferred<Long>>()
                for (i in 1..concurrent) {
                    val job = async {
                        workAsync(w).await()
                    }
                    coroutines.add(job)
                }
                coroutines.awaitAll().sum()
            }
        }
    })

    return results
}