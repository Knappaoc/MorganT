package day_3

import measureMedians
import java.io.File

fun main() {
    println("Answer 1: ${stepOne()}")
    println("Answer 2: ${stepTwo()}")
//    println("median durations were ${measureMedians(listOf(::stepOne, ::stepTwo)).joinToString(" and ")} respectively")
}

fun Char.toPriority(): Int = if (isLowerCase()) this - 'a' + 1 else this - 'A' + 27

fun stepOne(): Int =
     File("data/day_3.txt").useLines { lines ->
        val sections = lines.map {
            it.substring(0, it.length/2) to it.substring(it.length/2, it.length)
        }.toList()

        val commonItems = sections.map { (first, second) ->
            first.toCharArray().toSet().intersect(second.toCharArray().toSet())
        }

         commonItems.sumOf { it.single().toPriority() }
     }

fun stepTwo(): Int =
    File("data/day_3.txt").useLines { lines ->
        val groupLines = lines.chunked(3)
        val badgeSymbols = groupLines.map { lines -> lines.map { it.toCharArray().toSet() }.reduce { acc, next -> acc.intersect(next) }.single() }
        badgeSymbols.map { it.toPriority() }.sum()
    }