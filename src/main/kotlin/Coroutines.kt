import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

// ps -aux | grep Coroutines
// ps -T -p <pid>

fun main() {
    val concurrent = 10
    val result = runBlocking {
        val coroutines = mutableListOf<Deferred<Int>>()
        for (i in 1..concurrent) {
            val job = async {
                var i = 0
                while(true) {
                    i++
                    println(i)
                }
//                delay(100000)
//                println(i)
                1
            }
            coroutines.add(job)
        }
        coroutines.awaitAll().sum()
    }
    println(result)
}