package victor.perf

object KotlinLoops {
    fun range(numbers: List<Int>, f: (Int) -> Int): Int {
        var sum = 0;
        for (index in numbers.indices) {
            sum += f(numbers[index])
        }
        return sum
    }
}