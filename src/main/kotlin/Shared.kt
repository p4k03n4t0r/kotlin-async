import kotlinx.coroutines.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Runs using a shared (pointer to a) variable
 */
fun shared(totalWork: Long, concurrent: Int, cpus: Int): List<Result> {
    val results = mutableListOf<Result>()
    val workPerThread = totalWork / concurrent

    var sharedClass = SharedClass()
    var result = time(totalWork, "Shared-SingleThreaded-$cpus") { w ->
        workShared(w, sharedClass)
    }
    result.totalCount = sharedClass.counter
    results.add(result)

    sharedClass = SharedClass()
    result = time(workPerThread, "Shared-Multithreaded-$cpus") { w ->
        val executor = Executors.newFixedThreadPool(concurrent)
        for (i in 1..concurrent) {
            val worker = Callable { workShared(w, sharedClass) }
            executor.submit(worker)
        }
        executor.shutdown()
        executor.awaitTermination(1, TimeUnit.DAYS)
    }
    result.totalCount = sharedClass.counter
    results.add(result)

    sharedClass = SharedClass()
    result = time(workPerThread, "Shared-CoroutinesDefault-$cpus") { w ->
        runBlocking {
            val coroutines = mutableListOf<Deferred<Unit>>()
            for (i in 1..concurrent) {
                val job = async {
                    workSharedAsync(w, sharedClass).await()
                }
                coroutines.add(job)
            }
        }
    }
    result.totalCount = sharedClass.counter
    results.add(result)

    sharedClass = SharedClass()
    result = time(workPerThread, "Shared-CoroutinesIO-$cpus") { w ->
        runBlocking {
            withContext(Dispatchers.IO) {
                val coroutines = mutableListOf<Deferred<Unit>>()
                for (i in 1..concurrent) {
                    val job = async {
                        workSharedAsync(w, sharedClass).await()
                    }
                    coroutines.add(job)
                }
            }
        }
    }
    result.totalCount = sharedClass.counter
    results.add(result)

    sharedClass = SharedClass()
    result = time(workPerThread, "Shared-CoroutinesST-$cpus") { w ->
        runBlocking {
            withContext(newSingleThreadContext("MyOwnThread")) {
                val coroutines = mutableListOf<Deferred<Unit>>()
                for (i in 1..concurrent) {
                    val job = async {
                        workSharedAsync(w, sharedClass).await()
                    }
                    coroutines.add(job)
                }
            }
        }
    }
    result.totalCount = sharedClass.counter
    results.add(result)

    return results
}