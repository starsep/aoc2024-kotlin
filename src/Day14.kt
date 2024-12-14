import androidx.compose.runtime.*
import com.jakewharton.mosaic.layout.*
import com.jakewharton.mosaic.modifier.Modifier
import com.jakewharton.mosaic.runMosaicBlocking
import com.jakewharton.mosaic.ui.Box
import com.jakewharton.mosaic.ui.Column
import com.jakewharton.mosaic.ui.Text
import com.jakewharton.mosaic.ui.unit.IntOffset
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPOutputStream
import kotlin.test.assertEquals
import kotlin.text.Charsets.UTF_8
import kotlinx.coroutines.delay

data class Robot(val position: YX, val velocity: YX)

fun List<Robot>.move(size: YX, seconds: Int): List<Robot> = map {
  Robot((it.position + it.velocity * seconds) % size, it.velocity)
}

object Day14 {
  private val pattern = Regex("""p=(-?\d+),(-?\d+) v=(-?\d+),(-?\d+)""")

  private fun parseInput(input: List<String>): List<Robot> =
      input.map { row ->
        val match = pattern.matchEntire(row)!!
        Robot(
            position = YX(match.groupValues[2].toInt(), match.groupValues[1].toInt()),
            velocity = YX(match.groupValues[4].toInt(), match.groupValues[3].toInt()))
      }

  fun part1(robots: List<Robot>, size: YX, seconds: Int): Int {
    val robotPositions = robots.move(size, seconds).map(Robot::position)
    val middleY = size.y / 2
    val middleX = size.x / 2
    val outsideRobots = robotPositions.filterNot { it.y == middleY || it.x == middleX }
    val (n, s) = outsideRobots.partition { it.y < middleY }
    val (ne, nw) = n.partition { it.x < middleX }
    val (se, sw) = s.partition { it.x < middleX }
    return listOf(ne, nw, se, sw).map(List<YX>::size).fold(1) { acc, quadrant -> acc * quadrant }
  }

  fun main(): Pair<List<Robot>, YX> {
    val seconds = 100

    val testSize = YX(y = 7, x = 11)
    val testRobots = parseInput(readInput("Day14_test"))
    assertEquals(expected = 12, actual = part1(testRobots, testSize, seconds))

    val size = YX(y = 103, x = 101)
    val robots = parseInput(readInput("Day14"))
    println(part1(robots, size, seconds))
    return robots to size
  }
}

@Composable
fun part2Manual(startingRobots: List<Robot>, size: YX) {
  var second by remember { mutableStateOf(0) }
  var robots by remember { mutableStateOf(startingRobots.move(size, second)) }
  var running by remember { mutableStateOf(false) }

  Column(
      modifier =
          Modifier.onKeyEvent {
            when (it) {
              KeyEvent(" ") -> {
                running = !running
                true
              }
              else -> false
            }
          }) {
        Text("Second: $second")
        Box(
            modifier =
                Modifier.drawBehind { drawRect('|', drawStyle = DrawStyle.Stroke(1)) }
                    .padding(1)
                    .size(size.x, size.y),
        ) {
          robots.map {
            Text("#", modifier = Modifier.offset { IntOffset(it.position.x, it.position.y) })
          }
        }
      }

  LaunchedEffect(Unit) {
    while (true) {
      if (running) {
        second++
        robots = robots.move(size, 1)
      }
      delay(100)
    }
  }
}

fun gzip(content: String): ByteArray {
  val bos = ByteArrayOutputStream()
  GZIPOutputStream(bos).bufferedWriter(UTF_8).use { it.write(content) }
  return bos.toByteArray()
}

fun List<Robot>.grid(size: YX): String {
  val positions = map(Robot::position).toSet()
  return buildString {
    (0..size.y).forEach { y ->
      (0..size.x).forEach { x -> append(if (YX(y, x) in positions) '#' else ' ') }
      append('\n')
    }
  }
}

fun part2Entropy(robots: List<Robot>, size: YX): Int {
  var best = Int.MAX_VALUE
  var result = 0
  (0..10000).forEach { seconds ->
    val compressedSize = gzip(robots.move(size, seconds).grid(size)).size
    if (compressedSize < best) {
      best = compressedSize
      result = seconds
    }
  }
  return result
}

fun main(args: Array<String>) {
  val (robots, size) = Day14.main()
  println(part2Entropy(robots, size))
  runMosaicBlocking { part2Manual(robots, size) }
}
