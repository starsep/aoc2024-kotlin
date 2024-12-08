import kotlin.test.assertEquals

fun main() {
  fun ULong.concat(other: ULong): ULong = (toString() + other.toString()).toULong()

  fun List<ULong>.solvable(goal: ULong, concat: Boolean): Boolean {
    val head = this[0]
    if (head > goal) return false
    if (size == 1) return head == goal
    val rest = drop(2)
    val next = this[1]
    if (concat && (listOf(head.concat(next)) + rest).solvable(goal, true)) return true
    if ((listOf(head * next) + rest).solvable(goal, concat)) return true
    return (listOf(head + next) + rest).solvable(goal, concat)
  }

  fun solve(input: List<String>, concat: Boolean) =
      input
          .map { row ->
            val (goalString, rest) = row.split(":")
            val goal = goalString.toULong()
            val numbers = rest.split(" ").drop(1).map(String::toULong)
            numbers to goal
          }
          .filter { (numbers, goal) -> numbers.solvable(goal, concat = concat) }
          .sumOf { (_, goal) -> goal }

  fun part1(input: List<String>) = solve(input, concat = false)
  fun part2(input: List<String>) = solve(input, concat = true)

  val testInput = readInput("Day07_test")
  assertEquals(actual = part1(testInput), expected = 3749UL)
  assertEquals(actual = part2(testInput), expected = 11387UL)

  val input = readInput("Day07")
  part1(input).println()
  part2(input).println()
}
