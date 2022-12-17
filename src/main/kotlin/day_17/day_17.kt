package day_17

import day_14.Point2D
import java.io.File
import java.util.Vector
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
//    println("Answer 1 (test): ${stepOne("data/test.txt")}")
    measureTime {
        println("Answer 1: ${stepOne("data/day_17.txt")}")
    }.also { println(it) }
//    println("Answer 2 (test): ${stepTwo("data/test.txt")}") // 1514285714288 1571428571420
    println("Answer 2 (test): ${stepTwo("data/day_17.txt")}")

}

data class Vector2D(val x: Long, val y: Long) {
    operator fun plus(other: Vector2D) = Vector2D(x + other.x, y + other.y)
}

enum class RockShape(val points: Set<Vector2D>) {
    Horizontal(setOf(Vector2D(0, 0), Vector2D(1, 0), Vector2D(2, 0), Vector2D(3, 0))),
    Plus(setOf(Vector2D(1, 0), Vector2D(0, 1), Vector2D(1, 1), Vector2D(2, 1), Vector2D(1, 2))),
    L(setOf(Vector2D(0, 0), Vector2D(1, 0), Vector2D(2, 0), Vector2D(2, 1), Vector2D(2, 2))),
    Vertical(setOf(Vector2D(0, 0), Vector2D(0, 1), Vector2D(0, 2), Vector2D(0, 3))),
    Square(setOf(Vector2D(0, 0), Vector2D(0, 1), Vector2D(1, 0), Vector2D(1, 1))),
}

data class Rock(val points: Set<Vector2D>, val shape: RockShape) {

    private infix fun overlapping(otherRock: Rock) = (points intersect otherRock.points).isNotEmpty()
    private infix fun inBounds(xBounds: IntRange) = points.all { it.x in xBounds }

    fun move(offset: Vector2D, horizontalRange: IntRange, rocks: List<Rock>): Rock {
        val newRock = Rock(points.map { it + offset }.toSet(), shape)
        return if (newRock inBounds (horizontalRange) && rocks.none { newRock overlapping it }) newRock else this
    }

    fun move(offset: Vector2D, horizontalRange: IntRange, settled: Set<Vector2D>): Rock {
        val newRock = Rock(points.map { it + offset }.toSet(), shape)
        return if (newRock inBounds (horizontalRange) && (newRock.points.toSet() intersect settled).isEmpty()) newRock else this
    }
}

fun stepOne(path: String): Long {
    val jetMovements = File(path).readText().toList().map {
        when (it) {
            '>' -> Vector2D(1, 0)
            '<' -> Vector2D(-1, 0)
            else -> throw Error("Unexpected Character")
        }
    };

    val jetPattern = iterator {
        while (true) {
            jetMovements.forEach { yield(it) }
        }
    };
    val rockPattern = iterator {
        while (true) {
            RockShape.values().forEach { yield(Rock(it.points, it)) }
        }
    };

    val xRange = 0 until 7
    val fallen = mutableListOf<Rock>()
    fun nextRock() = rockPattern.next()
        .move(Vector2D(2, (fallen.asSequence().flatMap { it.points }.maxOfOrNull { it.y } ?: 0) + 4), xRange, fallen)

    var rock = nextRock()
    while (fallen.size != 2022) {
        val jetDirection = jetPattern.next();
        rock = rock.move(jetDirection, xRange, fallen)

        val gravity = rock.move(Vector2D(0, -1), xRange, fallen)
        if (gravity == rock || gravity.points.any { it.y == 0L }) {
            fallen.add(rock)
            rock = nextRock()
//            draw(xRange, rock, fallen.flatMap { it.points }.toSet());
            continue
        }
        rock = gravity
    }

    return fallen.flatMap { it.points }.maxOf { it.y };
}

//fun draw(xRange: IntRange, rock: Rock, solid: Set<Vector2D>){
//
//    val rockSet = rock.points.toSet()
//    val height = (solid + rockSet).maxOf { it.y } + 1;
//
//    val grid = List(height) { y -> List(xRange.last + 1) { x ->
//        when (Vector2D(x, y)) {
//            in solid -> '#'
//            in rockSet -> '@'
//            else -> '.'
//        }
//    } }
//
//    println(grid.reversed().joinToString("\n", postfix = "\n+${"-".repeat(xRange.last + 1)}+") { row -> row.joinToString("", prefix = "|", postfix = "|") })
//    println()
//}

