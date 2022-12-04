package day_4

import java.io.File

fun main() {
    println("Answer 1: ${stepOne()}")
    println("Answer 2: ${stepTwo()}")
//    println("median durations were ${measureMedians(listOf(::stepOne, ::stepTwo)).joinToString(" and ")} respectively")
}

fun String.toRange() = this.split("-").let { it[0].toInt() to it[1].toInt() }.run { first..second }

fun stepOne(): Int =
    File("data/day_4.txt").useLines { lines ->
        val pairs = lines.map{ line -> line.split(",").let { it[0].toRange() to it[1].toRange() } }
        pairs.count { (first, second) ->
            first.all { second.contains(it) } || second.all { first.contains(it) }
        }
    }

fun stepTwo(): Int =
    File("data/day_4.txt").useLines { lines ->
        val pairs = lines.map{ line -> line.split(",").let { it[0].toRange() to it[1].toRange() } }
        pairs.count { (first, second) ->
            first.any { second.contains(it) } || second.any { first.contains(it) }
        }
    }