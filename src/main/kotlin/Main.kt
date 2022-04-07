
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.File

fun main(args: Array<String>) {
    val concurrent = System.getenv("CONCURRENT")?.toInt() ?: 10
    val max = System.getenv("MAX")?.toLong() ?: 10000000000L
    val scale = System.getenv("SCALE")?.toLong() ?: 1000000000L
    val modes = System.getenv("MODES")?.split(",") ?: listOf("isolated", "returned", "shared")
    val outputFile = System.getenv("SCALE") ?: "output.csv"

    var measurements = mutableListOf<Measurement>()
    for(i in scale..max step scale) {
        var totalWork = i
        println(i)
        var results = mutableListOf<Result>()
        for(mode in modes) {
            when (mode) {
                "isolated" -> results.addAll(isolated(totalWork, concurrent))
                "returned" -> results.addAll(returned(totalWork, concurrent))
                "shared" -> results.addAll(shared(totalWork, concurrent))
                else -> {
                    println("Unknown mode $mode")
                }
            }
        }

        measurements.add(Measurement(totalWork.toString(), results))
    }

    writeResults(measurements, "output/$outputFile")
}

private fun writeResults(measurements: MutableList<Measurement>, fileName: String) {
    val f = File(fileName)
    f.createNewFile()
    val writer = f.writer()
    val csvPrinter = CSVPrinter(writer, CSVFormat.DEFAULT)
    measurements.forEach { measurement ->
        measurement.results.forEach { result ->
            csvPrinter.printRecord(measurement.x, result.name, result.time, result.totalCount)
        }
    }
    writer.flush()
    writer.close()
}

class Measurement constructor(val x: String, val results: MutableList<Result>) {

}

class Result constructor(val name: String, val time: Long, var totalCount: Long) {
    override fun toString(): String {
        return "$name ${time}ms ($totalCount)"
    }
}