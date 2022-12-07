package day_7

//import measureFastestExecutionTimes
import java.io.File
import kotlin.math.sign

fun main() {
    println("Answer 1: ${stepOne()}")
    println("Answer 2: ${stepTwo()}")
//    println("Fastest execution times ${measureFastestExecutionTimes(listOf(::stepOne, ::stepTwo)).joinToString(" and ")} respectively")
}

sealed class FSItem(open val name: String){
    abstract val size: Int
}
data class Directory(override val name: String, val parent: Directory?, val children: MutableList<FSItem>): FSItem(name){
    override fun toString(): String {
        return name;
    }

    override val size: Int
        get() = this.children.sumOf { it.size }
}
data class FSFile(override val name: String, override val size: Int): FSItem(name)

fun stepOne(): Int {
    File("data/day_7.txt").useLines { lines ->
        val filesystem = Directory("/", null, mutableListOf())
        var current = filesystem;
        for (line in lines){
            if (line.startsWith("$")){ // Command
                val tokens = line.split(" ");
                when(tokens[1]){
                    "cd" -> {
                        current = when (tokens[2]) {
                            "/" -> {
                                filesystem
                            }
                            ".." -> {
                                current.parent!!;
                            }
                            else -> {
                                current.children.filterIsInstance<Directory>().find { it.name == tokens[2] }!!
                            }
                        }
                    }
                    "ls" -> { }
                }
            } else {
                if (line.startsWith("dir")){
                    val (_, name) = line.split(" ");
                    current.children.add(Directory(name, current, mutableListOf()))
                } else {
                    val (sizeStr, name) = line.split(" ");
                    current.children.add(FSFile(name, sizeStr.toInt()))
                }
            }
        }

        val files = filesystem.children.toList().toMutableList();
        val queue = filesystem.children.filterIsInstance<Directory>().toMutableList()
        while(queue.isNotEmpty()){
            current = queue.removeFirst();
            files.addAll(current.children);
            queue.addAll(current.children.filterIsInstance<Directory>())
        }

        return files.filterIsInstance<Directory>().filter { it.size < 100000 }.sumOf { it.size }
    }
};

fun stepTwo(): Int {
    File("data/day_7.txt").useLines { lines ->
        val filesystem = Directory("/", null, mutableListOf())
        var current = filesystem;
        for (line in lines){
            if (line.startsWith("$")){ // Command
                val tokens = line.split(" ");
                when(tokens[1]){
                    "cd" -> {
                        current = when (tokens[2]) {
                            "/" -> {
                                filesystem
                            }
                            ".." -> {
                                current.parent!!;
                            }
                            else -> {
                                current.children.filterIsInstance<Directory>().find { it.name == tokens[2] }!!
                            }
                        }
                    }
                    "ls" -> { }
                }
            } else {
                if (line.startsWith("dir")){
                    val (_, name) = line.split(" ");
                    current.children.add(Directory(name, current, mutableListOf()))
                } else {
                    val (sizeStr, name) = line.split(" ");
                    current.children.add(FSFile(name, sizeStr.toInt()))
                }
            }
        }

        val files = filesystem.children.toList().toMutableList();
        val queue = filesystem.children.filterIsInstance<Directory>().toMutableList()
        while(queue.isNotEmpty()){
            current = queue.removeFirst();
            files.addAll(current.children);
            queue.addAll(current.children.filterIsInstance<Directory>())
        }

        val totalSpace = 70000000;
        val neededSpace = 30000000;

        val usedSpace = filesystem.size;
        val freeSpace = totalSpace - usedSpace;
        return files.filterIsInstance<Directory>().map { it.size }.sorted().first { freeSpace + it > neededSpace }
    }
}