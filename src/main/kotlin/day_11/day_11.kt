package day_11

import measureFastestExecutionTimes
import java.io.File
import java.math.BigInteger
import kotlin.time.*
import kotlin.to

enum class Operation(val symbol: String){
    Add("+"),
    Multiply("*"),
    Square("^");
    companion object {
        fun fromSymbol(symbol: String) = Operation.values().single{ it.symbol == symbol }
    }
}
data class MonkeyOperation(val type: Operation, val argument: Int? = null){
    fun apply(x: Long) = when(type){
        Operation.Add -> x + argument!!
        Operation.Multiply -> x * argument!!
        Operation.Square -> x * x
    }
}
data class MonkeyTest(val value: Int, val passedTarget: Int, val failedTarget: Int)

data class Monkey(val worries: MutableList<Long>, val operation: MonkeyOperation, val test: MonkeyTest){

    fun inspect(item: Long, relax: Boolean): Pair<Long, Int> {
        val newWorry = operation.apply(item).let { if (relax) it / 3 else it }
        val target = if ((newWorry % (test.value)) == 0L ) test.passedTarget else test.failedTarget
        return newWorry to target
    }
}

fun main() {
    println("Answer 1: ${stepOne()}")
    println("Answer 2: ${stepTwo()}") // !14399879944
//    println("Fastest execution times ${measureFastestExecutionTimes(listOf(::stepOne, ::stepTwo)).joinToString(" and ")} respectively")
}

fun loadData(path: String) = File(path).readText().split("\r\n\r\n").map { description ->
    val (idLine, itemLine, operationLine, testLine, passedLine, failedLine) = description.lines()
    val worries = itemLine.removePrefix("  Starting items: ").split(", ").map { it.toLong() }
    val operation = operationLine.removePrefix("  Operation: new = old ").split(" ").let {
        val operation = Operation.fromSymbol(it[0]);
        val argument = it[1].toIntOrNull();
        if (argument == null && operation == Operation.Multiply) MonkeyOperation(Operation.Square) else MonkeyOperation(Operation.fromSymbol(it[0]), it[1].toInt())
    }
    val divisor = testLine.removePrefix("  Test: divisible by ").toInt()
    val passedTarget = passedLine.removePrefix("    If true: throw to monkey ").toInt()
    val failedTarget = failedLine.removePrefix("    If false: throw to monkey ").toInt()
    Monkey(worries.toMutableList(), operation, MonkeyTest(divisor, passedTarget, failedTarget))
}

fun stepOne(): Long {
    val monkeys = loadData("data/day_11.txt").toMutableList()
    val count = List(monkeys.size) { 0L }.toMutableList()

    for (round in 1..20){
        for (monkeyIdx in 0 until monkeys.size){
            val monkey = monkeys[monkeyIdx]
            count[monkeyIdx] = count[monkeyIdx] + monkey.worries.size
            for (itemWorry in monkey.worries){
                val (newWorry, targetIdx) = monkey.inspect(itemWorry, true)
                monkeys[targetIdx].worries.add(newWorry);
            }
            monkey.worries.clear()
        }
    }
    return count.toList().sorted().takeLast(2).reduce { acc, value -> acc * value };
}

fun stepTwo(): Long {
    val monkeys = loadData("data/day_11.txt").toMutableList()
    // Looked at a hint for this, need to prevent the worry values from getting too large
    // because all the tests are just looking at remainders, map them to equivalent values by making sure none
    // go over the common divisor

    val commonDivisor = monkeys.map { it.test.value }.reduce{acc, it -> acc * it}
    val count = List(monkeys.size) { 0L }.toMutableList()

    for (round in 1..10000){
        for (monkeyIdx in 0 until monkeys.size){
            val monkey = monkeys[monkeyIdx]
            count[monkeyIdx] = count[monkeyIdx] + monkey.worries.size
            for (itemWorry in monkey.worries){
                val (newWorry, targetIdx) = monkey.inspect(itemWorry, false)
                val targetMonkey = monkeys[targetIdx];
                val scaledWorry = newWorry % commonDivisor;
                targetMonkey.worries.add(scaledWorry);
            }
            monkey.worries.clear()
        }
        if (round in listOf(1, 20, 1000, 2000)) println("$round: $count")
    }
    return count.toList().sorted().takeLast(2).reduce { acc, value -> acc * value };
}

private operator fun <E> List<E>.component6(): E = this[5]