@OptIn(ExperimentalTime::class)
fun stepTwo(path: String): Long {

    val heightSequence = sequence<Long> {
        val jetPattern = iterator {
            val jetMovements = File(path).readText().toList().map {
                when (it) {
                    '>' -> Vector2D(1, 0)
                    '<' -> Vector2D(-1, 0)
                    else -> throw Error("Unexpected Character")
                }
            };
            while (true) {
                jetMovements.forEach { yield(it) }
            }
        };
        val rockPattern = iterator {
            while (true) {
                RockShape.values().forEach { yield(Rock(it.points, it)) }
            }
        };

        val xRange = 0 until 7
        var fallenCount = 0
        val settled = mutableSetOf<Vector2D>()
        fun nextRock() = rockPattern.next().move(Vector2D(2, (settled.maxOfOrNull { it.y } ?: 0) + 4L), xRange, settled)
        yield(0L)
        var rock = nextRock()
        while (true) {

            val jetDirection = jetPattern.next();
            rock = rock.move(jetDirection, xRange, settled)

            val gravity = rock.move(Vector2D(0, -1), xRange, settled)
            if (gravity == rock || gravity.points.any { it.y == 0L }) {
                settled.addAll(rock.points)
                fallenCount += 1;

                val ys = rock.points.run { minOf { it.y }..maxOf { it.y } }
                for (y in ys) {
                    if (xRange.map { x -> Vector2D(x.toLong(), y) }.all { it in settled }) {
                        settled.removeIf { it.y < y }
                    }
                }
                yield(settled.maxOf { it.y })
                rock = nextRock()
                continue
            }
            rock = gravity
        }
    }

    val cycleSize = (1..500)
        .map { it * 5 }
        .first { chunkSize ->
            heightSequence
                .chunked(chunkSize)
                .map { it.last() - it.first() }
                .take(10).toList()
                .also { println(chunkSize to it) }
            false
        }

    val debug = heightSequence.elementAt(2022)

    val target = 1000000000000;
    val heightAtStartOfCycle = heightSequence.elementAt(cycleSize);
    val cycleHeight = heightSequence.elementAt(2*cycleSize) - heightAtStartOfCycle
    val remaining = target - cycleSize;
    val skippableCycles = remaining / cycleSize;
    val remainingCycles = (remaining % cycleSize).toInt();
    val skippableHeight = skippableCycles * cycleHeight;

    return heightSequence.elementAt(cycleSize + remainingCycles) + skippableHeight;
}


//fun stepTwo(path: String): Long {
//    val jetMovements = File(path).readText().toList().map { when(it) {
//        '>' -> Vector2D(1, 0)
//        '<' -> Vector2D(-1, 0)
//        else -> throw Error("Unexpected Character")
//    } };
//
//    val jetPattern = iterator { while (true){ jetMovements.forEach { yield(it) } } };
//    val rockPattern = iterator { while (true){ RockShapes.values().forEach { yield(Rock(it.points)) } } };
//
//    val xRange = 0 until 7
//    var fallenCount = 0L;
//    val collidable = mutableSetOf<Vector2D>()
//
//    fun nextRock() = rockPattern.next().move(Vector2D(2, (collidable.maxOfOrNull { it.y } ?: 0) + 4), xRange, collidable)
//    var rock = nextRock()
//    while (fallenCount != 1000000000000){
//        val jetDirection = jetPattern.next();
//        rock = rock.move(jetDirection, xRange, collidable)
//
//        val gravity = rock.move(Vector2D(0, -1), xRange, collidable)
//        if (gravity == rock || gravity.points.any { it.y == 0L }) {
//            fallenCount += 1;
//
//            collidable.addAll(rock.points);
//            (rock.points.minOf { it.y }.. rock.points.maxOf { it.y }).forEach { y ->
//                val row = xRange.map { x -> Vector2D(x.toLong(), y) }
//                if (row.all { it in collidable }){
//                    collidable.removeIf{it.y < y}
//                }
//            }
//            rock = nextRock()
////            draw(xRange, rock, fallen.flatMap { it.points }.toSet());
//            continue
//        }
//        rock = gravity
//    }
//
//    return collidable.maxOf { it.y };
//}