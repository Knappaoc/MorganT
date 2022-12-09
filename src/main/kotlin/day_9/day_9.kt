package day_9

import measureFastestExecutionTimes
import java.io.File
import kotlin.math.abs


fun main() {
    println("Answer 1: ${stepOne()}")
    println("Answer 2: ${stepTwo()}")
//    println("Fastest execution times ${measureFastestExecutionTimes(listOf(::stepOne, ::stepTwo)).joinToString(" and ")} respectively")
}

data class Position(val x: Int = 0, val y: Int = 0)
fun Position.inContact(other: Position) = abs(x - other.x) <= 1 && abs(y - other.y) <= 1
data class State(val head: Position = Position(), val tail: Position = Position())

fun State.move(direction: String): State {
    val nextHead = when(direction){
        "R" -> head.copy(x= head.x + 1)
        "L" -> head.copy(x= head.x - 1)
        "U" -> head.copy(y= head.y + 1)
        "D" -> head.copy(y= head.y - 1)
        else -> throw Error("Unexpected direction encountered \"$direction\"")
    }
    val nextTail = if (!nextHead.inContact(tail)) when {
        nextHead.x == tail.x && nextHead.y != tail.y -> {
            tail.copy(y = tail.y + if (nextHead.y > tail.y) 1 else -1 )
        }
        nextHead.x != tail.x && nextHead.y == tail.y -> {
            tail.copy(x = tail.x + if (nextHead.x > tail.x) 1 else -1 )
        }
        else -> {
            tail.copy( // Move diagonally
                x = tail.x + if (nextHead.x > tail.x) 1 else -1,
                y = tail.y + if (nextHead.y > tail.y) 1 else -1
            )
        }
    } else{
        tail.copy()
    }
    return State(nextHead, nextTail)
}

fun stepOne(): Int  {
    val moves = File("data/day_9.txt").readLines().map { it.split(" ") }.map { it[0] to it[1].toInt() }
    val steps = moves.flatMap { (direction, amount) -> List(amount) { direction } }

    val simulation = steps.scan(State()) { state, direction -> state.move(direction)}

    return simulation.map { it.tail }.toSet().size;
}

data class SnappedState(val knots: List<Position> = List(10) { Position() })

fun Position.trail(parent: Position) =
    if (!parent.inContact(this)) when {
        parent.x == x && parent.y != y -> {
            copy(y = y + if (parent.y > y) 1 else -1 )
        }
        parent.x != x && parent.y == y -> {
            copy(x = x + if (parent.x > x) 1 else -1 )
        }
        else -> {
            copy( // Move diagonally
                x = x + if (parent.x > x) 1 else -1,
                y = y + if (parent.y > y) 1 else -1
            )
        }
    } else{
        copy()
    }


fun SnappedState.move(direction: String): SnappedState {
    val head = knots.first()
    val nextHead = when(direction){
        "R" -> head.copy(x= head.x + 1)
        "L" -> head.copy(x= head.x - 1)
        "U" -> head.copy(y= head.y + 1)
        "D" -> head.copy(y= head.y - 1)
        else -> throw Error("Unexpected direction encountered \"$direction\"")
    }

    val nextKnots = mutableListOf<Position>(nextHead)
    for(index in 1 until knots.size){
        nextKnots.add(knots[index].trail(nextKnots.last()));
    }
    return SnappedState(nextKnots.toList())
}


fun stepTwo(): Int {
    val moves = File("data/day_9.txt").readLines().map { it.split(" ") }.map { it[0] to it[1].toInt() }
    val steps = moves.flatMap { (direction, amount) -> List(amount) { direction } }

    val simulation = steps.scan(SnappedState()) { state, direction -> state.move(direction)}

    return simulation.map { it.knots.last() }.toSet().size; //2717
}