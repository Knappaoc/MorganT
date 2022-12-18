package day_9

import measureFastestExecutionTimes
import java.io.File
import kotlin.math.abs

fun main() {
    println("Answer 1: ${stepOne()}")
    println("Answer 2: ${stepTwo()}")
    println("Fastest execution times ${measureFastestExecutionTimes(listOf(::stepOne, ::stepTwo)).joinToString(" and ")} respectively")
}

data class Vector2D(val x: Int = 0, val y: Int = 0){
    operator fun plus(other: Vector2D) = Vector2D(x + other.x, y + other.y)
    operator fun minus(other: Vector2D) = Vector2D(x - other.x, y - other.y)
    private infix fun touching(other: Vector2D) = (this - other).let { abs(it.x) <= 1 && abs(it.y) <= 1 }
    fun trail(leader: Vector2D) =
        if (this touching leader) {
            this
        } else {
            this + (leader - this).let { (dx, dy) -> Vector2D(if (dx != 0) dx/abs(dx) else 0, if (dy != 0) dy/abs(dy) else 0) }
        }

}

enum class Direction(val vector: Vector2D) {
    U(Vector2D(0, 1)),
    R(Vector2D(1, 0)),
    D(Vector2D(0, -1)),
    L(Vector2D(-1, 0)),
}

data class Rope(val head: Vector2D = Vector2D(), val tail: Vector2D = Vector2D()){
    fun moveToward(direction: Direction): Rope{
        val nextHead = head + direction.vector;
        val nextTail = tail.trail(nextHead)
        return Rope(nextHead, nextTail);
    }
}

fun stepOne(): Int  {
    val moves = File("data/day_9.txt").readLines().map { it.split(" ") }.map { Direction.valueOf(it[0]) to it[1].toInt() }
    val steps = moves.flatMap { (direction, amount) -> List(amount) { direction } }
    val simulation = steps.scan(Rope()) { rope, direction -> rope.moveToward(direction)}
    return simulation.map { it.tail }.toSet().size
}

data class SnappedRope(val knots: List<Vector2D> = List(10) { Vector2D() }){
    fun moveToward(direction: Direction): SnappedRope {
        val nextHead = knots.first() + direction.vector;
        val nextKnots = knots.drop(1).fold(mutableListOf(nextHead)){ acc, knot -> acc.apply { add(knot.trail(last())) } }
        return SnappedRope(nextKnots)
    }
}

fun stepTwo(): Int {
    val moves = File("data/day_9.txt").readLines().map { it.split(" ") }.map { Direction.valueOf(it[0]) to it[1].toInt() }
    val steps = moves.flatMap { (direction, amount) -> List(amount) { direction } }
    val simulation = steps.scan(SnappedRope()) { rope, direction -> rope.moveToward(direction)}
    return simulation.map { it.knots.last() }.toSet().size
}