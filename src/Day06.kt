import kotlin.test.assertEquals

val directions =
    mapOf("up" to (-1 to 0), "right" to (0 to 1), "down" to (1 to 0), "left" to (0 to -1))
val turn = mapOf("up" to "right", "right" to "down", "down" to "left", "left" to "up")

data class State(
    val board: List<CharArray>,
    val position: Pair<Int, Int>,
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

fun List<CharArray>.findPosition(): Pair<Int, Int> {
  for (y in indices) {
    for (x in this[y].indices) {
      if (this[y][x] == '^') {
        return y to x
      }
    }
  }
  throw Exception("Couldn't find start position")
}

fun parseInput(input: List<String>): State {
  val board = input.map(String::toCharArray)
  val position = board.findPosition()
  return State(board = board, position = position, direction = "up")
}

fun Pair<Int, Int>.x() = second

fun Pair<Int, Int>.y() = first

fun Pair<Int, Int>.nextPosition(direction: String): Pair<Int, Int> {
  val (dy, dx) = directions[direction]!!
  return (y() + dy) to (x() + dx)
}

fun Pair<Int, Int>.valid(board: List<CharArray>) = y() in board.indices && x() in board[0].indices

const val Looping = -1

fun solve(state: State): Int {
  var direction = state.direction
  var position = state.position
  val board = state.board
  var result = 0
  data class VisitedKey(val y: Int, val x: Int, val direction: String)
  val visited = mutableSetOf<VisitedKey>()
  while (position.valid(board)) {
    val visitedKey = VisitedKey(position.y(), position.x(), direction)
    if (visitedKey in visited) return Looping
    visited.add(visitedKey)
    if (board[position.y()][position.x()] != 'X') {
      result++
      board[position.y()][position.x()] = 'X'
    }
    for (i in directions.keys) {
      val potentialPosition = position.nextPosition(direction)
      if (!potentialPosition.valid(board)) return result
      if (board[potentialPosition.y()][potentialPosition.x()] !in setOf('#', 'O')) {
        position = potentialPosition
        break
      }
      direction = turn[direction]!!
    }
  }
  return result
}

fun part1(input: List<String>): Int {
  return solve(parseInput(input))
}

fun part2(input: List<String>): Int {
  val state = parseInput(input)
  solve(state)
  val potentialObstructions =
      state.board.withIndex().flatMap { (y, row) ->
        row.indices
            .filter { x -> state.board[y][x] == 'X' && y to x != state.position }
            .map { x -> y to x }
      }
  return potentialObstructions.count { (y, x) ->
    state.board[y][x] = 'O'
    val result = solve(state) == Looping
    state.board[y][x] = 'X'
    return@count result
  }
}

fun main() {
  val testInput = readInput("Day06_test")
  assertEquals(actual = part1(testInput), expected = 41)
  assertEquals(actual = part2(testInput), expected = 6)

  val input = readInput("Day06")
  part1(input).println()
  part2(input).println()
}
