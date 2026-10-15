package cz.utb.ap7mt.counter

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.counterDataStore by preferencesDataStore(name = "counter")

class CounterApplication : Application() {
    val counterRepository by lazy { CounterRepository(counterDataStore) }
}
