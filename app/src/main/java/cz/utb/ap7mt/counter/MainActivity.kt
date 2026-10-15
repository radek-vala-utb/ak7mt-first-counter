package cz.utb.ap7mt.counter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.LazyColumn

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize().safeDrawingPadding()) {
                    val model: CounterViewModel = viewModel()
                    LazyColumn {
                        item {
                            CounterScreen(model.name, model.count, model::rename, model::increment,
                                model::decrement, model::reset, onSave = model::save)
                        }
                        historyItems(model.history, model::remove)
                    }
                }
            }
        }
    }
}
