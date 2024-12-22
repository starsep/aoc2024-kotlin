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

  fun bfs(input: Input, maxDistance: Int, mustVisitFirst: YX, mustVisit: YX): Int? {
    val visited = mutableSetOf<YX>()
    val queue = mutableListOf<Pair<YX, Int>>()
    queue.add(input.start to 0)
    var best: Int? = null
    while (queue.isNotEmpty()) {
      val (node, distance) = queue.removeFirst()
      if (distance > maxDistance) return null
      if (node in visited) continue
      visited.add(node)
      if (node == mustVisit && mustVisitFirst !in visited) return null
      if (node == input.end) {
        if (mustVisit in visited) {
          if (best != null && best < distance) return null
          return distance
        }
        best = distance
      }
      directions.forEach { dyx ->
        val nyx = node + dyx
        if (nyx.valid(input.board) && input.board[nyx] != WALL && nyx !in visited) {
          queue.add(nyx to (distance + 1))
        }
      }
    }
    return null
  }

  fun part1(input: Input, minSave: Int): Int {
    val normalResult = bfs(input, Int.MAX_VALUE, input.start, input.end)!!
    val maxDistance = normalResult - minSave
    var result = 0
    input.board.indices.forEach { y ->
      input.board[y].indices.forEach { x ->
        val yx = YX(y, x)
        if (input.board[yx] == WALL) {
          input.board[yx] = EMPTY
          directions.forEach { dyx ->
            val nyx = yx + dyx
            if (nyx.valid(input.board)) {
              // val previous = input.board[nyx]
              // input.board[nyx] = EMPTY
              val cheatResult = bfs(input, maxDistance, yx, nyx)
              if (cheatResult != null) {
                result++
                println("$yx, $nyx, $cheatResult")
              }
              // input.board[nyx] = previous
            }
          }
          input.board[yx] = WALL
        }
      }
    }
    return result
  }

  fun part2(input: Input): Int = 42

  val testInput = readInput("Day20_test")
  assertEquals(expected = 5, actual = part1(parseInput(testInput), 20))
  assertEquals(expected = 42, actual = part2(parseInput(testInput)))

  val input = readInput("Day20")
  part1(parseInput(input), minSave = 100).println()
  part2(parseInput(input)).println()
}
