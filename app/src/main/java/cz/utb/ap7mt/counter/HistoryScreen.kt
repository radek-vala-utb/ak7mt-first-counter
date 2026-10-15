package cz.utb.ap7mt.counter

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun LazyListScope.historyItems(history: List<CounterHistoryItem>, onRemove: (String) -> Unit) {
    if (history.isEmpty()) item { Text("Zatím nebylo uloženo žádné počítadlo.", Modifier.padding(24.dp)) }
    items(history, key = { it.id }) { item ->
        Card(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 4.dp)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(item.name, style = MaterialTheme.typography.titleMedium)
                Text(item.value.toString(), style = MaterialTheme.typography.headlineMedium)
                Text(Instant.ofEpochMilli(item.createdAt).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("d. M. yyyy HH:mm")))
                TextButton(onClick = { onRemove(item.id) }) { Text("Smazat záznam") }
            }
        }
    }
}

@Composable
fun HistoryScreen(history: List<CounterHistoryItem>, onRemove: (String) -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
        historyItems(history, onRemove)
    }
}
