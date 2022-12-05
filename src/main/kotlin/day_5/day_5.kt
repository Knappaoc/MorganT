package day_5

import measureMedians
import java.io.File

fun main() {
    println("Answer 1: ${stepOne()}")
    println("Answer 2: ${stepTwo()}")
    println("median durations were ${measureMedians(listOf(::stepOne, ::stepTwo)).joinToString(" and ")} respectively")
}

fun stepOne(): String {
    val (startStateStr, movesStr) = File("data/day_5.txt").readText().split("\n\n")
    val stackStr = startStateStr.lines().dropLast(1);

    val t = stackStr.map { it.chunked(4).withIndex().toList() }
    val state = t
        .flatten()
        .filter { it.value.isNotBlank() }
        .reversed()
        .groupBy { it.index + 1 }
        .mapValues { it.value.map { it.value.replace("[\\s|\\[|\\]]".toRegex(), "") }.toMutableList() }

    val moves = movesStr.lines().map { line ->
        val tokens = line.split(" ");
        listOf(tokens[1], tokens[3], tokens[5]).map{it.toInt()}
    }

    moves.forEach { (amount, from, to) ->
        val takeStack = state.getOrDefault(from, mutableListOf<String>())
        val putStack = state.getOrDefault(to, mutableListOf<String>())
        repeat(amount){ putStack.add(takeStack.removeLast()) }
    }

    val top = state.entries.sortedBy { it.key }.map { it.value.last() }.joinToString("")

    return top
}


fun stepTwo(): String {
    val (startStateStr, movesStr) = File("data/day_5.txt").readText().split("\n\n")
    val stackStr = startStateStr.lines().dropLast(1);

    val t = stackStr.map { it.chunked(4).withIndex().toList() }
    val state = t
        .flatten()
        .filter { it.value.isNotBlank() }
        .reversed()
        .groupBy { it.index + 1 }
        .mapValues { it.value.map { it.value.replace("[\\s|\\[|\\]]".toRegex(), "") }.toMutableList() }

    val moves = movesStr.lines().map { line ->
        val tokens = line.split(" ");
        listOf(tokens[1], tokens[3], tokens[5]).map{it.toInt()}
    }

    moves.forEach { (amount, from, to) ->
        val takeStack = state.getOrDefault(from, mutableListOf<String>())
        val putStack = state.getOrDefault(to, mutableListOf<String>())
        putStack.addAll(takeStack.takeLast(amount));
        repeat(amount){ takeStack.removeLast() }

    }

    val top = state.entries.sortedBy { it.key }.map { it.value.last() }.joinToString("")

    return top
}