package day_5

import measureFastestExecutionTimes
import java.io.File

fun main() {
    println("Answer 1: ${stepOne()}")
    println("Answer 2: ${stepTwo()}")
    println("Fastest execution times ${measureFastestExecutionTimes(listOf(::stepOne, ::stepTwo)).joinToString(" and ")} respectively")
}

fun String.trimSquareBrackets() = trim().removePrefix("[").removeSuffix("]")

fun stepOne(): String {
    val (startStateStr, movesStr) = File("data/day_5.txt").readText().split("\n\n")

    val state = startStateStr
        .lines()
        .dropLast(1)
        .flatMap { line ->
            line.chunked(4)
                .mapIndexedNotNull{ index, value -> if (value.isNotBlank()) index + 1 to value.trimSquareBrackets() else null }
        }
        .reversed()
        .groupBy({ it.first }, { it.second })
        .toMutableMap()

    val movePattern = "move (\\d+) from (\\d+) to (\\d)".toRegex()
    val moves = movesStr.lines().map { line -> requireNotNull(movePattern.matchEntire(line)).groupValues.drop(1).map { it.toInt() } }

    for ((amount, from, to) in moves) {
        val previousFromStack = state.getOrPut(from) { emptyList() }
        val previousToStack = state.getOrPut(to) { emptyList() }
        state[from] = previousFromStack.dropLast(amount)
        state[to] = previousToStack + previousFromStack.takeLast(amount).reversed()
    }

    return state.entries.sortedBy { it.key }.joinToString("") { it.value.last() }
}


fun stepTwo(): String {
    val (startStateStr, movesStr) = File("data/day_5.txt").readText().split("\n\n")

    val state = startStateStr
        .lines()
        .dropLast(1)
        .flatMap { line ->
            line.chunked(4)
                .mapIndexedNotNull{ index, value -> if (value.isNotBlank()) index + 1 to value.trimSquareBrackets() else null }
        }
        .reversed()
        .groupBy({ it.first }, { it.second })
        .toMutableMap()

    val movePattern = "move (\\d+) from (\\d+) to (\\d)".toRegex()
    val moves = movesStr.lines().map { line -> requireNotNull(movePattern.matchEntire(line)).groupValues.drop(1).map { it.toInt() } }

    for ((amount, from, to) in moves) {
        val previousFromStack = state.getOrPut(from) { emptyList() }
        val previousToStack = state.getOrPut(to) { emptyList() }
        state[from] = previousFromStack.dropLast(amount)
        state[to] = previousToStack + previousFromStack.takeLast(amount)
    }

    return state.entries.sortedBy { it.key }.joinToString("") { it.value.last() }
}