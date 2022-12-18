package day_14

import measureFastestExecutionTimes
import java.io.File

fun main() {
    println("Answer 1: ${stepOne()}")
    println("Answer 2: ${stepTwo()}")
    println("Fastest execution times ${measureFastestExecutionTimes(listOf(::stepOne, ::stepTwo)).joinToString(" and ")} respectively")
}

enum class Material {
    Rock,
    Sand,
    Air
}

data class Point2D(val x: Int, val y: Int)

infix fun Point2D.lineTo(destination: Point2D): List<Point2D> {
    val (dx, dy) = destination;
    return if (x == dx) {
        if (y < dy) {
            ((y + 1)..dy).map { Point2D(x, it) }
        } else {
            ((y - 1) downTo dy).map { Point2D(x, it) }
        }
    } else if (y == dy) {
        if (x < dx) {
            ((x + 1)..dx).map { Point2D(it, y) }
        } else {
            ((x - 1) downTo dx).map { Point2D(it, y) }
        }
    } else {
        throw Error("Encountered diagonal")
    }
}

fun loadData(path: String) = File(path).useLines { lines ->
    lines
        .map { it.split(" -> ") }
        .map {
            it.map { coordinateStr ->
                coordinateStr.split(",").let { (x, y) -> Point2D(x.toInt(), y.toInt()) }
            }
        }.toList()
}

class World(rockPathVertexes: List<List<Point2D>>, private val deltaFloor: Int? = null) {
    private val sandEntryPoint = Point2D(500, 0);
    private val maxRockY = rockPathVertexes.maxOf { it.maxOf { position -> position.y } }
    private val cells: MutableMap<Point2D, Material> = mutableMapOf()

    init {
        val rockPositions = rockPathVertexes.flatMap { line ->
            listOf(line.first()) + line.windowed(2).flatMap { (start, end) -> start lineTo end }
        }
        rockPositions.forEach { position -> cells[position] = Material.Rock }
    }

    val sandCount: Int
        get() = cells.entries.filter { it.value == Material.Sand }.size

    private operator fun get(point: Point2D) = cells.getOrElse(point) {
        if (deltaFloor != null && (point.y >= maxRockY + deltaFloor))
            Material.Rock
        else
            Material.Air
    }

    fun addSand(): Point2D?{
        if (this[sandEntryPoint] == Material.Sand) return null;
        var current = sandEntryPoint;
        while(true){
            val (x, y) = current;
            if(deltaFloor == null && y >= maxRockY ) { return null }
            val nextPosition = listOf(Point2D(x, y + 1), Point2D(x - 1, y + 1), Point2D(x + 1, y + 1)).find { this[it] == Material.Air }
            if (nextPosition != null){
                current = nextPosition;
            } else {
                cells[current] = Material.Sand;
                return current;
            }
        }
    }
}

fun stepOne(): Int {
    val world = World(loadData("data/day_14.txt"))
    do { val settled = world.addSand() } while (settled != null);
    return world.sandCount;
}

fun stepTwo(): Int {
    val world = World(loadData("data/day_14.txt"), 2)
    do { val settled = world.addSand() } while (settled != null);
    return world.sandCount;
}

