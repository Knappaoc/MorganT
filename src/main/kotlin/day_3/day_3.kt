package day_3

import measureFastestExecutionTimes
import java.io.File

fun main() {
    println("Answer 1: ${stepOne()}")
    println("Answer 2: ${stepTwo()}")
    println("Fastest execution times ${measureFastestExecutionTimes(listOf(::stepOne, ::stepTwo)).joinToString(" and ")} respectively")
}

fun Char.toPriority(): Int = if (isLowerCase()) this - 'a' + 1 else this - 'A' + 27

fun stepOne(): Int =
    File("data/day_3.txt").useLines { rucksacks ->
        val sections = rucksacks.map { line -> line.run { substring(0, length / 2) to substring(length / 2, length) } }
        val commonItemTypes = sections.map { (first, second) -> listOf(first, second).map { it.toCharArray().toSet() }.commonElements().single() }
        commonItemTypes.sumOf { it.toPriority() }
    }

fun stepTwo(): Int =
    File("data/day_3.txt").useLines { rucksacks ->
        rucksacks
            .chunked(3)
            .map { groupRucksacks ->
                groupRucksacks
                    .map { rucksack -> rucksack.toCharArray().toSet() }
                    .commonElements()
                    .single()
            }
            .sumOf { it.toPriority() }
    }

fun <T> List<Set<T>>.commonElements() = reduce { acc, next -> acc.intersect(next) }