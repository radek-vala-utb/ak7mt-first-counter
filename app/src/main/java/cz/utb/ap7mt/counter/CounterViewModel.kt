package cz.utb.ap7mt.counter

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel

class CounterViewModel : ViewModel() {
    var name by mutableStateOf("")
        private set
    var count by mutableIntStateOf(0)
        private set

    fun rename(value: String) { name = value }
    fun increment() { if (count < Int.MAX_VALUE) count++ }
    fun decrement() { if (count > 0) count-- }
    fun reset() { count = 0 }
}
