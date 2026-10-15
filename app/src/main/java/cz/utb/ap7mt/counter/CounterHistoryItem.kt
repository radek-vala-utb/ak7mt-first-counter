package cz.utb.ap7mt.counter

@kotlinx.serialization.Serializable
data class CounterHistoryItem(
    val id: String,
    val name: String,
    val value: Int,
    val createdAt: Long,
)
