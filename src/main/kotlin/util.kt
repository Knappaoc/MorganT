import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun measureFastestExecutionTimes(functions: List<() -> Any?>, iterations: Int = 11) = functions.map { function ->
    sequence { repeat(iterations) { yield(measureTime { function() }) } }.min()
}