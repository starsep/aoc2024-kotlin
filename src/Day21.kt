import kotlin.test.assertEquals

val numericKeyboard =
    mapOf(
        '7' to YX(0, 0),
        '8' to YX(0, 1),
        '9' to YX(0, 2),
        '4' to YX(1, 0),
        '5' to YX(1, 1),
        '6' to YX(1, 2),
        '1' to YX(2, 0),
        '2' to YX(2, 1),
        '3' to YX(2, 2),
        ' ' to YX(3, 0),
        '0' to YX(3, 1),
        'A' to YX(3, 2),
    )
val directionalKeyboard =
    mapOf(
        ' ' to YX(0, 0),
        '^' to YX(0, 1),
        'A' to YX(0, 2),
        '<' to YX(1, 0),
        'v' to YX(1, 1),
        '>' to YX(1, 2),
    )

fun String.repeat2(count: Int): String = if (count < 0) "" else this.repeat(count)

fun main() {
  val pathCache = mutableMapOf<Pair<Char, Char>, String>()
  fun path(start: Char, end: Char): String {
    pathCache[start to end]?.let {
      return@path it
    }
    val keyboard =
        if (start in numericKeyboard && end in numericKeyboard) numericKeyboard
        else directionalKeyboard
    val dyx = keyboard[end]!! - keyboard[start]!!
    val verticalMoves = "^".repeat2(-dyx.y) + "v".repeat2(dyx.y)
    val horizontalMoves = "<".repeat2(-dyx.x) + ">".repeat2(dyx.x)
    val throughEmpty = keyboard[' ']!! - keyboard[start]!!
    val verticalFirst =
        (dyx.x > 0 || (throughEmpty.y == 0 && throughEmpty.x == dyx.x)) && throughEmpty.y != dyx.y
    return ((if (verticalFirst) verticalMoves + horizontalMoves
        else horizontalMoves + verticalMoves) + "A")
        .also { pathCache[start to end] = it }
  }
  assertEquals("A", path('A', 'A'))
  assertEquals("^^>>A", path('1', '9'))
  assertEquals(">>^A", path('<', 'A'))
  assertEquals("<A", path('>', 'v'))
  assertEquals("<<^^A", path('3', '7'))

  val shortestCache = mutableMapOf<Pair<String, Int>, Long>()
  fun shortest(moves: String, depth: Int): Long {
    if (depth == 0) return moves.length.toLong()
    shortestCache[moves to depth]?.let {
      return@shortest it
    }
    return moves
        .withIndex()
        .sumOf { (index, move) ->
          val from = if (index == 0) 'A' else moves[index - 1]
          shortest(path(from, move), depth - 1)
        }
        .also { shortestCache[moves to depth] = it }
  }
  fun solve(input: List<String>, depth: Int): Long =
      input.sumOf { it.dropLast(1).toLong() * shortest(it, depth) }

  fun part1(input: List<String>) = solve(input, 3)
  fun part2(input: List<String>) = solve(input, 26)

  val testInput = readInput("Day21_test")
  assertEquals(expected = 126384L, actual = part1(testInput))
  assertEquals(expected = 175343041201758L, actual = part2(testInput))

  val input = readInput("Day21")
  part1(input).println()
  part2(input).println()
}
