import kotlin.test.assertEquals

fun main() {
  val directions = listOf(YX(-1, 0), YX(0, -1), YX(0, 1), YX(1, 0))
  data class Input(val bytes: List<YX>, val fallen: Int, val size: Int)
  fun parseInput(input: List<String>, fallen: Int, size: Int) =
      Input(
          bytes =
              input.map {
                val (x, y) = it.split(",").map(String::toInt)
                YX(y = y, x = x)
              },
          fallen = fallen,
          size = size)

  fun part1(input: Input): Int? {
    val board = Array(input.size) { BooleanArray(input.size) { false } }
    val visited = Array(input.size) { BooleanArray(input.size) { false } }
    input.bytes.take(input.fallen).forEach { board[it.y][it.x] = true }
    val queue = mutableListOf<Pair<YX, Int>>()
    queue.add(YX(0, 0) to 0)
    val target = YX(input.size - 1, input.size - 1)
    while (queue.isNotEmpty()) {
      val (yx, distance) = queue.removeFirst()
      if (yx == target) return distance
      if (visited[yx.y][yx.x]) continue
      visited[yx.y][yx.x] = true
      directions.forEach { dyx ->
        val nyx = yx + dyx
        val valid = nyx.x in 0 ..< input.size && nyx.y in 0 ..< input.size
        if (valid && !visited[nyx.y][nyx.x] && !board[nyx.y][nyx.x]) {
          queue.add(nyx to (distance + 1))
        }
      }
    }
    return null
  }
  fun part2(input: Input): String {
    var begin = 0
    var end = input.bytes.size - 1
    lateinit var last: YX
    while (begin <= end) {
      val mid = (begin + end) / 2
      val result = part1(input.copy(fallen = mid + 1))
      if (result == null) {
        last = input.bytes[mid]
        end = mid - 1
      } else {
        begin = mid + 1
      }
    }
    return "${last.x},${last.y}"
  }

  val testInput = parseInput(readInput("Day18_test"), fallen = 12, size = 7)
  assertEquals(expected = 22, actual = part1(testInput))
  assertEquals(expected = "6,1", actual = part2(testInput))

  val input = parseInput(readInput("Day18"), fallen = 1024, size = 71)
  part1(input).println()
  part2(input).println()
}
