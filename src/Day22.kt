import kotlin.test.assertEquals

const val MOD = 16777216
const val DAYS = 2000

fun main() {
  fun parseInput(input: List<String>): List<Long> = input.map(String::toLong)

  fun calculate(secret: Long): Long {
    val step1 = ((64 * secret) xor secret) % MOD
    val step2 = ((step1 / 32) xor step1) % MOD
    val step3 = ((step2 * 2048) xor step2) % MOD
    return step3
  }

  fun part1(input: List<Long>): Long {
    return input.sumOf {
      var result = it
      repeat(DAYS) { result = calculate(result) }
      return@sumOf result
    }
  }

  fun part2(input: List<Long>): Int {
    val results = mutableMapOf<List<Int>, MutableList<Int>>()
    input.forEach { secret ->
      var result = secret
      var cost = (secret % 10).toInt()
      val seen = mutableSetOf<List<Int>>()
      val diffs = mutableListOf<Int>()
      repeat(DAYS) {
        val next = calculate(result)
        val nextCost = (next % 10).toInt()
        val difference = nextCost - cost
        diffs.add(difference)
        if (diffs.size > 4) diffs.removeFirst()
        if (diffs.size == 4) {
          val diffsNonMutable = diffs.toList()
          if (diffsNonMutable !in seen) {
            if (diffsNonMutable !in results) results[diffsNonMutable] = mutableListOf()
            results[diffsNonMutable]!!.add(nextCost)
            seen.add(diffsNonMutable)
          }
        }
        result = next
        cost = nextCost
      }
    }
    val best = results.maxBy { it.value.sum() }
    return best.value.sum()
  }

  val testInput = readInput("Day22_test")
  assertEquals(expected = 37327623, actual = part1(parseInput(testInput)))
  assertEquals(expected = 24, actual = part2(parseInput(testInput)))

  val testInput2 = readInput("Day22_test2")
  assertEquals(expected = 37990510, actual = part1(parseInput(testInput2)))
  assertEquals(expected = 23, actual = part2(parseInput(testInput2)))

  val input = readInput("Day22")
  part1(parseInput(input)).println()
  part2(parseInput(input)).println()
}
