import kotlin.test.assertEquals

const val WALL = '#'
const val BOX = 'O'
const val LEFT_BOX = '['
const val RIGHT_BOX = ']'
const val ROBOT = '@'
const val EMPTY = '.'

fun main() {
  val moves = mapOf('v' to YX(1, 0), '>' to YX(0, 1), '<' to YX(0, -1), '^' to YX(-1, 0))

  class Input(val board: List<String>, val moves: List<Char>)

  fun findEmpty(board: Array<CharArray>, position: YX, direction: YX): YX? {
    var current = position
    while (board[current] == BOX) {
      current += direction
    }
    return when (board[current]) {
      EMPTY -> current
      else -> null
    }
  }

  fun MutableBoard.findRobot(): YX {
    indices.forEach { y ->
      this[y].indices.forEach { x ->
        if (this[y][x] == ROBOT) {
          this[y][x] = EMPTY
          return YX(y, x)
        }
      }
    }
    throw Exception("Couldn't find robot")
  }

  fun MutableBoard.gpsSum() =
      indices.sumOf { y ->
        this[y].indices.sumOf { x ->
          when (this[y][x]) {
            BOX,
            LEFT_BOX -> 100 * y + x
            else -> 0
          }
        }
      }

  fun List<String>.wideWarehouse(): MutableBoard =
      map { row ->
            row.flatMap {
                  when (it) {
                    BOX -> listOf(LEFT_BOX, RIGHT_BOX)
                    ROBOT -> listOf(ROBOT, EMPTY)
                    WALL -> listOf(WALL, WALL)
                    EMPTY -> listOf(EMPTY, EMPTY)
                    else -> throw Exception("Unexpected $it")
                  }
                }
                .toCharArray()
          }
          .toTypedArray()

  fun moveWide(board: Array<CharArray>, start: YX, direction: YX): Boolean {
    val affectedPositions = mutableListOf(start)
    var i = 0
    while (i < affectedPositions.size) {
      val position = affectedPositions[i]
      when (val c = board[position]) {
        WALL -> return false
        EMPTY -> {}
        LEFT_BOX,
        RIGHT_BOX -> {
          val side = position + YX(0, if (c == LEFT_BOX) 1 else -1)
          if (side !in affectedPositions) {
            affectedPositions.add(side)
          }
          val next = position + direction
          if (board[next] == WALL) return false
          if (board[next] != EMPTY && next !in affectedPositions) {
            affectedPositions.add(next)
          }
        }
      }
      i++
    }
    affectedPositions.reversed().forEach { position ->
      val next = position + direction
      board[next] = board[position]
      board[position] = EMPTY
    }
    return true
  }

  fun solve(input: Input, wide: Boolean): Int {
    val board =
        when (wide) {
          true -> input.board.wideWarehouse()
          false -> input.board.map(String::toCharArray).toTypedArray()
        }
    var robot = board.findRobot()
    input.moves.forEach {
      val direction = moves[it]!!
      val next = robot + direction
      when (board[next]) {
        EMPTY -> robot = next
        WALL -> {}
        BOX -> {
          val empty = findEmpty(board, next, direction)
          if (empty != null) {
            robot = next
            board[empty] = BOX
            board[robot] = EMPTY
          }
        }
        LEFT_BOX,
        RIGHT_BOX -> {
          if (moveWide(board, next, direction)) robot = next
        }
      }
    }
    return board.gpsSum()
  }

  fun part1(input: Input): Int = solve(input, wide = false)
  fun part2(input: Input): Int = solve(input, wide = true)

  fun parseInput(input: List<String>): Input {
    val (moveLines, boardLines) =
        input.filterNot(String::isBlank).partition {
          (it.toCharArray().toSet() - moves.keys).isEmpty()
        }
    return Input(
        board = boardLines,
        moves = moveLines.joinToString("").toCharArray().toList(),
    )
  }

  val smallInput = parseInput(readInput("Day15_small"))
  assertEquals(expected = 2028, actual = part1(smallInput))
  assertEquals(expected = 1751, actual = part2(smallInput))

  val testInput = parseInput(readInput("Day15_test"))
  assertEquals(expected = 10092, actual = part1(testInput))
  assertEquals(expected = 9021, actual = part2(testInput))

  val input = parseInput(readInput("Day15"))
  part1(input).println()
  part2(input).println()
}
