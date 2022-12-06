package day_6

import measureFastestExecutionTimes
import java.io.File

fun main() {
    println("Answer 1: ${stepOne()}")
    println("Answer 2: ${stepTwo()}")
    println("Fastest execution times ${measureFastestExecutionTimes(listOf(::stepOne, ::stepTwo)).joinToString(" and ")} respectively")
}

fun stepOne(): Int {
    val signal = File("data/day_6.txt").readText().asSequence()
    val first = signal.windowed(4).indexOfFirst { it.toSet().size == 4 }
    return first + 4;
}

fun stepTwo(): Int {
    val signal = File("data/day_6.txt").readText().asSequence()
    val first = signal.windowed(14).indexOfFirst { it.toSet().size == 14 }
    return first + 14;
}