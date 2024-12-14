import kotlin.test.assertEquals

fun Board.valid(yx: YX) = yx.y in indices && yx.x in this[0].indices

operator fun Board.get(yx: YX) = this[yx.y][yx.x]

fun main() {
  val directions = listOf(YX(-1, 0), YX(0, -1), YX(0, 1), YX(1, 0))
  fun sides(perimeters: Set<Pair<YX, YX>>): Int {
    val visited = mutableSetOf<Pair<YX, YX>>()
    var result = 0
    perimeters.forEach { (point, direction) ->
      if ((point to direction) !in visited) {
        visited.add(point to direction)
        result++
        val queue = mutableListOf(point to direction)
        while (queue.isNotEmpty()) {
          val (current, currentDirection) = queue.first()
          queue.remove(current to currentDirection)
          visited.add(current to currentDirection)
          directions.forEach { dyx ->
            val nyx = current + dyx
            if ((nyx to direction) !in visited && (nyx to direction) in perimeters) {
              visited.add(nyx to direction)
              queue.add(nyx to direction)
            }
          }
        }
      }
    }
    return result
  }
  fun process(point: YX, board: Board, visited: MutableSet<YX>, discount: Boolean): Int {
    if (point in visited) return 0
    var area = 0
    val perimeters = mutableSetOf<Pair<YX, YX>>()
    val queue = mutableListOf(point)
    while (queue.isNotEmpty()) {
      area++
      val yx = queue.removeLast()
      visited.add(yx)
      directions.forEach { dyx ->
        val nyx = yx + dyx
        if (board.valid(nyx) && board[nyx] == board[yx]) {
          if (nyx !in visited) {
            visited.add(nyx)
            queue.add(nyx)
          }
        } else {
          perimeters.add(nyx to dyx)
        }
      }
    }
    return area * (if (discount) sides(perimeters) else perimeters.size)
  }

  fun solve(board: Board, discount: Boolean): Int {
    val visited = mutableSetOf<YX>()
    return board.indices.sumOf { mainY ->
      board[mainY]
          .indices
          .map { YX(mainY, it) }
          .sumOf { point -> process(point, board, visited, discount) }
    }
  }

  fun part1(board: Board) = solve(board, discount = false)
  fun part2(board: Board) = solve(board, discount = true)

  val testInput = readInput("Day12_test1")
  assertEquals(expected = 140, actual = part1(testInput))
  assertEquals(expected = 80, actual = part2(testInput))

  val testInput2 = readInput("Day12_test2")
  assertEquals(expected = 772, actual = part1(testInput2))
  assertEquals(expected = 436, actual = part2(testInput2))

  val testInput3 = readInput("Day12_test3")
  assertEquals(expected = 1930, actual = part1(testInput3))
  assertEquals(expected = 1206, actual = part2(testInput3))

  val input = readInput("Day12")
  println(part1(input))
  println(part2(input))
}
