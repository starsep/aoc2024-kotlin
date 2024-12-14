data class YX(val y: Int, val x: Int)

operator fun YX.plus(other: YX): YX = YX(y = y + other.y, x = x + other.x)

operator fun YX.times(q: Int): YX = YX(y = y * q, x = x * q)

fun Int.safeMod(m: Int) = (this % m + m) % m

operator fun YX.rem(m: YX): YX = YX(y = y.safeMod(m.y), x = x.safeMod(m.x))
