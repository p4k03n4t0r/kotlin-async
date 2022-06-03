fun main(args: Array<String>) {
    val concurrent = 10
    val work = 10000000000L

    val result = returned(work, concurrent, 0)

    println(result)
}