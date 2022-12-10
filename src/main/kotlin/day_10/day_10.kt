package day_10

import java.io.File

fun main() {
//    println("Answer 1: ${stepOne()}")
    println("Answer 2: ${stepTwo()}")
//    println("Fastest execution times ${measureFastestExecutionTimes(listOf(::stepOne, ::stepTwo)).joinToString(" and ")} respectively")
}

data class State(val cycle: Int, val x: Int, val dx: Int = 0)

fun stepOne(): Int {
    val instructions = File("data/day_10.txt").readLines()

    val evolution = instructions.fold(mutableListOf<State>(State(0, 1))) { acc, s ->
        val settled = acc.last().let { it.copy(x = it.x + it.dx, dx = 0) }
        if (s == "noop"){
            acc.add(settled.copy(cycle = settled.cycle + 1))
        } else {
            val arg = s.split(" ").last().toInt()
            acc.addAll(listOf(
                settled.copy(cycle = settled.cycle + 1),
                settled.copy(cycle = settled.cycle + 2, dx= arg))
            )
        }
        acc
    }

    val signalStrengths = (20..220 step 40).map {cycle ->
        val (_, x) = evolution.find { it.cycle == cycle }!!;
        cycle * x
    }

    return signalStrengths.sum();
}

fun stepTwo() {
    val instructions = File("data/day_10.txt").readLines()

    val evolution = instructions.fold(mutableListOf<State>(State(0, 1))) { acc, s ->
        val settled = acc.last().let { it.copy(x = it.x + it.dx, dx = 0) }
        if (s == "noop"){
            acc.add(settled.copy(cycle = settled.cycle + 1))
        } else {
            val arg = s.split(" ").last().toInt()
            acc.addAll(listOf(
                settled.copy(cycle = settled.cycle + 1),
                settled.copy(cycle = settled.cycle + 2, dx= arg))
            )
        }
        acc
    }

    val height = 6; val width = 40;
    val screen = List(height){ List(width){ "." }.toMutableList() }
    for (state in evolution.drop(1)){
        val (cycle, x) = state;
        val (px, py) = (cycle - 1) % 40 to (cycle - 1) / 40
        if (x in listOf(px - 1, px, px + 1)){
            screen[py][px] = "#";
        }
    }
    println(screen.joinToString("\n") { it.joinToString("") })
}

//fun stepTwo(): Int {
//    val input = File("data/test.txt").readLines()
//    return 1;
//}
