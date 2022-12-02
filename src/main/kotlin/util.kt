import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun List<Duration>.median() = sorted()[size/2]
@OptIn(ExperimentalTime::class)
fun measureMedians(functions: List<() -> Any?>, iterations: Int = 11) = functions.map { function -> List(iterations) { measureTime { function() } }.median() }