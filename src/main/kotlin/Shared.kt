import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Callable
import java.util.concurrent.Executors

fun shared(totalWork: Long, threads: Int): List<Result> {
    val results = mutableListOf<Result>()
    val workPerThread =  totalWork/threads

    var sharedCounter = Counter()
    val result1 = time(totalWork, "Shared-SingleThreaded") { w ->
        workShared(w, sharedCounter)
    }
    result1.totalCount = sharedCounter.total
    results.add(result1)

    sharedCounter = Counter()
    val result2 = time(workPerThread, "Shared-Multithreaded") { w ->
        val executor = Executors.newFixedThreadPool(threads)
        for (i in 1..threads) {
            val worker = Callable { workShared(w, sharedCounter) }
            executor.submit(worker)
        }
        executor.shutdown()
        while (!executor.isTerminated) {
        }
    }
    result2.totalCount = sharedCounter.total
    results.add(result2)

    sharedCounter = Counter()
    val result3 = time(workPerThread, "Shared-Coroutines") { w ->
        runBlocking {
            val coroutines = mutableListOf<Deferred<Unit>>()
            for (i in 1..threads) {
                val job = async {
                    return@async workSharedAsync(w, sharedCounter).await()
                }
                coroutines.add(job)
            }
        }
    }
    result3.totalCount = sharedCounter.total
    results.add(result3)

    return results
}