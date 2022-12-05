package day_4

import measureFastestExecutionTimes
import java.io.File

fun main() {
    println("Answer 1: ${stepOne()}")
    println("Answer 2: ${stepTwo()}")
    println("Fastest execution times ${measureFastestExecutionTimes(listOf(::stepOne, ::stepTwo)).joinToString(" and ")} respectively")
}

fun String.toRange() = this.split("-").let { it[0].toInt() to it[1].toInt() }.run { first..second }

infix fun IntRange.covers(target: IntRange) = first <= target.first && target.last <= last

fun stepOne(): Int =
    File("data/day_4.txt").useLines { lines ->
        val pairs = lines.map{ line -> line.split(",").let { it[0].toRange() to it[1].toRange() } }
        pairs.count { (first, second) -> first covers second || second covers first }
    }

infix fun IntRange.overlaps(target: IntRange) = contains(target.first) || contains(target.last) || (target covers this)

fun stepTwo(): Int =
    File("data/day_4.txt").useLines { lines ->
        val pairs = lines.map{ line -> line.split(",").let { it[0].toRange() to it[1].toRange() } }
        pairs.count { (first, second) -> first overlaps second }
    }