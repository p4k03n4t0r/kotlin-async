import kotlinx.coroutines.*
import java.lang.Runnable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Runs without shared variable or a return value
 */
fun isolated(totalWork: Long, concurrent: Int): List<Result> {
    val results = mutableListOf<Result>()
    val dividedWork = totalWork / concurrent

    results.add(time(totalWork, "Isolated-SingleThreaded") { w ->
        work(w)
    })

    results.add(time(dividedWork, "Isolated-Multithreaded") { w ->
        val executor = Executors.newFixedThreadPool(concurrent)
        for (i in 1..concurrent) {
            val worker = Runnable { work(w) }
            executor.submit(worker)
        }
        executor.shutdown()
        executor.awaitTermination(1, TimeUnit.DAYS)
    })

    results.add(time(dividedWork, "Isolated-CoroutinesDefault") { w ->
        runBlocking {
            val coroutines = mutableListOf<Deferred<Unit>>()
            for (i in 1..concurrent) {
                val job = async<Unit> {
                    workAsync(w).await()
                }
                coroutines.add(job)
            }
            coroutines.awaitAll()
        }
    })

    results.add(time(dividedWork, "Isolated-CoroutinesIO") { w ->
        runBlocking {
            withContext(Dispatchers.IO) {
                val coroutines = mutableListOf<Deferred<Unit>>()
                for (i in 1..concurrent) {
                    val job = async<Unit> {
                        workAsync(w).await()
                    }
                    coroutines.add(job)
                }
                coroutines.awaitAll()
            }
        }
    })

    results.add(time(dividedWork, "Isolated-CoroutinesST") { w ->
        runBlocking {
            withContext(newSingleThreadContext("MyOwnThread")) {
                val coroutines = mutableListOf<Deferred<Unit>>()
                for (i in 1..concurrent) {
                    val job = async<Unit> {
                        workAsync(w).await()
                    }
                    coroutines.add(job)
                }
                coroutines.awaitAll()
            }
        }
    })

    return results
}
