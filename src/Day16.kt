import java.util.PriorityQueue
import kotlin.test.assertEquals

const val START = 'S'
const val END = 'E'

typealias Day16Results = Array<Array<IntArray>>

typealias Day16Visited = Array<Array<BooleanArray>>

fun main() {
  val moveCost = 1
  val turnCost = 1000
  val moves = mapOf('v' to YX(1, 0), '>' to YX(0, 1), '<' to YX(0, -1), '^' to YX(-1, 0))
  val turns =
      mapOf(
          'v' to listOf('<', '>'),
          '^' to listOf('<', '>'),
          '>' to listOf('^', 'v'),
          '<' to listOf('^', 'v'))
  val reverseDirections = mapOf('v' to '^', '^' to 'v', '<' to '>', '>' to '<')

  class Input(val board: List<String>, val start: YX, val end: YX)
  data class Node(val position: YX, val direction: Char)
  operator fun Day16Results.get(node: Node): Int =
      this[node.position.y][node.position.x][moves.keys.indexOf(node.direction)]
  operator fun Day16Results.set(node: Node, value: Int) {
    this[node.position.y][node.position.x][moves.keys.indexOf(node.direction)] = value
  }
  operator fun Day16Visited.get(node: Node): Boolean =
      this[node.position.y][node.position.x][moves.keys.indexOf(node.direction)]
  operator fun Day16Visited.set(node: Node, value: Boolean) {
    this[node.position.y][node.position.x][moves.keys.indexOf(node.direction)] = value
  }

  fun findOptimalSpots(input: Input, result: Day16Results, bestScore: Int): Int {
    val board = input.board
    val visited = Array(board.size) { Array(board[0].length) { BooleanArray(moves.size) } }
    val queue = mutableListOf<Node>()
    moves.keys.forEach {
      val endNode = Node(input.end, it)
      if (result[Node(input.end, it)] == bestScore) {
        queue.add(endNode)
      }
    }
    while (queue.isNotEmpty()) {
      val node = queue.removeLast()
      if (visited[node]) continue
      visited[node] = true
      val previousPosition = node.position + moves[reverseDirections[node.direction]!!]!!
      val previousNode = Node(previousPosition, node.direction)
      if (result[previousNode] == result[node] - moveCost) {
        queue.add(previousNode)
      }
      turns[node.direction]!!.forEach {
        val turnNode = Node(node.position, direction = it)
        if (result[turnNode] + turnCost == result[node]) {
          queue.add(turnNode)
        }
      }
    }
    return visited.sumOf { row -> row.count { yz -> yz.any { it } } }
  }

  fun solve(input: Input, part2: Boolean): Int {
    val board = input.board
    val result =
        Array(board.size) { Array(board[0].length) { IntArray(moves.keys.size) { Int.MAX_VALUE } } }
    val queue =
        PriorityQueue(
            compareBy<Node>({ result[it] }, { it.position.y }, { it.position.y }, Node::direction))
    val startNode = Node(position = input.start, direction = '>')
    result[startNode] = 0
    queue.add(startNode)
    while (!queue.isEmpty()) {
      val node = queue.poll()
      val nextPosition = node.position + moves[node.direction]!!
      if (board[nextPosition] != WALL) {
        val nextNode = Node(nextPosition, node.direction)
        val nextScore = result[node] + moveCost
        if (nextScore < result[nextNode] || (part2 && nextScore == result[nextNode])) {
          result[nextNode] = nextScore
          queue.add(nextNode)
        }
      }
      turns[node.direction]!!.forEach {
        val turnNode = Node(node.position, direction = it)
        val turnScore = result[node] + turnCost
        if (turnScore < result[turnNode] || (part2 && turnScore == result[turnNode])) {
          result[turnNode] = turnScore
          queue.add(turnNode)
        }
      }
    }
    val bestScore = moves.keys.minOf { result[Node(input.end, it)] }
    if (!part2) return bestScore
    return findOptimalSpots(input, result, bestScore)
  }

  fun part1(input: Input): Int = solve(input, part2 = false)
  fun part2(input: Input): Int = solve(input, part2 = true)

  fun parseInput(input: List<String>): Input {
    lateinit var start: YX
    lateinit var end: YX
    input.indices.forEach { y ->
      input[y].indices.forEach { x ->
        when (input[y][x]) {
          START -> start = YX(y, x)
          END -> end = YX(y, x)
        }
      }
    }
    return Input(input, start, end)
  }

  val testInput1 = parseInput(readInput("Day16_test1"))
  assertEquals(expected = 7036, actual = part1(testInput1))
  assertEquals(expected = 45, actual = part2(testInput1))

  val testInput2 = parseInput(readInput("Day16_test2"))
  assertEquals(expected = 11048, actual = part1(testInput2))
  assertEquals(expected = 64, actual = part2(testInput2))

  val input = parseInput(readInput("Day16"))
  part1(input).println()
  part2(input).println()
}
