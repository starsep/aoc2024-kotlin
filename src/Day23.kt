import kotlin.test.assertEquals

fun main() {
  fun parseInput(input: List<String>): Map<String, Set<String>> {
    val graph = mutableMapOf<String, MutableSet<String>>()
    input.forEach {
      val (start, end) = it.split("-")
      if (start !in graph) graph[start] = mutableSetOf()
      graph[start]!!.add(end)
      if (end !in graph) graph[end] = mutableSetOf()
      graph[end]!!.add(start)
    }
    return graph
  }

  fun threes(graph: Map<String, Set<String>>): Set<List<String>> {
    val nodes = graph.keys
    val tNodes = nodes.filter { it.startsWith("t") }
    val results = mutableSetOf<List<String>>()
    tNodes.forEach { t ->
      val tNeighbours = graph[t]!!
      tNeighbours
          .filter { !it.startsWith("t") || it > t }
          .forEach { n ->
            val nNeighbours = graph[n]!!.filter { it > n }.toSet()
            val common = tNeighbours.intersect(nNeighbours)
            common.forEach { c -> results.add(listOf(t, n, c).sorted()) }
          }
    }
    return results
  }

  fun part1(graph: Map<String, Set<String>>): Int = threes(graph).size

  fun part2(graph: Map<String, Set<String>>): String {
    val nodes = graph.keys.sorted()
    val visited = mutableSetOf<String>()
    var best = 0
    var result = listOf<String>()
    for (n in nodes) {
      if (n in visited) continue
      visited.add(n)
      val clique = mutableSetOf(n)
      for (x in graph[n]!!) {
        if (clique.all { x in graph[it]!! }) {
          clique.add(x)
        }
      }
      if (clique.size > best) {
        best = clique.size
        result = clique.sorted()
      }
    }
    return result.joinToString(",")
  }

  val testInput = readInput("Day23_test")
  assertEquals(expected = 7, actual = part1(parseInput(testInput)))
  assertEquals(expected = "co,de,ka,ta", actual = part2(parseInput(testInput)))

  val input = readInput("Day23")
  part1(parseInput(input)).println()
  part2(parseInput(input)).println()
}
