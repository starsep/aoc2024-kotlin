import kotlin.math.abs

fun main() {
  fun String.parse(): List<Int> = split(" ").map(String::toInt)

  fun List<Int>.checkRow(): Boolean =
      zipWithNext().all { (x, y) -> abs(x - y) in 1..3 } &&
          (this == sorted() || this == sortedDescending())

  fun part1(input: List<String>): Int = input.count { row -> row.parse().checkRow() }

  fun List<Int>.withoutIndex(index: Int) = take(index) + takeLast(size - index - 1)

  fun List<Int>.checkPart2(): Boolean {
    if (checkRow()) return true
    if (dropLast(1).checkRow()) return true
    if (size <= 2) {
      return indices.any { i -> withoutIndex(i).checkRow() }
    }

    var badIndex: Int? = null
    for (i in 0..(size - 2)) {
      if (abs(this[i] - this[i + 1]) !in 1..3) {
        badIndex = i
      }
    }
    if (badIndex != null) {
      return withoutIndex(badIndex).checkRow() || withoutIndex(badIndex + 1).checkRow()
    }
    val ascending = zipWithNext().count { (x, y) -> x < y }
    val decreasing = zipWithNext().count { (x, y) -> x > y }
    if (ascending > decreasing) {
      val index = indices.drop(1).firstOrNull { i -> this[i - 1] >= this[i] } ?: return false
      return withoutIndex(index - 1).checkRow()
    } else {
      val index = indices.drop(1).firstOrNull { i -> this[i - 1] <= this[i] } ?: return false
      return withoutIndex(index - 1).checkRow()
    }
  }

  fun part2(input: List<String>): Int = input.map(String::parse).count(List<Int>::checkPart2)

  val testInput = readInput("Day02_test")
  check(part1(testInput) == 2)
  check(part2(testInput) == 4)

  val input = readInput("Day02")
  part1(input).println()
  part2(input).println()
}
