# CV3 – Ukládání snímků a seznam historie

Rozšíříme počítadlo o tlačítko Uložit do historie. Uloží se snímek názvu a hodnoty v daném okamžiku, formulář se vyprázdní a pod ním se zobrazí historie. Nezaznamenáváme každé kliknutí na +1.

## Cíl cvičení

- Navrhnout datový model pomocí data class.
- Měnit pozorovatelný seznam tvorbou nové hodnoty.
- Vykreslit položky přes LazyColumn a použít stabilní klíče.
- Rozlišit živé počítadlo a nezávislý uložený snímek.

## Příprava a výchozí stav

Pracujte v prvním repozitáři **ap7mt-counter**. Výchozí větev je **`first_counter_viewmodel`**, referenční výsledek **`first_counter_viewmodel_list`**. Otevřete kořen projektu v Android Studiu, dokončete Gradle Sync a nejprve spusťte výchozí aplikaci. Prostředí a spuštění popisuje [hlavní README](../README.md).

Pokud pokračujete ve vlastním funkčním řešení předchozího cvičení, založte novou větev přímo z něj. Následující příkazy jsou alternativa pro začátek z připravené referenční větve; spouštějte je v terminálu v kořeni správného repozitáře:

```bash
git status
git switch first_counter_viewmodel
git switch -c student/cv03
```

Před přepnutím musí být vaše změny uložené v commitu. Pokud pracovní větev už existuje, pokračujte příkazem `git switch student/cv03` bez `-c`. Při čerstvém klonování Git obvykle dohledá výchozí větev na origin; pokud ji nevidí, nejprve spusťte `git fetch origin`.

## Jak vkládat kód

Každý blok označený cestou k souboru níže obsahuje **celý výsledný soubor včetně importů**. Existující obsah nahraďte; u nového souboru jej vytvořte. Nevkládejte nové definice pod staré a nevytvářejte další bloky `plugins`, `dependencies` či další třídu se stejným názvem. Části kódu se odkazují jedna na druhou, proto může být rozpracovaný projekt mezi kroky dočasně označen chybami; kompletní sestavení proveďte až po propojení všech částí.

Kotlin soubory aplikace patří pod `app/src/main/java/cz/utb/ap7mt/counter/`. V Android Studiu použijte na příslušném balíčku **New → Kotlin Class/File** a zvolte soubor. Podbalíčky vytvářejte jako **Package**; řádek `package` v ukázce musí odpovídat umístění. Pokud IDE vygenerovalo prázdnou třídu, nahraďte ji celou. Značka `*` v některých importech zpřístupňuje veřejné názvy z daného balíčku; při kopírování zachovejte uvedené importy.

## Postup

1. Vytvořte model jednoho snímku.
2. Rozšiřte ViewModel o historii, uložení a odstranění.
3. Přidejte callback a tlačítko pro uložení.
4. Připravte vykreslení položek a prázdného seznamu.
5. Spojte formulář a historii do jedné LazyColumn.
6. Rozšiřte testy o nezávislost a identitu snímků.

Následuje podrobná realizace. Po každé části si ověřte její význam; při červeném podtržení rozlišujte chybějící soubor z dalšího kroku a skutečný překlep.

### Krok 1 – Model jednoho uloženého počítadla

Vytvořte datovou třídu. Její instance bude představovat jeden řádek historie.

**Vytvořte nový soubor `app/src/main/java/cz/utb/ap7mt/counter/CounterHistoryItem.kt`:**

<!-- source-file: app/src/main/java/cz/utb/ap7mt/counter/CounterHistoryItem.kt -->
```kotlin
package cz.utb.ap7mt.counter

data class CounterHistoryItem(
    val id: String,
    val name: String,
    val value: Int,
    val createdAt: Long,
)
```

`data class` poskytne mimo jiné porovnání hodnot a metodu `copy`. Vlastnosti s `val` po vytvoření nepřepisujeme.

