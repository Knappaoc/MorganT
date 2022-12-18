package day_10

import measureFastestExecutionTimes
import java.io.File

fun main() {
    println("Answer 1: ${stepOne()}") // 15120
    println("Answer 2:\n${stepTwo()}") // RKPJBPLA
    println("Fastest execution times ${measureFastestExecutionTimes(listOf(::stepOne, ::stepTwo)).joinToString(" and ")} respectively")
}

sealed interface Instruction {
    val cycles: Int
}

class Noop : Instruction {
    override val cycles: Int = 1
}

data class AddX(val arg: Int) : Instruction {
    override val cycles: Int = 2
}

data class StateX(val cycle: Int, val x: Int) {
    fun apply(instruction: Instruction) =
        when (instruction) {
            is Noop -> this.copy(cycle = cycle + instruction.cycles)
            is AddX -> this.copy(cycle = cycle + instruction.cycles, x = x + instruction.arg)
        }
}

fun loadProgram(path: String) = File(path).readLines().map { line ->
    val tokens = line.split(" ");
    when (tokens.first()) {
        "noop" -> Noop();
        "addx" -> AddX(tokens.last().toInt())
        else -> throw Error("Problem parsing program line \"$line\"")
    }
}

fun stepOne(): Int {
    val program = loadProgram("data/day_10.txt")
    val evolution = program.scan(StateX(0, 1)) { acc, instruction -> acc.apply(instruction) }
    val signalStrengths = (20..220 step 40).map { cycle -> evolution.last { state -> state.cycle < cycle }.x * cycle }
    return signalStrengths.sum();
}

fun stepTwo(): String {
    val program = loadProgram("data/day_10.txt")
    val evolution = program.scan(StateX(0, 1)) { acc, instruction -> acc.apply(instruction) }
    val height = 6; val width = 40
    val screen = (1..(width * height)).joinToString("") { cycle ->
        val x = evolution.last { state -> state.cycle < cycle }.x;
        val px = (cycle - 1) % width
        if (x in (px - 1)..(px + 1)) "#" else "."
    }.chunked(width).joinToString("\n")
    return screen;
}
