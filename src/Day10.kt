import kotlin.test.assertEquals

fun main() {
  fun YX.valid(data: List<String>) = y in data.indices && x in data[0].indices
  fun List<String>.value(y: Int, x: Int) = this[y][x].toString().toInt()
  val directions = listOf(0 to -1, 0 to 1, 1 to 0, -1 to 0)
  fun dfs(data: List<String>, y: Int, x: Int, visited9s: MutableList<YX>) {
    if (data.value(y, x) == 9) {
      visited9s.add(YX(y, x))
      return
    }
    directions.forEach { (dy, dx) ->
      val ny = y + dy
      val nx = x + dx
      if (YX(ny, nx).valid(data) && data.value(y, x) + 1 == data.value(ny, nx)) {
        dfs(data, ny, nx, visited9s)
      }
    }
  }

  fun solve(data: List<String>, distinct: Boolean): Int {
    var result = 0
    data.indices.forEach { y ->
      data[y].indices.forEach { x ->
        if (data.value(y, x) == 0) {
          val visited9s = mutableListOf<YX>()
          dfs(data, y, x, visited9s)
          result += (if (distinct) visited9s.toSet() else visited9s).size
        }
      }
    }
    return result
  }

  fun part1(data: List<String>) = solve(data, distinct = true)
  fun part2(data: List<String>) = solve(data, distinct = false)

  val testInput = readInput("Day10_test")
  assertEquals(expected = 36, actual = part1(testInput))
  assertEquals(expected = 81, actual = part2(testInput))

  val input = readInput("Day10")
  part1(input).println()
  part2(input).println()
}
