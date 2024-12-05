fun main() {
  val mulPattern = Regex("""mul\((\d{1,3}),(\d{1,3})\)""")

  fun MatchResult.mul(): Int = groups[1]!!.value.toInt() * groups[2]!!.value.toInt()

  fun part1(input: List<String>): Int =
      input.sumOf { row -> mulPattern.findAll(row).sumOf(MatchResult::mul) }
  val doPattern = Regex("""do\(\)""")
  val doNotPattern = Regex("""don't\(\)""")

  fun part2(input: List<String>): Int {
    var enabled = true
    var result = 0
    input.forEach { row ->
      row.indices.forEach { index ->
        if (enabled && mulPattern.matchesAt(row, index)) {
          result += mulPattern.find(row, index)!!.mul()
        }
        if (doPattern.matchesAt(row, index)) enabled = true
        if (doNotPattern.matchesAt(row, index)) enabled = false
      }
    }
    return result
  }

  check(part1(readInput("Day03_test")) == 161)
  check(part2(readInput("Day03_test2")) == 48)

  val input = readInput("Day03")
  part1(input).println()
  part2(input).println()
}
