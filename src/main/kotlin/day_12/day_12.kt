package day_12

import measureFastestExecutionTimes
import java.io.File
import java.util.PriorityQueue

fun main() {
//    println("Answer 1: ${stepOne()}")
    println("Answer 2: ${stepTwo()}")
//    println("Fastest execution times ${measureFastestExecutionTimes(listOf(::stepOne, ::stepTwo)).joinToString(" and ")} respectively")
}

fun Char.height() : Int{
    if (this == 'S') return 0;
    if (this == 'E') return 'z' - 'a';
    return this - 'a';
}

fun Pair<Int, Int>.bound(xBound: Int, yBound: Int) = if (first in 0 until xBound && second in 0 until yBound) this else null;

fun stepOne(): Int {
    val grid = File("data/day_12.txt").readLines().map { line -> line.toList() }
    val width = grid[0].size;  val height = grid.size;
    val startY = grid.indexOfFirst { it.contains('S') }; val startX = grid[startY].indexOf('S');
    val endY = grid.indexOfFirst { it.contains('E') }; val endX = grid[endY].indexOf('E');

    val start = startX to startY;
    val end = endX to endY;

    val queue = PriorityQueue<List<Pair<Int, Int>>>() { a, b -> a.size - b.size};
    queue.add(listOf(start))
    val visited = mutableSetOf<Pair<Int, Int>>()
    while(queue.isNotEmpty()){
        val candidate = queue.poll()
        val position = candidate.last()

        if (position in visited) continue;

        if (position == end) return candidate.size - 1

        val (px, py) = position;
        visited.add(position);
        val neighbours = listOf(px - 1 to py, px + 1 to py, px to py - 1, px to py + 1)
            .mapNotNull { it.bound(width, height) }
            .filter { !visited.contains(it) }
            .filter { (x, y) -> grid[y][x].height() - grid[py][px].height() <= 1 }
        queue.addAll(neighbours.map { candidate + it })
    }
    throw Error("Exhausted");
}

fun shortestPathLength(start: Pair<Int, Int>, end: Pair<Int, Int>, grid: List<List<Char>>): Int{
    val width = grid[0].size;  val height = grid.size;
    val queue = PriorityQueue<List<Pair<Int, Int>>>() { a, b -> a.size - b.size};
    queue.add(listOf(start))
    val visited = mutableSetOf<Pair<Int, Int>>()
    while(queue.isNotEmpty()){
        val candidate = queue.poll()
        val position = candidate.last()

        if (position in visited) continue;

        if (position == end) return candidate.size - 1

        val (px, py) = position;
        visited.add(position);
        val neighbours = listOf(px - 1 to py, px + 1 to py, px to py - 1, px to py + 1)
            .mapNotNull { it.bound(width, height) }
            .filter { !visited.contains(it) }
            .filter { (x, y) -> grid[y][x].height() - grid[py][px].height() <= 1 }
        queue.addAll(neighbours.map { candidate + it })
    }
    throw Error("Exhausted");
}


fun stepTwo(): Int {
    val grid = File("data/day_12.txt").readLines().map { line -> line.toList() }
    val starts = grid.mapIndexed { y, chars -> chars.mapIndexed{ x, char -> if (char.height() == 0) x to y else null} }.flatten().filterNotNull()
    val endY = grid.indexOfFirst { it.contains('E') }; val endX = grid[endY].indexOf('E');
    val end = endX to endY;

    val steps = starts.map { start -> try { shortestPathLength(start, end, grid) } catch (e: Error) { null} }

    return steps.filterNotNull().min();
}

//fun stepTwo(): Int {
//    val signal = File("data/day_6.txt").readText().asSequence()
//    val first = signal.windowed(14).indexOfFirst { it.toSet().size == 14 }
//    return first + 14;
//}