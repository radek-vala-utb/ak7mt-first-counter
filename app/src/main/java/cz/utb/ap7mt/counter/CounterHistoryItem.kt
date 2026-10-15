package cz.utb.ap7mt.counter

data class CounterHistoryItem(
    val id: String,
    val name: String,
    val value: Int,
    val createdAt: Long,
)
