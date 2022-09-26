fun main() {
    val input = readLine()!!
    when {
        input.isEmpty() -> println()
        input.first() == 'i' -> {
            val new = input.drop(1)
            println(new.toInt() + 1)
        }
        input.first() == 's' -> {
            val new = input.drop(1)
            println(new.reversed())
        }
        else -> println(input)
    }
}
