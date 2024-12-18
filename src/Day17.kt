import kotlin.test.assertEquals
import kotlin.test.fail

enum class Command(val code: Int) {
  Adv(0),
  Bxl(1),
  Bst(2),
  Jnz(3),
  Bxc(4),
  Out(5),
  Bdv(6),
  Cdv(7);

  companion object {
    fun fromCode(code: Int) = entries.first { it.code == code }
  }
}

fun main() {
  data class Input(
      val registerA: Long,
      val registerB: Long,
      val registerC: Long,
      val program: List<Int>
  )
  fun parseInput(input: List<String>): Input {
    var result = Input(0, 0, 0, emptyList())
    fun String.registerValue() = split(":").last().trim().toLong()
    fun String.program() = split(":").last().trim().split(",").map(String::toInt)
    input.forEach { line ->
      if (line.startsWith("Register A")) {
        result = result.copy(registerA = line.registerValue())
      } else if (line.startsWith("Register B")) {
        result = result.copy(registerB = line.registerValue())
      } else if (line.startsWith("Register C")) {
        result = result.copy(registerC = line.registerValue())
      } else if (line.startsWith("Program:")) {
        result = result.copy(program = line.program())
      }
    }
    return result
  }

  fun computer(input: Input): List<Int> {
    val output = mutableListOf<Int>()
    var registerA = input.registerA.toLong()
    var registerB = input.registerB.toLong()
    var registerC = input.registerC.toLong()
    var pointer = 0
    while (pointer < input.program.size) {
      // println("A=${registerA} B=${registerB} C=${registerC} pointer=${pointer}")
      val command = Command.fromCode(input.program[pointer])
      val operand = input.program[pointer + 1].toLong()
      val combo =
          when (operand) {
            in 0L..3L -> operand
            4L -> registerA
            5L -> registerB
            6L -> registerC
            else -> fail("Unexpected operand $operand")
          }
      // println("$command $operand (combo=$combo) $pointer")
      when (command) {
        Command.Adv -> registerA = registerA shr combo.toInt()
        Command.Bxl -> registerB = registerB xor operand
        Command.Bst -> registerB = combo % 8
        Command.Jnz ->
            if (registerA != 0L) {
              pointer = operand.toInt()
            }
        Command.Bxc -> {
          registerB = registerB xor registerC
        }
        Command.Out -> {
          output.add((combo % 8).toInt())
        }
        Command.Bdv -> {
          registerB = registerA shr combo.toInt()
        }
        Command.Cdv -> {
          registerC = registerA shr combo.toInt()
        }
      }
      if (command != Command.Jnz || registerA == 0L) pointer += 2
    }
    return output
  }

  fun part1(input: Input): String = computer(input).joinToString(",", transform = Int::toString)
  fun find(input: Input, last: Int, prefix: Long): List<Long> =
      (prefix..prefix + 7L)
          .filter { a ->
            val result = computer(input.copy(registerA = a))
            result.takeLast(last) == input.program.takeLast(last)
          }
          .map { if (last < input.program.size) it * 8 else it }

  fun part2(input: Input): Long {
    var results = mutableSetOf(0L)
    input.program.indices.forEach { positionFromEnd ->
      val newResults = mutableSetOf<Long>()
      results.forEach { a -> newResults.addAll(find(input, positionFromEnd + 1, a)) }
      results = newResults
    }
    return results.min()
  }

  val testInput = parseInput(readInput("Day17_test"))
  assertEquals(expected = "4,6,3,5,6,3,5,2,1,0", actual = part1(testInput))

  val part2Example = parseInput(readInput("Day17_part2"))
  assertEquals(expected = "5,7,3,0", actual = part1(part2Example))
  assertEquals(expected = 117440, actual = part2(part2Example))

  val input = parseInput(readInput("Day17"))
  part1(input).println()
  part2(input).println()
}
