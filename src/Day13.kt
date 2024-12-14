import kotlin.test.assertEquals

data class XY(val x: Long, val y: Long)

fun XY.det(other: XY) = x * other.y - y * other.x

fun main() {
  data class Machine(val a: XY, val b: XY, val prize: XY)
  fun parseInput(input: List<String>, addPrize: Long): List<Machine> {
    val result = mutableListOf<Machine>()
    var a = XY(0L, 0L)
    var b = XY(0L, 0L)
    var prize: XY
    input.forEach { row ->
      if (row.startsWith("Button A")) {
        val x = row.split("X")[1].split(",").first().toLong()
        val y = row.split("Y").last().toLong()
        a = XY(x, y)
      } else if (row.startsWith("Button B")) {
        val x = row.split("X")[1].split(",").first().toLong()
        val y = row.split("Y").last().toLong()
        b = XY(x, y)
      } else if (row.startsWith("Prize")) {
        val x = row.split("=")[1].split(",").first().toLong()
        val y = row.split("=").last().toLong()
        prize = XY(x + addPrize, y + addPrize)
        result.add(Machine(a = a, b = b, prize = prize))
      }
    }
    return result
  }

  fun solve(input: List<String>, addPrize: Long, maxMoves: Long): Long {
    var result = 0L
    for (machine in parseInput(input, addPrize)) {
      val w = machine.a.det(machine.b)
      val wX = machine.prize.det(machine.b)
      val wY = machine.a.det(machine.prize)
      if (w != 0L) {
        val aDouble = wX.toDouble() / w
        val bDouble = wY.toDouble() / w
        val a = aDouble.toLong()
        val b = bDouble.toLong()
        if (aDouble == a.toDouble() && bDouble == b.toDouble()) {
          if (a in 0..maxMoves && b in 0..maxMoves) {
            result += 3 * a + b
          }
        }
      }
    }
    return result
  }

  fun part1(input: List<String>) = solve(input, addPrize = 0L, maxMoves = 100L)
  fun part2(input: List<String>) =
      solve(input, addPrize = 10000000000000L, maxMoves = Long.MAX_VALUE)

  val testInput = readInput("Day13_test")
  assertEquals(expected = 480L, actual = part1(testInput))

  val input = readInput("Day13")
  println(part1(input))
  println(part2(input))
}
