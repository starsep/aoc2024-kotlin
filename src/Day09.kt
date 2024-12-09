import kotlin.test.assertEquals

typealias Disk = MutableList<Int?>

fun main() {
  fun Disk.debug() = joinToString("") { it?.toString() ?: "." }.println()
  fun Disk.checksum() = withIndex().sumOf { (index, value) -> index.toLong() * (value ?: 0) }

  fun initialDisk(data: String): Disk {
    val disk = mutableListOf<Int?>()
    data.withIndex().forEach { (index, value) ->
      val count = value.toString().toInt()
      val block = if (index % 2 == 0) index / 2 else null
      repeat(count) { disk.add(block) }
    }
    return disk
  }

  fun defragment(disk: Disk, fullBlock: Boolean) {
    var left = 0
    var right = disk.size - 1
    while (left <= right) {
      while (disk[left] != null) {
        left++
        if (left >= disk.size) return
      }
      while (disk[right] == null) {
        right--
        if (right < 0) return
      }
      if (left > right) return
      if (!fullBlock) {
        disk[left] = disk[right]
        disk[right] = null
      } else {
        var blockStart = right
        while (disk[blockStart] == disk[right]) {
          blockStart--
        }
        blockStart++
        var emptyBlockStart = left
        while (emptyBlockStart < blockStart) {
          while (disk[emptyBlockStart] != null) {
            emptyBlockStart++
            if (emptyBlockStart >= blockStart) break
          }
          if (emptyBlockStart >= blockStart) break
          var emptyBlockEnd = emptyBlockStart
          while (disk[emptyBlockEnd] == null) {
            emptyBlockEnd++
          }
          emptyBlockEnd--
          if (right - blockStart <= emptyBlockEnd - emptyBlockStart) {
            for (index in blockStart..right) {
              disk[emptyBlockStart + index - blockStart] = disk[index]
              disk[index] = null
            }
            break
          } else {
            emptyBlockStart = emptyBlockEnd + 1
          }
        }
        val previousIndex = disk[right]
        while (disk[right] == previousIndex) right--
      }
    }
  }

  fun solve(data: String, fullBlock: Boolean): Long {
    val disk = initialDisk(data)
    defragment(disk, fullBlock = fullBlock)
    return disk.checksum()
  }

  fun part1(data: String) = solve(data, fullBlock = false)
  fun part2(data: String) = solve(data, fullBlock = true)

  val testInput = readInput("Day09_test")[0]
  assertEquals(expected = 1928L, actual = part1(testInput))
  assertEquals(expected = 2858L, actual = part2(testInput))

  val input = readInput("Day09")[0]
  part1(input).println()
  part2(input).println()
}