| Vlastnost | Účel |
| --- | --- |
| `id` | Jedinečná identita pro vykreslování a smazání. |
| `name` | Název v okamžiku uložení. |
| `value` | Hodnota v okamžiku uložení. |
| `createdAt` | Čas uložení v milisekundách od unixové epochy. |

Dvě počítadla mohou mít stejný název i hodnotu, ale musí mít různá ID. Název proto nepoužíváme jako identifikátor.

### Krok 2 – Historie ve ViewModelu

Nahraďte původní ViewModel. Operace z CV2 zůstávají a přibudou `history`, `save()` a `remove(id)`.

**Nahraďte celý obsah souboru `app/src/main/java/cz/utb/ap7mt/counter/CounterViewModel.kt`:**

<!-- source-file: app/src/main/java/cz/utb/ap7mt/counter/CounterViewModel.kt -->
```kotlin
package cz.utb.ap7mt.counter

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel

class CounterViewModel : ViewModel() {
    var name by mutableStateOf("")
        private set
    var count by mutableIntStateOf(0)
        private set

    var history by mutableStateOf<List<CounterHistoryItem>>(emptyList())
        private set

    fun save() {
        if (name.isBlank()) return
        val item = CounterHistoryItem(java.util.UUID.randomUUID().toString(), name.trim(), count, System.currentTimeMillis())
        history = listOf(item) + history
        name = ""
        count = 0
    }
    fun remove(id: String) { history = history.filterNot { it.id == id } }

    fun rename(value: String) { name = value }
    fun increment() { if (count < Int.MAX_VALUE) count++ }
    fun decrement() { if (count > 0) count-- }
    fun reset() { count = 0 }
}
```

`mutableStateOf<List<CounterHistoryItem>>(emptyList())` obsahuje seznam, jehož **nahrazení** je pro Compose pozorovatelné. `listOf(item) + history` vytvoří nový seznam s novým snímkem na začátku. `filterNot` vytvoří seznam bez vybraného ID.

`save()` nejprve odmítne prázdný či pouze mezerový název. `trim()` odstraní okrajové mezery, UUID vytvoří identitu a `System.currentTimeMillis()` dodá čas. Teprve po vytvoření snímku nastavíme název na prázdný text a hodnotu na nulu. Uložený objekt se tím nezmění.

Nepřepisujte tento postup na `mutableListOf().add(...)` mimo pozorovatelný stav: běžný mutable seznam sám Compose neinformuje o změně.

### Krok 3 – Tlačítko pro uložení

Nahraďte obrazovku novou verzí. Přidaný parametr `onSave` umožňuje předat událost uložení, aniž by obrazovka sama pracovala se seznamem.

**Nahraďte celý obsah souboru `app/src/main/java/cz/utb/ap7mt/counter/CounterScreen.kt`:**

<!-- source-file: app/src/main/java/cz/utb/ap7mt/counter/CounterScreen.kt -->
```kotlin
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
    onSave: (() -> Unit)? = null,
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
        if (onSave != null) {
            Button(onClick = onSave, enabled = name.isNotBlank()) { Text("Uložit do historie") }
        }
    }
}
```

`onSave: (() -> Unit)? = null` znamená volitelný callback. Je-li `null`, tlačítko se nevykreslí; jinak tlačítko zavolá předanou funkci. `enabled = name.isNotBlank()` dovolí uložení až po zadání názvu. Stejnou podmínku ověřuje i ViewModel.

Hodnota 0 je platný snímek. Tlačítko proto neomezujeme podmínkou `count > 0`. Reset a uložení jsou rozdílné akce: Reset zachovává název, uložení připravuje nový prázdný formulář.

### Krok 4 – Vykreslení historie

Připravte soubor pro řádky historie i samostatnou obrazovku, kterou využijeme v CV4.

**Vytvořte nový soubor `app/src/main/java/cz/utb/ap7mt/counter/HistoryScreen.kt`:**

<!-- source-file: app/src/main/java/cz/utb/ap7mt/counter/HistoryScreen.kt -->
```kotlin
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
```

