package day_1

import measureFastestExecutionTimes
import java.io.File

fun main(){
    println("Answer 1: ${stepOne()}")
    println("Answer 2: ${stepTwo()}")
    println("Fastest execution times ${measureFastestExecutionTimes(listOf(::stepOne, ::stepTwo)).joinToString(" and ")} respectively")
}

fun stepOne(): Int {
    val input = File("data/day_1.txt").readText();
    val elfStr = input.split("\n\n");

    val cals = elfStr.map { str -> str.split('\n').sumOf { it.toInt() } }.withIndex()
    val max = cals.maxBy { it.value }.value

    return max;
}

fun stepTwo(): Int {
    val input = File("data/day_1.txt").readText();
    val elfStr = input.split("\n\n");

    val cals = elfStr.map { str -> str.split('\n').sumOf { it.toInt() } }

    val max = cals.sorted().takeLast(3).sum()

    return max;
}
