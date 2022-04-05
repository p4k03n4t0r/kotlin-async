
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.File

fun main(args: Array<String>) {
    val concurrent = System.getenv("CONCURRENT")?.toInt() ?: 10
    val max = System.getenv("MAX")?.toLong() ?: 10000000000L
    val scale = System.getenv("SCALE")?.toLong() ?: 1000000000L

    var measurements = mutableListOf<Measurement>()
    for(i in scale..max step scale) {
        var totalWork = i
        println(i)
        var results = mutableListOf<Result>()
        results.addAll(isolated(totalWork, concurrent))
        results.addAll(returned(totalWork, concurrent))
        results.addAll(shared(totalWork, concurrent))

        measurements.add(Measurement(totalWork.toString(), results))
    }

    writeResults(measurements, "output/output.csv")
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