`LazyColumn` vytváří potřebné položky seznamu postupně. `historyItems` je pomocná rozšiřující funkce pro `LazyListScope`: zapisuje položky do již existující LazyColumn. Proto sama není označená `@Composable`; Composable obsah vzniká až uvnitř bloků `item` a `items`.

`items(history, key = { it.id })` používá ID jako stabilní identitu. Tlačítko smazání předá ID zpět přes `onRemove`. Změnu seznamu provádí ViewModel.

Při prázdném seznamu přidáme jednu informativní položku. Čas převádíme z milisekund na `Instant`, pak do časového pásma zařízení a nakonec na čitelný text. `HistoryScreen` obaluje stejné položky vlastní LazyColumn; v tomto kroku jej ještě nemusíme volat.

### Krok 5 – Formulář a seznam v jednom scrollování

Nahraďte aktivitu. Vnější `LazyColumn` bude obsahovat formulář jako první položku a pod ním snímky historie.

**Nahraďte celý obsah souboru `app/src/main/java/cz/utb/ap7mt/counter/MainActivity.kt`:**

<!-- source-file: app/src/main/java/cz/utb/ap7mt/counter/MainActivity.kt -->
```kotlin
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
```

`item { CounterScreen(...) }` vloží formulář jako jedinou položku. `historyItems(...)` přidá další položky do stejného seznamu. Formuláři zde **nepředáváme** modifier s `verticalScroll`: celou obrazovku posouvá vnější LazyColumn. Vyhneme se dvěma svislým scrollům v sobě s neomezenou výškou.

Callback `onSave = model::save` propojuje tlačítko s uložením snímku. V `historyItems(model.history, model::remove)` předáváme aktuální seznam a operaci smazání.

**Průběžná kontrola:** Uložte „Návštěvníci“ s hodnotou 3. Formulář se vyprázdní a pod ním se objeví snímek s původním názvem, hodnotou a časem.

### Krok 6 – Testy snímků

Nahraďte dosavadní testovací soubor. Původní testy zůstávají a přibývá scénář historie.

**Nahraďte celý obsah souboru `app/src/test/java/cz/utb/ap7mt/counter/CounterViewModelTest.kt`:**

<!-- source-file: app/src/test/java/cz/utb/ap7mt/counter/CounterViewModelTest.kt -->
```kotlin
package cz.utb.ap7mt.counter

import org.junit.Assert.*
import org.junit.Test

class CounterViewModelTest {
    @Test fun decrementStopsAtZero() {
        val model = CounterViewModel()
        model.decrement()
        assertEquals(0, model.count)
        model.increment(); model.increment(); model.decrement()
        assertEquals(1, model.count)
    }
    @Test fun resetPreservesName() {
        val model = CounterViewModel()
        model.rename("Návštěvníci"); model.increment(); model.reset()
        assertEquals("Návštěvníci", model.name)
        assertEquals(0, model.count)
    }

    @Test fun snapshotsAreIndependentAndRemovedById() {
        val model = CounterViewModel()
        model.save()
        assertTrue(model.history.isEmpty())
        repeat(2) { model.rename(" Stejný "); model.increment(); model.save() }
        assertEquals("", model.name)
        assertEquals(0, model.count)
        assertEquals(2, model.history.size)
        assertEquals("Stejný", model.history.first().name)
        model.increment()
        assertEquals(1, model.history.first().value)
        val retained = model.history.last()
        model.remove(model.history.first().id)
        assertEquals(listOf(retained), model.history)
    }
}
```

Nový test nejprve zkusí uložení bez názvu, potom uloží dva stejně pojmenované snímky. Ověří reset formuláře, oříznutí mezer, nezávislost snímku na další změně hodnoty a odstranění právě jednoho ID.

`assertEquals(listOf(retained), model.history)` ověřuje celý obsah seznamu, nejen jeho délku. Pomůže odhalit chybu, kdy by smazání odstranilo nesprávný snímek.

