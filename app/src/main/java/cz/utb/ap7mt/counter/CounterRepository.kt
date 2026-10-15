package cz.utb.ap7mt.counter

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/** DataStore is created once by the application; tests can supply a temporary store. */
class CounterRepository(private val store: DataStore<Preferences>) {
    private val key = stringPreferencesKey("counter_state")
    private val json = Json { ignoreUnknownKeys = true }
    private fun decode(raw: String?): CounterState {
        val state = raw?.let { json.decodeFromString<CounterState>(it) } ?: CounterState()
        require(state.count >= 0 && state.history.all { it.value >= 0 }) { "Záporné počítadlo v uložených datech" }
        return state
    }
    val state: Flow<CounterState> = store.data.map { decode(it[key]) }

    suspend fun update(transform: (CounterState) -> CounterState) {
        store.edit { preferences ->
            // Read and change inside the same transaction: no lost increments.
            preferences[key] = json.encodeToString(transform(decode(preferences[key])))
        }
    }
}
