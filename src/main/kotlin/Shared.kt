import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Callable
import java.util.concurrent.Executors

/**
 * Runs using a shared (pointer to a) variable
 */
fun shared(totalWork: Long, concurrent: Int): List<Result> {
    val results = mutableListOf<Result>()
    val workPerThread = totalWork / concurrent

    var sharedClass = SharedClass()
    val result1 = time(totalWork, "Shared-SingleThreaded") { w ->
        workShared(w, sharedClass)
    }
    result1.totalCount = sharedClass.counter
    results.add(result1)

    sharedClass = SharedClass()
    val result2 = time(workPerThread, "Shared-Multithreaded") { w ->
        val executor = Executors.newFixedThreadPool(concurrent)
        for (i in 1..concurrent) {
            val worker = Callable { workShared(w, sharedClass) }
            executor.submit(worker)
        }
        executor.shutdown()
        while (!executor.isTerminated) {
        }
    }
    result2.totalCount = sharedClass.counter
    results.add(result2)

    sharedClass = SharedClass()
    val result3 = time(workPerThread, "Shared-Coroutines") { w ->
        runBlocking {
            val coroutines = mutableListOf<Deferred<Unit>>()
            for (i in 1..concurrent) {
                val job = async {
                    return@async workSharedAsync(w, sharedClass).await()
                }
                coroutines.add(job)
            }
        }
    }
    result3.totalCount = sharedClass.counter
    results.add(result3)

    return results
}