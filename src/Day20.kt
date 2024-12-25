import kotlin.math.abs
import kotlin.test.assertEquals

fun YX.valid(board: Array<CharArray>) = y in board.indices && x in board[0].indices

fun main() {
  val directions = listOf(YX(-1, 0), YX(0, -1), YX(0, 1), YX(1, 0))
  data class Input(val board: Array<CharArray>, val start: YX, val end: YX)
  fun parseInput(input: List<String>): Input {
    lateinit var start: YX
    lateinit var end: YX
    input.indices.forEach { y ->
      input[y].indices.forEach { x ->
        when (input[y][x]) {
          START -> start = YX(y, x)
          END -> end = YX(y, x)
        }
      }
    }
    val board = input.map(String::toCharArray).toTypedArray()
    return Input(board, start, end)
  }

  fun bfs(input: Input): Map<YX, Int> {
    val visited = mutableSetOf<YX>()
    val queue = mutableListOf<Pair<YX, Int>>()
    val results = mutableMapOf<YX, Int>()
    queue.add(input.start to 0)
    while (queue.isNotEmpty()) {
      val (node, distance) = queue.removeFirst()
      if (node in visited) continue
      visited.add(node)
      results[node] = distance
      directions.forEach { dyx ->
        val nyx = node + dyx
        if (nyx.valid(input.board) && input.board[nyx] != WALL && nyx !in visited) {
          queue.add(nyx to (distance + 1))
        }
      }
    }
    return results
  }

  fun solve(input: Input, minSave: Int, maxCheatLength: Int): Int {
    val results = bfs(input)
    val normalDistance = results[input.end]!!
    var result = 0
    val optimalPoints = mutableListOf<YX>()
    val queue = mutableListOf(input.end)
    while (queue.isNotEmpty()) {
      val node = queue.removeFirst()
      optimalPoints.add(node)
      directions.forEach { dyx ->
        val yx = node + dyx
        if (results[yx] == results[node]!! - 1) {
          queue.add(yx)
        }
      }
    }
    for (cheatStart in optimalPoints) {
      for (cheatEnd in optimalPoints) {
        val distance = abs(cheatStart.y - cheatEnd.y) + abs(cheatStart.x - cheatEnd.x)
        if (distance > maxCheatLength) continue
        val cheatResult = results[cheatStart]!! + (normalDistance - results[cheatEnd]!!) + distance
        val saved = normalDistance - cheatResult
        if (saved >= minSave) result++
      }
    }
    return result
  }

  fun part1(input: Input, minSave: Int) = solve(input, minSave, maxCheatLength = 2)
  fun part2(input: Input, minSave: Int) = solve(input, minSave, maxCheatLength = 20)

  val testInput = readInput("Day20_test")
  assertEquals(expected = 5, actual = part1(parseInput(testInput), minSave = 20))
  assertEquals(expected = 285, actual = part2(parseInput(testInput), minSave = 50))

  val input = readInput("Day20")
  part1(parseInput(input), minSave = 100).println()
  part2(parseInput(input), minSave = 100).println()
}
