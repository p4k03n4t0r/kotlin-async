fun main(args: Array<String>) {
    val concurrent = 4
    val totalWork = 1000000000L

    shared(totalWork, concurrent)
}