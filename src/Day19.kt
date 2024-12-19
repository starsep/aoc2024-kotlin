import kotlin.test.assertEquals

fun main() {
  data class Input(val towels: List<String>, val designs: List<String>)
  fun parseInput(input: List<String>) =
      Input(towels = input.first().split(",").map(String::trim), designs = input.drop(2))
  val cache = mutableMapOf<String, Long>()

  fun String.ways(towels: List<String>): Long {
    cache[this]?.let {
      return it
    }
    if (isEmpty()) return 1
    if (length == 1) {
      cache[this] = 0
      return 0
    }
    return towels
        .filter(this::startsWith)
        .sumOf { substring(it.length).ways(towels) }
        .also { cache[this] = it }
  }

  fun part1(input: Input): Int {
    input.towels.forEach { if (it.length == 1) cache[it] = 1 }
    return input.designs.count { it.ways(input.towels) > 0 }
  }

  fun part2(input: Input): Long {
    input.towels.forEach { if (it.length == 1) cache[it] = 1 }
    return input.designs.sumOf { it.ways(input.towels) }
  }

  val testInput = parseInput(readInput("Day19_test"))
  assertEquals(expected = 6, actual = part1(testInput))
  assertEquals(expected = 16, actual = part2(testInput))
  cache.clear()

  val input = parseInput(readInput("Day19"))
  part1(input).println()
  part2(input).println()
}
