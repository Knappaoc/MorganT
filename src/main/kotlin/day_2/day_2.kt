package day_2

import measureFastestExecutionTimes
import java.io.File

fun main() {
    println("Answer 1: ${stepOne()}")
    println("Answer 2: ${stepTwo()}")
    println("Fastest execution times ${measureFastestExecutionTimes(listOf(::stepOne, ::stepTwo)).joinToString(" and ")} respectively")
}

enum class Move(val symbols: Set<String>, val value: Int) {
    ROCK(setOf("A", "X"), 1),
    PAPER(setOf("B", "Y"), 2),
    SCISSORS(setOf("C", "Z"), 3)
}
fun String.toMove() = Move.values().single { it.symbols.contains(this) }

enum class Outcome(val symbol: String, val value: Int) {
    WIN("Z", 6),
    DRAW("Y", 3),
    LOSE("X", 0)
}
fun String.toOutcome() = Outcome.values().single { it.symbol == this }

fun stepOne(): Int {

    fun calculateScore(game: Pair<Move, Move>): Int {
        val (opponentMove, move) = game
        val outcome = when (opponentMove) {
            Move.PAPER -> when (move){
                Move.PAPER -> Outcome.DRAW
                Move.SCISSORS -> Outcome.WIN
                Move.ROCK -> Outcome.LOSE
            }
            Move.SCISSORS -> when (move) {
                Move.PAPER -> Outcome.LOSE
                Move.SCISSORS -> Outcome.DRAW
                Move.ROCK -> Outcome.WIN
            }
            Move.ROCK -> when (move) {
                Move.PAPER -> Outcome.WIN
                Move.SCISSORS -> Outcome.LOSE
                Move.ROCK -> Outcome.DRAW
            }
        }
        return outcome.value + move.value
    }

    return File("data/day_2.txt").useLines { lines ->
        val games = lines.map { line ->
            val split = line.split(" ")
            split.let { it[0].toMove() to it[1].toMove() }
        }
        val scores = games.map { calculateScore(it) }
        scores.sum()
    }
}

fun stepTwo(): Int {

    fun calculateScore(game: Pair<Move, Outcome>): Int {
        val (opponentMove, outcome) = game;
        val winningMove = when(opponentMove){
            Move.PAPER -> {
                when (outcome){
                    Outcome.WIN -> Move.SCISSORS
                    Outcome.DRAW -> Move.PAPER
                    Outcome.LOSE -> Move.ROCK
                }
            }
            Move.SCISSORS -> {
                when (outcome){
                    Outcome.WIN -> Move.ROCK
                    Outcome.DRAW -> Move.SCISSORS
                    Outcome.LOSE -> Move.PAPER
                }
            }
            Move.ROCK -> {
                when (outcome){
                    Outcome.WIN -> Move.PAPER
                    Outcome.DRAW -> Move.ROCK
                    Outcome.LOSE -> Move.SCISSORS
                }
            }
        }
        return outcome.value + winningMove.value
    }

    return File("data/day_2.txt").useLines { lines ->
        val games = lines.map { line ->
            val split = line.split(" ")
            split.let { it[0].toMove() to it[1].toOutcome() }
        }
        val scores = games.map { calculateScore(it) }
        scores.sum()
    }
}