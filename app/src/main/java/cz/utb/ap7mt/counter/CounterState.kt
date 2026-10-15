package cz.utb.ap7mt.counter

import kotlinx.serialization.Serializable

@Serializable
data class CounterState(
    val name: String = "",
    val count: Int = 0,
    val history: List<CounterHistoryItem> = emptyList(),
) {
    fun increment() = copy(count = if (count < Int.MAX_VALUE) count + 1 else count)
    fun decrement() = copy(count = (count - 1).coerceAtLeast(0))
    fun save(id: String, now: Long): CounterState {
        if (name.isBlank()) return this
        val item = CounterHistoryItem(id, name.trim(), count, now)
        return CounterState(history = (listOf(item) + history).take(100))
    }
}
