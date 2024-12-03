import kotlin.math.abs

fun main() {
    fun part1(input: List<String>): Int {
        val left = mutableListOf<Int>()
        val right = mutableListOf<Int>()
        for (row in input) {
            val parts = row.split("   ")
            left.add(parts[0].toInt())
            right.add(parts[1].toInt())
        }
        return left.sorted().zip(right.sorted()).sumOf { (a, b) ->
            abs(a - b)
        }
    }

    fun part2(input: List<String>): Int {
        val left = mutableListOf<Int>()
        val countsB = mutableMapOf<Int, Int>()
        for (row in input) {
            val parts = row.split("   ")
            left.add(parts[0].toInt())
            val b = parts[1].toInt()
            if (b !in countsB) {
                countsB[b] = 0
            }
            countsB[b] = countsB.getOrDefault(b, 0) + 1
        }
        return left.sumOf {
            it * (countsB.getOrDefault(it, 0))
        }
    }

    val testInput = readInput("Day01_test")
    check(part1(testInput) == 11)
    check(part2(testInput) == 31)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}