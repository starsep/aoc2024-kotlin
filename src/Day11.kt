import kotlin.test.assertEquals

fun solve(numbers: List<ULong>, steps: Int): ULong {
  var result = mutableMapOf<ULong, ULong>()
  numbers.forEach { result[it] = (result[it] ?: 0UL) + 1UL }
  repeat(steps) {
    val newResult = mutableMapOf<ULong, ULong>()
    result.forEach { (number, count) ->
      val numberString = number.toString()
      if (number == 0UL) {
        newResult[1UL] = (newResult[1UL] ?: 0UL) + count
      } else if (numberString.length % 2 == 0) {
        val left = numberString.slice(0 ..< numberString.length / 2).toULong()
        val right = numberString.slice((numberString.length / 2) ..< numberString.length).toULong()
        newResult[left] = (newResult[left] ?: 0UL) + count
        newResult[right] = (newResult[right] ?: 0UL) + count
      } else {
        val multiplied = number * 2024UL
        newResult[multiplied] = (newResult[multiplied] ?: 0UL) + count
      }
    }
    result = newResult
  }
  return result.values.sum()
}

fun main() {
  assertEquals(2UL, solve(listOf(125UL, 17UL), 0))
  assertEquals(3UL, solve(listOf(125UL, 17UL), 1))
  assertEquals(4UL, solve(listOf(125UL, 17UL), 2))
  assertEquals(5UL, solve(listOf(125UL, 17UL), 3))
  assertEquals(9UL, solve(listOf(125UL, 17UL), 4))
  assertEquals(13UL, solve(listOf(125UL, 17UL), 5))
  assertEquals(22UL, solve(listOf(125UL, 17UL), 6))
  assertEquals(55312UL, solve(listOf(125UL, 17UL), 25))

  println(solve(listOf(70949UL, 6183UL, 4UL, 3825336UL, 613971UL, 0UL, 15UL, 182UL), 25))
  println(solve(listOf(70949UL, 6183UL, 4UL, 3825336UL, 613971UL, 0UL, 15UL, 182UL), 75))
}
