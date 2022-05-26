fun main(args: Array<String>) {
    val concurrent = 10
    val work = 10000000000L

    val result = returned(work, concurrent)

    println(result)
}