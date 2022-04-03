
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Callable
import java.util.concurrent.Executors

fun returned(totalWork: Long, threads: Int): List<Result> {
    val results = mutableListOf<Result>()
    val workPerThread =  totalWork/threads

    results.add(timeWithReturn(totalWork, "Returned-SingleThreaded") { w ->
        return@timeWithReturn work(w)
    })

    results.add(timeWithReturn(workPerThread, "Returned-Multithreaded") { w ->
        val executor = Executors.newFixedThreadPool(threads)
        val works = mutableListOf<Callable<Long>>()
        for (i in 1..threads) {
            val worker = Callable { work(w) }
            executor.submit(worker)
            works.add(worker)
        }
        executor.shutdown()
        while (!executor.isTerminated) {
        }
        return@timeWithReturn works.sumOf { it.call() }
    })

    results.add(timeWithReturn(workPerThread, "Returned-Coroutines") { w ->
        runBlocking {
            val coroutines = mutableListOf<Deferred<Long>>()
            for (i in 1..threads) {
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