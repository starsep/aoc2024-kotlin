import kotlin.test.assertEquals

typealias Key = List<Int>

typealias Lock = List<Int>

fun main() {
  val width = 5
  data class Input(val locks: List<Lock>, val keys: List<Key>)
  fun parseInput(input: List<String>): Input {
    val locks = mutableListOf<Lock>()
    val keys = mutableListOf<Key>()
    var lock: Boolean? = null
    val current = IntArray(width)
    for (row in input) {
      if (row.isBlank()) {
        (if (lock == true) locks else keys).add(current.toList())
        for (i in current.indices) current[i] = 0
        lock = null
        continue
      }
      if (lock == null) {
        lock = row[0] == WALL
      }
      for ((index, column) in row.withIndex()) {
        current[index] += if (column == WALL) 1 else 0
      }
    }
    (if (lock == true) locks else keys).add(current.toList())
    return Input(locks, keys)
  }

  val height = 7
  fun part1(input: Input): Int {
    var result = 0
    for (key in input.keys) {
      for (lock in input.locks) {
        var ok = true
        for (index in lock.indices) {
          if (key[index] + lock[index] > height) {
            ok = false
            break
          }
        }
        if (ok) result++
      }
    }
    return result
  }

  fun part2(input: Input) = 42

  val testInput = readInput("Day25_test")
  assertEquals(expected = 3, actual = part1(parseInput(testInput)))
  assertEquals(expected = 42, actual = part2(parseInput(testInput)))

  val input = readInput("Day25")
  part1(parseInput(input)).println()
  part2(parseInput(input)).println()
}
