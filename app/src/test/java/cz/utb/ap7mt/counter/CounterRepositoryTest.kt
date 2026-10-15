package cz.utb.ap7mt.counter

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class CounterRepositoryTest {
    @get:Rule val folder = TemporaryFolder()

    @Test fun roundTripAndConcurrentUpdates() = runBlocking {
        val file = folder.newFolder().resolve("test.preferences_pb")
        val firstScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        val store = PreferenceDataStoreFactory.create(scope = firstScope) { file }
        val repository = CounterRepository(store)
        val name = "A | B ;;; \"Citace\"".replace("\\", "")
        repository.update { it.copy(name = name) }
        coroutineScope { repeat(30) { launch { repository.update { it.increment() } } } }
        repository.update { it.save("id", 12).copy(name = "Rozepsané", count = 4) }
        firstScope.coroutineContext[Job]!!.cancelAndJoin()
        val secondScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        try {
            val reopened = CounterRepository(PreferenceDataStoreFactory.create(scope = secondScope) { file })
            val state = reopened.state.first()
            assertEquals(30, state.history.single().value)
            assertEquals(name, state.history.single().name)
            assertEquals("Rozepsané", state.name)
            assertEquals(4, state.count)
            reopened.update { it.copy(history = emptyList()) }
            assertTrue(reopened.state.first().history.isEmpty())
        } finally { secondScope.coroutineContext[Job]!!.cancelAndJoin() }
    }

    @Test fun corruptJsonIsNotOverwritten() = runBlocking {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        val store = PreferenceDataStoreFactory.create(scope = scope) { folder.newFolder().resolve("bad.preferences_pb") }
        val key = stringPreferencesKey("counter_state")
        try {
            store.edit { it[key] = "not json" }
            val result = runCatching { CounterRepository(store).update { CounterState() } }
            assertTrue(result.isFailure)
            assertEquals("not json", store.data.first()[key])
        } finally { scope.coroutineContext[Job]!!.cancelAndJoin() }
    }
}
