package cz.utb.ap7mt.counter

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel

class CounterViewModel : ViewModel() {
    var name by mutableStateOf("")
        private set
    var count by mutableIntStateOf(0)
        private set

    var history by mutableStateOf<List<CounterHistoryItem>>(emptyList())
        private set

    fun save() {
        if (name.isBlank()) return
        val item = CounterHistoryItem(java.util.UUID.randomUUID().toString(), name.trim(), count, System.currentTimeMillis())
        history = listOf(item) + history
        name = ""
        count = 0
    }
    fun remove(id: String) { history = history.filterNot { it.id == id } }

    fun rename(value: String) { name = value }
    fun increment() { if (count < Int.MAX_VALUE) count++ }
    fun decrement() { if (count > 0) count-- }
    fun reset() { count = 0 }
}