## Ověření aplikace

Po dokončení všech kroků spusťte aplikaci přes Run. Vyzkoušejte tyto situace v uvedeném pořadí:

| Akce | Očekávaný výsledek |
| --- | --- |
| Uložit s prázdným názvem | Tlačítko je zakázané; ViewModel také odmítá uložení. |
| Uložit dva snímky se stejným názvem | Zobrazí se dvě různé položky, novější první. |
| Po uložení změnit živé počítadlo | Uložené hodnoty se nezmění. |
| Smazat jednu z položek | Druhá zůstane. |
| Smazat poslední položku | Zobrazí se prázdný stav. |
| Otočit zařízení | Historie zůstává ve ViewModelu. |
| Ukončit proces aplikace | Data zatím nemají trvalé úložiště. |

### Sestavení a testy

Příkazy patří do kořene projektu, vedle `gradlew` a `gradlew.bat`.

macOS / Linux:

```bash
./gradlew assembleDebug testDebugUnitTest lintDebug
```

Windows PowerShell:

```powershell
.\gradlew.bat assembleDebug testDebugUnitTest lintDebug
```

`assembleDebug` vytváří debug APK, `testDebugUnitTest` spouští lokální testy na počítači a `lintDebug` hledá problémy statickou analýzou. Očekávejte `BUILD SUCCESSFUL`; samotné sestavení neověřuje ruční scénáře. Lokální testy najdete v `app/src/test`, report v `app/build/reports/tests/testDebugUnitTest/index.html`. U testu lze v Android Studiu použít také zelenou šipku vedle jeho názvu.

## Nejčastější problémy

| Problém | Kontrola nebo oprava |
| --- | --- |
| Seznam se neaktualizuje | Nahrazujte history novým seznamem; neměňte obyčejný mutable seznam na místě. |
| Chyba duplicitního klíče | Každý snímek potřebuje vlastní UUID; name není vhodný key. |
| Chyba nekonečné výšky scrollování | CounterScreen v item nesmí mít vlastní verticalScroll. |
| Smazání ovlivní všechny stejné názvy | Filtrujte podle ID, nikoliv názvu. |
| Datum se nezobrazuje | Zkontrolujte importy java.time a hodnotu createdAt v milisekundách. |

## Samostatná práce

Zobrazte součet hodnot všech uložených snímků. Počítejte jej z historie, neuchovávejte vedle ní druhý nezávislý součet. Zvažte typ Long, pokud by součet mohl přesáhnout Int.

## Kontrolní seznam a uložení do Gitu

- [ ] Začal/a jsem ze správného výchozího milníku a pracuji ve vlastní větvi.
- [ ] Vytvořil/a jsem všechny uvedené soubory v příslušných balíčcích.
- [ ] Dokážu vysvětlit, která část vlastní stav a jak se změna dostane na obrazovku.
- [ ] Vyzkoušel/a jsem uvedené ruční scénáře, včetně chybových a hraničních situací.
- [ ] Sestavení, lokální testy a lint skončily bez chyb; případné instrumentační testy jsem spustil/a zvlášť.
- [ ] Do commitu nepřidávám `local.properties`, `build` ani nastavení IDE.

```bash
git status
git diff
git add app gradle build.gradle.kts
git commit -m "Dokonci CV3: Ukládání snímků a seznam historie"
git status
```

Před `git add` ověřte, že změny patří k tomuto cvičení. Příkaz zahrnuje také odstraněné testy v `app`; adresáře build zůstávají ignorované. Referenční změny lze prohlédnout bez přepnutí větve:

```bash
git diff origin/first_counter_viewmodel..origin/first_counter_viewmodel_list -- app gradle
```

Případné rozšíření ze samostatné práce uložte do dalšího commitu, aby bylo zřejmé, co tvoří základní řešení.

## Dokumentace

- [Compose – seznamy](https://developer.android.com/develop/ui/compose/lists)
