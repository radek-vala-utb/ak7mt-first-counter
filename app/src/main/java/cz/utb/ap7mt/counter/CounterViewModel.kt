package cz.utb.ap7mt.counter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

data class CounterUiState(
    val data: CounterState = CounterState(),
    val loading: Boolean = true,
    val error: String? = null,
)

class CounterViewModel(private val repository: CounterRepository) : ViewModel() {
    private val mutableUi = MutableStateFlow(CounterUiState())
    val ui = mutableUi.asStateFlow()
    private val operations = Channel<(CounterState) -> CounterState>(Channel.UNLIMITED)
    private var observation: Job? = null

    init {
        reload()
        viewModelScope.launch {
            for (operation in operations) {
                try {
                    repository.update(operation)
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    // Reject queued edits after an error, instead of replaying stale input later.
                    while (operations.tryReceive().isSuccess) { }
                    mutableUi.update { it.copy(error = "Zápis se nezdařil. Načtěte uložená data znovu.") }
                }
            }
        }
    }
    fun reload() {
        observation?.cancel()
        mutableUi.update { it.copy(loading = true, error = null) }
        observation = viewModelScope.launch {
            try {
                repository.state.collect { data ->
                    mutableUi.update { it.copy(data = data, loading = false) }
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                mutableUi.update { it.copy(loading = false, error = "Uložená data nelze načíst. Nebyla přepsána.") }
            }
        }
    }
    private fun change(transform: (CounterState) -> CounterState) {
        if (!ui.value.loading && ui.value.error == null) operations.trySend(transform)
    }
    fun rename(name: String) = change { it.copy(name = name) }
    fun increment() = change { it.increment() }
    fun decrement() = change { it.decrement() }
    fun reset() = change { it.copy(count = 0) }
    fun save() {
        val id = UUID.randomUUID().toString()
        val time = System.currentTimeMillis()
        change { it.save(id, time) }
    }
    fun remove(id: String) = change { it.copy(history = it.history.filterNot { item -> item.id == id }) }
    override fun onCleared() { operations.close() }
}
