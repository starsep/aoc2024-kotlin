import java.io.File
import kotlin.test.assertEquals

fun main() {
  fun String.id() = drop(1).toInt()
  data class Gate(val left: String, val operator: String, val right: String, val output: String) {
    fun outputLabel(): String? {
      try {
        val leftId = left.id()
        val rightId = right.id()
        if (setOf(left.first(), right.first()) != setOf('x', 'y')) return null
        if (leftId != rightId) return null
        return operator.lowercase() + leftId.toString()
      } catch (e: NumberFormatException) {
        return null
      }
    }
  }
  data class Input(val values: Map<String, Boolean>, val gates: List<Gate>)
  fun parseInput(input: List<String>): Input {
    val values = mutableMapOf<String, Boolean>()
    val gates = mutableListOf<Gate>()
    for (row in input) {
      if (row.isBlank()) continue
      if (":" in row) {
        val (variable, value) = row.split(":")
        values[variable] = value.trim().toInt() == 1
      } else {
        val gate = row.replace("-> ", "").split(" ")
        gates.add(Gate(gate[0], gate[1], gate[2], gate[3]))
      }
    }
    return Input(values, gates)
  }

  fun Map<String, Boolean?>.calculate(prefix: String): Long =
      filterKeys { it.startsWith(prefix) }
          .entries
          .sortedByDescending { it.key }
          .map { if (it.value == true) '1' else '0' }
          .joinToString("")
          .toLong(2)

  fun solve(input: Input): Map<String, Boolean?> {
    val values = mutableMapOf<String, Boolean?>()
    val gatesPerVariable = mutableMapOf<String, MutableList<Gate>>()
    input.gates.forEach { gate ->
      values[gate.left] = null
      values[gate.right] = null
      values[gate.output] = null
      if (gate.left !in gatesPerVariable) gatesPerVariable[gate.left] = mutableListOf()
      gatesPerVariable[gate.left]!!.add(gate)
      if (gate.right !in gatesPerVariable) gatesPerVariable[gate.right] = mutableListOf()
      gatesPerVariable[gate.right]!!.add(gate)
      if (gate.output !in gatesPerVariable) gatesPerVariable[gate.output] = mutableListOf()
    }
    values.putAll(input.values)
    val knownVariables = mutableListOf<String>()
    knownVariables.addAll(values.filterValues { it != null }.keys)
    while (knownVariables.isNotEmpty()) {
      val variable = knownVariables.removeLast()
      gatesPerVariable[variable]?.forEach { gate ->
        val left = values[gate.left]
        val right = values[gate.right]
        if (values[gate.output] == null && left != null && right != null) {
          values[gate.output] =
              when (gate.operator) {
                "AND" -> left && right
                "XOR" -> left xor right
                "OR" -> left or right
                else -> throw Exception("Unexpected ${gate.operator}")
              }
          knownVariables.add(gate.output)
        }
      }
    }
    return values
  }

  fun part1(input: Input): Long = solve(input).calculate("z")
  val options = listOf(false, true)
  fun checkBit0(input: Input): Boolean {
    for (x in options) {
      for (y in options) {
        val value = solve(input.copy(values = mapOf("x00" to x, "y00" to y))).calculate("z")
        if ((value == 1L) != (x xor y)) return false
      }
    }
    return true
  }
  fun Int.zeroPad() = toString().padStart(2, '0')

  fun Boolean.toInt() = if (this) 1 else 0
  fun checkBitK(input: Input, k: Int): Boolean {
    val values =
        (0 ..< k)
            .flatMap { listOf("x${it.zeroPad()}" to false, "y${it.zeroPad()}" to false) }
            .toMap()
            .toMutableMap()
    val current = k.zeroPad()
    for (carry in options) {
      val previous = (k - 1).zeroPad()
      values["x$previous"] = carry
      values["y$previous"] = carry
      for (x in options) {
        values["x$current"] = x
        for (y in options) {
          values["y$current"] = y
          val result = solve(input.copy(values = values))["z$current"]
          val expected = carry xor (x xor y)
          if (result != expected) {
            println("$k -> carry=${carry.toInt()} x=${x.toInt()} y=${y.toInt()}")
            return false
          }
        }
      }
    }
    return true
  }
  fun Boolean.color() = if (this) "green" else "red"

  fun generateGraphviz(input: Input) {
    val colors = mutableMapOf<String, String>()
    colors["z00"] = checkBit0(input).color()
    val zBits = input.gates.filter { it.output.startsWith("z") }.map { it.output.id() }.sorted()
    for (k in zBits.drop(1).dropLast(1)) {
      colors["z${k.zeroPad()}"] = checkBitK(input, k).color()
    }
    val writer = File("Day24.dot").writer()
    writer.append("digraph G {\n")
    for (sign in listOf("x", "y", "z")) {
      val bits = if (sign == "z") zBits else zBits.dropLast(1)
      writer.append(bits.joinToString(" -> ") { "$sign${it.zeroPad()}" })
      writer.append(";\n")
    }
    for ((variable, color) in colors) {
      writer.append("$variable [style=filled,color=$color];\n")
    }
    fun Gate.xyInputs() =
        (left.startsWith("x") || left.startsWith("y")) &&
            (right.startsWith("x") || right.startsWith("y"))
    val gates =
        input.gates
            .map { gate ->
              val variables = listOf(gate.left, gate.right)
              val left = variables.min()
              val right = variables.max()
              Gate(left, gate.operator, right, gate.output)
            }
            .sortedWith { a, b ->
              return@sortedWith when (a.xyInputs() to b.xyInputs()) {
                false to false,
                true to true -> a.left.compareTo(b.left)
                false to true -> 1
                true to false -> -1
                else -> throw Exception("")
              }
            }
    for ((index, gate) in gates.withIndex()) {
      writer.append("gate$index [shape=box, label=\"${gate.operator}\"];\n")
      writer.append("${gate.left} -> gate$index\n")
      writer.append("${gate.right} -> gate$index\n")
      writer.append("gate$index -> ${gate.output}\n")
      val label = gate.outputLabel()
      if (label != null) {
        writer.append("${gate.output} [label=\"$label\\n${gate.output}\"];\n")
      }
    }
    writer.append("}\n")
    writer.close()
    println("Check:\ndot -Tsvg Day24.dot > Day24.svg")
  }

  fun List<Gate>.swap(swaps: Map<String, String>): List<Gate> = map { gate ->
    if (gate.output in swaps) gate.copy(output = swaps[gate.output]!!) else gate
  }

  fun part2(input: Input) {
    val swaps = mapOf("qjj" to "gjc", "wmp" to "z17", "gvm" to "z26", "qsb" to "z39")
    val swapMap = swaps.flatMap { (key, value) -> listOf(key to value, value to key) }.toMap()
    val swapped = input.copy(gates = input.gates.swap(swapMap))
    generateGraphviz(swapped)
    val result = swaps.entries.flatMap { listOf(it.key, it.value) }.sorted().joinToString(",")
    println(result)
  }

  val testInput = readInput("Day24_test1")
  assertEquals(expected = 4, actual = part1(parseInput(testInput)))

  val testInput2 = readInput("Day24_test2")
  assertEquals(expected = 2024, actual = part1(parseInput(testInput2)))

  val input = readInput("Day24")
  part1(parseInput(input)).println()
  part2(parseInput(input))
}
