package day_12

import measureFastestExecutionTimes
import java.io.File
import java.util.PriorityQueue

fun main() {
    println("Answer 1: ${stepOne()}")
    println("Answer 2: ${stepTwo()}")
    println("Fastest execution times ${measureFastestExecutionTimes(listOf(::stepOne, ::stepTwo)).joinToString(" and ")} respectively")
}

fun Char.height() : Int{
    if (this == 'S') return 0;
    if (this == 'E') return 'z' - 'a';
    return this - 'a';
}

fun Pair<Int, Int>.bound(xBound: Int, yBound: Int) = if (first in 0 until xBound && second in 0 until yBound) this else null

fun shortestPath(start: Pair<Int, Int>, end: Pair<Int, Int>, grid: List<List<Char>>): List<Pair<Int, Int>>? {
    val width = grid.first().size; val height = grid.size

    val queue = PriorityQueue<List<Pair<Int, Int>>> { a, b -> a.size - b.size };
    queue.add(listOf(start))

    val visited = mutableSetOf<Pair<Int, Int>>()

    while(queue.isNotEmpty()){
        val candidate = queue.poll()
        val position = candidate.last()

        if (position in visited) continue;
        visited.add(position);

        if (position == end) return candidate

        val (px, py) = position;
        val neighbours = listOf(px - 1 to py, px + 1 to py, px to py - 1, px to py + 1)
            .mapNotNull { it.bound(width, height) }
            .filter { !visited.contains(it) }
            .filter { (x, y) -> grid[y][x].height() - grid[py][px].height() <= 1 }

        queue.addAll(neighbours.map { candidate + it })
    }

    return null;
}

fun loadData(path: String, charactersOfInterest: Set<Char>) = File(path).readLines().let { lines ->
    val grid = lines.map { line -> line.toList() }
    val pointOfInterest = grid
        .asSequence()
        .withIndex()
        .flatMap { (y, row) -> row
            .toList()
            .withIndex()
            .map { (x, char) -> char to (x to y) }
        }
        .filter { it.first in charactersOfInterest }
        .groupBy ({ it.first }){ it.second }
     grid to pointOfInterest;
}

fun stepOne(): Int {
    val (grid, pointsOfInterest) = loadData("data/day_12.txt", "SE".toSet());
    val start = pointsOfInterest['S']!!.single()
    val end = pointsOfInterest['E']!!.single()
    val path = shortestPath(start, end, grid)!!
    return path.size - 1
}

fun stepTwo(): Int {
    val (grid, pointsOfInterest) = loadData("data/day_12.txt", "SaE".toSet());
    val starts = pointsOfInterest['S']!! + pointsOfInterest['a']!!
    val end = pointsOfInterest['E']!!.single()
    val steps = starts.map { start -> shortestPath(start, end, grid) }
    return steps.filterNotNull().minOf { it.size } - 1;
}