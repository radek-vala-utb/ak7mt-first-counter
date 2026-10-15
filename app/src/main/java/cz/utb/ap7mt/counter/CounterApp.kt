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

@Composable
fun CounterApp(model: CounterViewModel = viewModel()) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    Column(Modifier.fillMaxSize()) {
        Text("CounterApp", Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
        PrimaryTabRow(selectedTabIndex = selectedTab) {
            listOf("Počítadlo", "Historie").forEachIndexed { index, title ->
                Tab(selected = selectedTab == index, onClick = { selectedTab = index }, text = { Text(title) })
            }
        }
        if (selectedTab == 0) {
            CounterScreen(model.name, model.count, model::rename, model::increment,
                model::decrement, model::reset, Modifier.verticalScroll(rememberScrollState()), model::save)
        } else {
            HistoryScreen(model.history, model::remove)
        }
    }
}
