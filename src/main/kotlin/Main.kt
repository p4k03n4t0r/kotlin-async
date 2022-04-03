
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.File

fun main() {
    val threads = 10
    val max = 10000000000L
    val scale = 1000000000L

    var measurements = mutableListOf<Measurement>()
    for(i in scale..max step scale) {
//        val totalWork = 10F.pow(i).toLong()
        var totalWork = i
        println(i)
        var results = mutableListOf<Result>()
        results.addAll(isolated(totalWork, threads))
        results.addAll(returned(totalWork, threads))
        results.addAll(shared(totalWork, threads))

        measurements.add(Measurement(totalWork.toString(), results))
    }

    writeResults(measurements, "output.csv")
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