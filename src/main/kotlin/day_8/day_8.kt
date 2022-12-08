package day_8

import measureFastestExecutionTimes
import java.io.File

fun main() {
//    println("Answer 1: ${stepOne()}")
    println("Answer 2: ${stepTwo()}")
//    println("Fastest execution times ${measureFastestExecutionTimes(listOf(::stepOne, ::stepTwo)).joinToString(" and ")} respectively")
}

fun stepOne(): Int {
    val trees = File("data/day_8.txt").readLines().map { line ->
        line.split("").mapNotNull { it.toIntOrNull() }
    }

    val height = trees.size;
    val width = trees[0].size

    fun isVisible(x:Int, y: Int): Boolean{
        if (x ==  0 || x == (width - 1)) return true;
        if (y ==  0 || y == (height - 1)) return true;

        val tree = trees[y][x]
        val fromLeft = trees[y].subList(0, x).all { it < tree }
        val fromRight = trees[y].subList(x + 1, width).all { it < tree }
        val fromTop = (0 until y).map { ty -> trees[ty][x] }.all { it < tree }
        val fromBottom = (y + 1 until height).map { ty -> trees[ty][x] }.all { it < tree }

        return fromLeft || fromRight || fromTop || fromBottom
    }

    val count = trees.withIndex().sumOf { (y, row) -> row.withIndex().count { (x, _) -> isVisible(x, y) } }

    return count
}

fun stepTwo(): Int {
    val trees = File("data/day_8.txt").readLines().map { line ->
        line.split("").mapNotNull { it.toIntOrNull() }
    }

    val height = trees.size;
    val width = trees[0].size

    fun scenicScore(x:Int, y: Int): Int {
        val tree = trees[y][x]

        val toLeft = trees[y].subList(0, x).reversed()
        val leftCount = if (toLeft.any { it >= tree }) toLeft.indexOfFirst { it >= tree } + 1 else toLeft.size

        val toRight = trees[y].subList(x + 1, width)
        val rightCount = if (toRight.any { it >= tree }) toRight.indexOfFirst { it >= tree } + 1 else toRight.size

        val toTop = (0 until y).map { ty -> trees[ty][x] }.reversed()
        val topCount = if (toTop.any { it >= tree }) toTop.indexOfFirst { it >= tree } + 1 else toTop.size

        val toBottom = (y + 1 until height).map { ty -> trees[ty][x] }
        val bottomCount = if (toBottom.any { it >= tree }) toBottom.indexOfFirst { it >= tree } + 1 else toBottom.size

        return leftCount * rightCount * topCount * bottomCount
    }

    val scores = trees.withIndex().flatMap{ (y, row) ->
        row.withIndex().map { (x, _) -> scenicScore(x, y) }
    }

    return scores.max()
}