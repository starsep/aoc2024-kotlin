import kotlin.test.assertEquals

typealias Board = List<String>

fun main() {
  data class Point(val y: Int, val x: Int) {
    fun valid(board: Board) = y in board.indices && x in board[0].indices

    operator fun minus(other: Point) = Point(y - other.y, x - other.x)
  }

  fun solve(board: Board, multiple: Boolean): Int {
    val antennas = mutableMapOf<Char, MutableSet<Point>>()
    board.withIndex().forEach { (y, row) ->
      row.indices
          .filter { x -> row[x] != '.' }
          .forEach { x ->
            if (row[x] !in antennas) {
              antennas[row[x]] = mutableSetOf()
            }
            antennas[row[x]]!!.add(Point(y, x))
          }
    }
    val antinodes = mutableSetOf<Point>()
    antennas.forEach { (_, points) ->
      points.forEach { point ->
        points.forEach { other ->
          if (point != other) {
            val difference = point - other
            var antinode = other - difference
            if (antinode.valid(board)) {
              antinodes.add(antinode)
            }
            while (multiple && antinode.valid(board)) {
              antinodes.add(antinode)
              antinode -= difference
            }
            if (multiple) {
              antinodes.add(point)
              antinodes.add(other)
            }
          }
        }
      }
    }
    return antinodes.size
  }

  fun part1(board: Board): Int = solve(board, multiple = false)
  fun part2(board: Board): Int = solve(board, multiple = true)

  val testInput = readInput("Day08_test")
  assertEquals(expected = 14, actual = part1(testInput))
  assertEquals(expected = 34, actual = part2(testInput))

  val input = readInput("Day08")
  part1(input).println()
  part2(input).println()
}
