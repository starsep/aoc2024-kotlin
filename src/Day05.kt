import kotlin.test.assertEquals

const val MAX = 100

fun main() {
  fun parseInput(input: List<String>): Pair<Array<BooleanArray>, List<List<Int>>> {
    val matrix = Array(MAX) { BooleanArray(MAX) }
    val (rules, updateLines) = input.filter(String::isNotBlank).partition { "|" in it }
    for (rule in rules) {
      val (before, after) = rule.split("|").map(String::toInt)
      matrix[before][after] = true
    }
    val updates = updateLines.map { it.split(",").map(String::toInt) }
    return matrix to updates
  }

  fun List<Int>.middle(): Int = this[size / 2]

  fun List<Int>.updateOk(matrix: Array<BooleanArray>): Boolean =
      withIndex().all { (index, x) -> drop(index + 1).all { y -> !matrix[y][x] } }

  fun part1(input: List<String>): Int {
    val (matrix, updates) = parseInput(input)
    return updates.sumOf { if (it.updateOk(matrix)) it.middle() else 0 }
  }

  fun List<Int>.reorder(matrix: Array<BooleanArray>): List<Int> = sortedWith { x, y ->
    if (matrix[x][y]) -1 else if (matrix[y][x]) 1 else 0
  }

  fun part2(input: List<String>): Int {
    val (matrix, updates) = parseInput(input)
    return updates.sumOf { if (!it.updateOk(matrix)) it.reorder(matrix).middle() else 0 }
  }

  val testInput = readInput("Day05_test")
  assertEquals(actual = part1(testInput), expected = 143)
  assertEquals(actual = part2(testInput), expected = 123)

  val input = readInput("Day05")
  part1(input).println()
  part2(input).println()
}
