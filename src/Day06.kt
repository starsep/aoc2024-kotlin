import kotlin.test.assertEquals

val directions =
    mapOf("up" to YX(-1, 0), "right" to YX(0, 1), "down" to YX(1, 0), "left" to YX(0, -1))
val turn = mapOf("up" to "right", "right" to "down", "down" to "left", "left" to "up")

data class State(
    val board: List<CharArray>,
    val position: YX,
    val direction: String,
) {
  fun debugBoard() {
    board.forEach { row ->
      row.forEach { character -> print(character) }
      println("")
    }
    println("")
  }
}

fun List<CharArray>.findPosition(): YX {
  for (y in indices) {
    for (x in this[y].indices) {
      if (this[y][x] == '^') {
        return YX(y, x)
      }
    }
  }
  throw Exception("Couldn't find start position")
}

fun parseInputDay6(input: List<String>): State {
  val board = input.map(String::toCharArray)
  val position = board.findPosition()
  return State(board = board, position = position, direction = "up")
}

fun YX.nextPosition(direction: String): YX = this + directions[direction]!!

fun YX.valid(board: List<CharArray>) = y in board.indices && x in board[0].indices

const val Looping = -1

fun solve(state: State): Int {
  var direction = state.direction
  var position = state.position
  val board = state.board
  var result = 0
  data class VisitedKey(val y: Int, val x: Int, val direction: String)
  val visited = mutableSetOf<VisitedKey>()
  while (position.valid(board)) {
    val visitedKey = VisitedKey(position.y, position.x, direction)
    if (visitedKey in visited) return Looping
    visited.add(visitedKey)
    if (board[position.y][position.x] != 'X') {
      result++
      board[position.y][position.x] = 'X'
    }
    for (i in directions.keys) {
      val potentialPosition = position.nextPosition(direction)
      if (!potentialPosition.valid(board)) return result
      if (board[potentialPosition.y][potentialPosition.x] !in setOf('#', 'O')) {
        position = potentialPosition
        break
      }
      direction = turn[direction]!!
    }
  }
  return result
}

fun main() {
  fun part1(input: List<String>): Int {
    return solve(parseInputDay6(input))
  }

  fun part2(input: List<String>): Int {
    val state = parseInputDay6(input)
    solve(state)
    val potentialObstructions =
        state.board.withIndex().flatMap { (y, row) ->
          row.indices
              .filter { x -> state.board[y][x] == 'X' && YX(y, x) != state.position }
              .map { x -> y to x }
        }
    return potentialObstructions.count { (y, x) ->
      state.board[y][x] = 'O'
      val result = solve(state) == Looping
      state.board[y][x] = 'X'
      return@count result
    }
  }

  val testInput = readInput("Day06_test")
  assertEquals(actual = part1(testInput), expected = 41)
  assertEquals(actual = part2(testInput), expected = 6)

  val input = readInput("Day06")
  part1(input).println()
  part2(input).println()
}
