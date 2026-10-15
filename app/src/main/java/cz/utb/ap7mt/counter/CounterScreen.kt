package cz.utb.ap7mt.counter

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CounterScreen(
    name: String,
    count: Int,
    onNameChange: (String) -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Počítadlo", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(value = name, onValueChange = onNameChange, label = { Text("Název počítadla") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Text(name.ifBlank { "Moje počítadlo" }, style = MaterialTheme.typography.titleLarge)
        Text(count.toString(), style = MaterialTheme.typography.displayLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onDecrement, enabled = count > 0) { Text("−1") }
            Button(onClick = onIncrement, enabled = count < Int.MAX_VALUE) { Text("+1") }
            OutlinedButton(onClick = onReset, enabled = count > 0) { Text("Reset") }
        }
        
    }
}
