import kotlin.test.assertEquals

fun main() {
  fun part1(input: List<String>): Int {
    val word = "XMAS"
    val directions = listOf(0 to 1, 1 to 0, -1 to 0, 0 to -1, -1 to -1, -1 to 1, 1 to -1, 1 to 1)
    return input.withIndex().sumOf { (y, row) ->
      row.withIndex().sumOf { (x, character) ->
        if (character == word[0]) {
          directions.count { (dy, dx) ->
            val indices = (1 ..< word.length).map { (y + it * dy) to (x + it * dx) }
            indices.last().run { first in input.indices && second in row.indices } &&
                indices.withIndex().all { (index, yx) ->
                  input[yx.first][yx.second] == word[index + 1]
                }
          }
        } else {
          0
        }
      }
    }
  }

  fun part2(input: List<String>): Int =
      input.withIndex().drop(1).dropLast(1).sumOf { (y, row) ->
        row.withIndex().drop(1).dropLast(1).count { (x, character) ->
          character == 'A' &&
              setOf(input[y - 1][x - 1], input[y + 1][x + 1]) == setOf('M', 'S') &&
              setOf(input[y - 1][x + 1], input[y + 1][x - 1]) == setOf('M', 'S')
        }
      }

  val testInput = readInput("Day04_test")
  assertEquals(actual = part1(testInput), expected = 18)
  assertEquals(actual = part2(testInput), expected = 9)

  val input = readInput("Day04")
  part1(input).println()
  part2(input).println()
}
