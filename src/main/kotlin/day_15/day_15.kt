package day_15

import java.io.File
import kotlin.math.abs

fun main() {
//    println("Test Answer 1: ${stepOne("data/test.txt", 10)}")
//    println("Answer 1: ${stepOne("data/day_15.txt", 2000000)}") // 4229193, 4229371 is too low, equals 5832528

    println("Answer 2: ${stepTwo("data/day_15.txt")}")
//    println("Fastest execution times ${measureFastestExecutionTimes(listOf(::stepOne, ::stepTwo)).joinToString(" and ")} respectively")
}

val matcher = "Sensor at x=(\\d+), y=(\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)".toRegex()

data class Vector2D(val x: Int, val y: Int){
    operator fun plus(other: Vector2D) = Vector2D(x + other.x, y + other.y)
    operator fun minus(other: Vector2D) = Vector2D(x - other.x, y - other.y)

    val manhattanMagnitude = abs(x) + abs(y)
}

fun Vector2D.pointsWithinRange(size: Int) =
    ((x-size)..(x+size)).flatMap { tx ->
        val yRangeHeight = size - abs(tx - x);
        val yRange = (y-yRangeHeight)..(y+yRangeHeight)
        yRange.map { ty -> Vector2D(tx, ty) }
    }

fun Vector2D.pointsWithinRangeOnLine(size: Int, yTarget: Int): IntRange?{
    val dy = abs(y - yTarget)
    if (dy > size) return null;
    val remainingDistance = size - dy;
    return (x - remainingDistance)..(x + remainingDistance)
}

data class Sensor(val position: Vector2D, val closestBeaconPosition: Vector2D){
    private val radius = (closestBeaconPosition - position).manhattanMagnitude;
    fun covers(point: Vector2D) = (point - position).manhattanMagnitude <= radius;
    fun exclusiveBoundaryPoints(): List<Vector2D> = ((position.x - (radius + 1))..(position.x + (radius + 1))).fold(mutableListOf<Vector2D>()){ acc, tx ->
        val dx = abs(tx - position.x);
        val dy = (radius + 1) - dx;
        if (dy == 0){
            acc.add(Vector2D(tx, position.y))
        } else {
            acc.add(Vector2D(tx, position.y + dy))
            acc.add(Vector2D(tx, position.y - dy))
        }
        acc
    }
}
fun stepOne(path: String, targetY: Int): Int {
    val sensors = File(path)
        .readLines()
        .mapNotNull { line -> matcher.matchEntire(line)?.run { groups.drop(1).map { it!!.value.toInt() } } }
        .map { (sx, sy, dbx, dby) -> Sensor(Vector2D(sx, sy), Vector2D(dbx, dby)) }

    val positions = sensors.flatMap { listOf(it.position, it.closestBeaconPosition) }

    val excludedRanges = sensors.mapNotNull { sensor -> sensor.position.pointsWithinRangeOnLine((sensor.closestBeaconPosition - sensor.position).manhattanMagnitude, targetY) }
    val t = mutableSetOf<Int>()
    excludedRanges.forEach { t.addAll(it) }
    positions.filter { it.y == targetY }.forEach { (x, _) -> t.remove(x) }

    return t.size
}

fun stepTwo(path: String): Long {
    val sensors = File(path)
        .readLines()
        .mapNotNull { line -> matcher.matchEntire(line)?.run { groups.drop(1).map { it!!.value.toInt() } } }
        .map { (sx, sy, dbx, dby) -> Sensor(Vector2D(sx, sy), Vector2D(dbx, dby)) }

    val xs = 0..4000000; val ys = 0..4000000

    val candidates = sensors
        .asSequence()
        .flatMap { it.exclusiveBoundaryPoints() }
        .filter { (x, y) -> (x in xs) && (y in ys) }
        .toSet()

    val distressBeaconPoint = candidates.single { candidate -> !sensors.any { sensor -> sensor.covers(candidate) } }

    val freq = 4000000L * distressBeaconPoint.x.toLong() + distressBeaconPoint.y.toLong()

    return freq
}