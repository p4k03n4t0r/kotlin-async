import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Runs and processes the result
 */
fun returned(totalWork: Long, concurrent: Int): List<Result> {
    val results = mutableListOf<Result>()
    val workPerThread = totalWork / concurrent

    results.add(timeWithReturn(totalWork, "Returned-SingleThreaded") { w ->
        return@timeWithReturn work(w)
    })

    results.add(timeWithReturn(workPerThread, "Returned-Multithreaded") { w ->
        val executor = Executors.newFixedThreadPool(concurrent)
        val works = mutableListOf<Callable<Long>>()
        for (i in 1..concurrent) {
            val worker = Callable { work(w) }
            executor.submit(worker)
            works.add(worker)
        }
        executor.shutdown()
        executor.awaitTermination(1, TimeUnit.DAYS)
        return@timeWithReturn works.sumOf { it.call() }
    })

    results.add(timeWithReturn(workPerThread, "Returned-Coroutines") { w ->
        runBlocking {
            val coroutines = mutableListOf<Deferred<Long>>()
            for (i in 1..concurrent) {
                val job = async {
                    return@async workAsync(w).await()
                }
                coroutines.add(job)
            }
            return@runBlocking coroutines.awaitAll().sum()
        }
    })

    return results
}