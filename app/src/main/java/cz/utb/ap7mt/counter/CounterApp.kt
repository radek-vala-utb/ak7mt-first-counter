package cz.utb.ap7mt.counter

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.LocalContext

@Composable
fun CounterApp() {
    val application = LocalContext.current.applicationContext as CounterApplication
    val model: CounterViewModel = viewModel(factory = viewModelFactory {
        initializer { CounterViewModel(application.counterRepository) }
    })
    val ui by model.ui.collectAsStateWithLifecycle()
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    Column(Modifier.fillMaxSize()) {
        Text("CounterApp", Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
        PrimaryTabRow(selectedTabIndex = selectedTab) {
            listOf("Počítadlo", "Historie").forEachIndexed { index, title ->
                Tab(selected = selectedTab == index, onClick = { selectedTab = index }, text = { Text(title) })
            }
        }
        if (ui.loading) {
            CircularProgressIndicator(Modifier.padding(24.dp))
        } else if (ui.error != null) {
            Text(ui.error.orEmpty(), Modifier.padding(24.dp))
            Button(onClick = model::reload) { Text("Načíst znovu") }
        } else if (selectedTab == 0) {
            CounterScreen(ui.data.name, ui.data.count, model::rename, model::increment,
                model::decrement, model::reset, Modifier.verticalScroll(rememberScrollState()), model::save)
        } else {
            HistoryScreen(ui.data.history, model::remove)
        }
    }
}
