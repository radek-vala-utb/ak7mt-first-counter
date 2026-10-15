package cz.utb.ap7mt.counter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize().safeDrawingPadding()) {
                    CounterScreen()
                }
            }
        }
    }
}

@Composable
fun CounterScreen() {
    var name by remember { mutableStateOf("") }
    var count by remember { mutableIntStateOf(0) }
    val modifier = Modifier.verticalScroll(rememberScrollState())
    Column(modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Počítadlo", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Název počítadla") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Text(name.ifBlank { "Moje počítadlo" }, style = MaterialTheme.typography.titleLarge)
        Text(count.toString(), style = MaterialTheme.typography.displayLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { if (count > 0) count-- }, enabled = count > 0) { Text("−1") }
            Button(onClick = { if (count < Int.MAX_VALUE) count++ }, enabled = count < Int.MAX_VALUE) { Text("+1") }
            OutlinedButton(onClick = { count = 0 }, enabled = count > 0) { Text("Reset") }
        }
        
    }
}
