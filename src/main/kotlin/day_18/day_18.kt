package day_18

import java.io.File

fun main() {
    println("Answer 1: ${stepOne("data/day_18.txt")}")
    println("Answer 2: ${stepTwo("data/day_18.txt")}")
}

data class Vector3D(val x: Int, val y: Int, val z: Int) {
    fun faceNeighbours() = setOf<Vector3D>(
        Vector3D(x - 1, y, z),
        Vector3D(x + 1, y, z),
        Vector3D(x, y - 1, z),
        Vector3D(x, y + 1, z),
        Vector3D(x, y, z - 1),
        Vector3D(x, y, z + 1),
    )
}

fun loadData(path: String) = File(path).useLines { lines ->
    lines.map { line ->
        line.split(",").map { it.toInt() }
    }
        .map { (x, y, z) -> Vector3D(x, y, z) }
        .toList()
}

fun stepOne(path: String): Int {
    val voxels = loadData(path).toSet()
    return voxels.sumOf { it.faceNeighbours().count { neighbour -> neighbour !in voxels } }
}

data class Bounds3D(val xs: IntRange, val ys: IntRange, val zs: IntRange){
    val firstCorner
        get() = Vector3D(xs.first, ys.first, zs.first)
}
fun Vector3D.expandToVolume(exclude: Set<Vector3D>, bounds: Bounds3D): Set<Vector3D> {
    val volume = mutableSetOf<Vector3D>();
    var expansion = setOf(this)
    while (expansion.isNotEmpty()){
        volume.addAll(expansion)
        expansion = expansion
            .flatMap { it.faceNeighbours() }
            .filter { it !in exclude }
            .filter { it.x in bounds.xs && it.y in bounds.ys && it.z in bounds.zs }
            .toSet() - volume;
    }
    return volume.toSet();
}

fun stepTwo(path: String): Int {
    val dropletVoxels = loadData(path).toSet()
    val neighbouringVoxels = dropletVoxels.fold(emptySet<Vector3D>()) { acc, voxel -> acc + voxel.faceNeighbours()} - dropletVoxels;
    val boundary = neighbouringVoxels.run { Bounds3D(minOf { it.x }..maxOf { it.y }, minOf { it.x }..maxOf { it.y }, minOf { it.z }..maxOf { it.z }, ) }
    val surface = boundary.firstCorner.expandToVolume(dropletVoxels, boundary)
//
//    val volumes = mutableSetOf<Set<Vector3D>>()
//    for (neighbour in neighbouringVoxels){
//        if (volumes.any { neighbour in it }) continue;
//        volumes.add(neighbour.expandToVolume(dropletVoxels, boundary))
//    }
//    val surface = volumes.single { boundary.firstCorner in it }
//    val internalVolumes = volumes - surface;

    return dropletVoxels.sumOf { it.faceNeighbours().count { neighbour -> neighbour in surface } }
}