# CV5 – Trvalé uložení počítadla pomocí DataStore

Počítadlo a historie nyní přežijí i nové spuštění procesu. DataStore bude ukládat malý celek CounterState do JSON. UI dál dostává stav z ViewModelu, ale počáteční hodnoty se nejprve načtou z disku a každá změna se zapíše.

## Cíl cvičení

- Rozlišit stav v paměti a stav uložený na disku.
- Použít Preferences DataStore a Kotlin serialization.
- Předat závislost přes konstruktor a ViewModel factory.
- Číst StateFlow v Compose a zpracovat načítání i chybu.
- Ověřit obnovu dat a oddělit lokální testy od testů na zařízení.

## Příprava a výchozí stav

Pracujte v prvním repozitáři **ap7mt-counter**. Výchozí větev je **`first_counter_viewmodel_tabs`**, referenční výsledek **`first_counter_persistance`**. Otevřete kořen projektu v Android Studiu, dokončete Gradle Sync a nejprve spusťte výchozí aplikaci. Prostředí a spuštění popisuje [hlavní README](../README.md).

Pokud pokračujete ve vlastním funkčním řešení předchozího cvičení, založte novou větev přímo z něj. Následující příkazy jsou alternativa pro začátek z připravené referenční větve; spouštějte je v terminálu v kořeni správného repozitáře:

```bash
git status
git switch first_counter_viewmodel_tabs
git switch -c student/cv05
```

Před přepnutím musí být vaše změny uložené v commitu. Pokud pracovní větev už existuje, pokračujte příkazem `git switch student/cv05` bez `-c`. Při čerstvém klonování Git obvykle dohledá výchozí větev na origin; pokud ji nevidí, nejprve spusťte `git fetch origin`.

## Jak vkládat kód

Každý blok označený cestou k souboru níže obsahuje **celý výsledný soubor včetně importů**. Existující obsah nahraďte; u nového souboru jej vytvořte. Nevkládejte nové definice pod staré a nevytvářejte další bloky `plugins`, `dependencies` či další třídu se stejným názvem. Části kódu se odkazují jedna na druhou, proto může být rozpracovaný projekt mezi kroky dočasně označen chybami; kompletní sestavení proveďte až po propojení všech částí.

Kotlin soubory aplikace patří pod `app/src/main/java/cz/utb/ap7mt/counter/`. V Android Studiu použijte na příslušném balíčku **New → Kotlin Class/File** a zvolte soubor. Podbalíčky vytvářejte jako **Package**; řádek `package` v ukázce musí odpovídat umístění. Pokud IDE vygenerovalo prázdnou třídu, nahraďte ji celou. Značka `*` v některých importech zpřístupňuje veřejné názvy z daného balíčku; při kopírování zachovejte uvedené importy.

## Postup

1. Přidejte DataStore, serializaci a podporu Flow v Compose.
2. Vytvořte serializovatelný model celého stavu.
3. Přidejte CounterRepository pro čtení a transakční zápis.
4. V Application vytvořte jednu instanci úložiště a registrujte ji v manifestu.
5. Upravte ViewModel pro načtení a seřazené asynchronní zápisy.
6. Připojte factory a vykreslete načítání, chybu nebo formulář.
7. Nahraďte staré testy novými testy stavu a úložiště; přidejte UI test.

Následuje podrobná realizace. Po každé části si ověřte její význam; při červeném podtržení rozlišujte chybějící soubor z dalšího kroku a skutečný překlep.

### Krok 1 – Knihovny a plugin serializace

Nejprve otevřete katalog verzí a potom modulový Gradle soubor. Pro tento připravený projekt můžete nahradit celé soubory následujícími verzemi. Pokud jste přidávali vlastní bonusové závislosti, zachovejte je a doplňte jen nové položky, nikoliv duplicitní sekce TOML.

**Nahraďte celý obsah souboru `gradle/libs.versions.toml`:**

<!-- source-file: gradle/libs.versions.toml -->
```toml
[versions]
agp = "9.4.1"
coreKtx = "1.19.0"
junit = "4.13.2"
junitVersion = "1.1.5"
espressoCore = "3.7.0"
lifecycleRuntimeKtx = "2.11.0"
activityCompose = "1.13.0"
kotlin = "2.2.10"
composeBom = "2026.02.01"
datastorePreferences = "1.2.1"

[libraries]
androidx-lifecycle-runtime-compose = { module = "androidx.lifecycle:lifecycle-runtime-compose", version.ref = "lifecycleRuntimeKtx" }
kotlinx-serialization-json = { module = "org.jetbrains.kotlinx:kotlinx-serialization-json", version = "1.7.3" }
kotlinx-coroutines-test = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-test", version = "1.9.0" }

androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
junit = { group = "junit", name = "junit", version.ref = "junit" }
androidx-junit = { group = "androidx.test.ext", name = "junit", version.ref = "junitVersion" }
androidx-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espressoCore" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycleRuntimeKtx" }
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycleRuntimeKtx" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-compose-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-compose-ui-test-manifest = { group = "androidx.compose.ui", name = "ui-test-manifest" }
androidx-compose-ui-test-junit4 = { group = "androidx.compose.ui", name = "ui-test-junit4" }
androidx-compose-material3 = { group = "androidx.compose.material3", name = "material3" }
androidx-datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "datastorePreferences" }

[plugins]
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }

android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
```

**Nahraďte celý obsah souboru `app/build.gradle.kts`:**

<!-- source-file: app/build.gradle.kts -->
```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}
android {
    namespace = "cz.utb.ap7mt.counter"
    compileSdk { version = release(37) }
    defaultConfig {
        applicationId = "cz.utb.ap7mt.counter"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures { compose = true }
}
dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.lifecycle.runtime.compose)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
```

V katalogu přibudou aliasy pro lifecycle-runtime-compose, kotlinx-serialization-json, coroutines-test a plugin kotlin-serialization. Závislost DataStore už má v katalogu připravený alias. V `app/build.gradle.kts` aktivujeme serializační plugin a přidáme příslušné knihovny.

| Položka | Účel |
| --- | --- |
| `datastore-preferences` | Čtení a transakční změna malého úložiště klíčů a hodnot. |
| `kotlinx-serialization-json` | Převod datového modelu na JSON a zpět. |
| Plugin `kotlin-serialization` | Vygenerování serializérů pro třídy s `@Serializable`. |
| `lifecycle-runtime-compose` | Napojení Flow do Compose s ohledem na životní cyklus. |
| `kotlinx-coroutines-test` | Nástroje pro testování korutin; využijeme je i v navazujícím repozitáři. |

Verze ponechte podle připraveného základu. Po úpravě proveďte Gradle Sync. Kořenový `build.gradle.kts` se v této variantě nemění.

**Průběžná kontrola:** Gradle Sync dokončí konfiguraci. Chybu pluginu nebo závislosti vyřešte před přidáváním Kotlin souborů.

### Krok 2 – Serializovatelný stav

Historii doplníme o anotaci `@Serializable` a přidáme model, který drží celý ukládaný obsah. V tomto kroku není nutné měnit vzhled formuláře.

**Nahraďte celý obsah souboru `app/src/main/java/cz/utb/ap7mt/counter/CounterHistoryItem.kt`:**

<!-- source-file: app/src/main/java/cz/utb/ap7mt/counter/CounterHistoryItem.kt -->
```kotlin
package cz.utb.ap7mt.counter

@kotlinx.serialization.Serializable
data class CounterHistoryItem(
    val id: String,
    val name: String,
    val value: Int,
    val createdAt: Long,
)
```

**Vytvořte nový soubor `app/src/main/java/cz/utb/ap7mt/counter/CounterState.kt`:**

<!-- source-file: app/src/main/java/cz/utb/ap7mt/counter/CounterState.kt -->
```kotlin
package cz.utb.ap7mt.counter

import kotlinx.serialization.Serializable

@Serializable
data class CounterState(
    val name: String = "",
    val count: Int = 0,
    val history: List<CounterHistoryItem> = emptyList(),
) {
    fun increment() = copy(count = if (count < Int.MAX_VALUE) count + 1 else count)
    fun decrement() = copy(count = (count - 1).coerceAtLeast(0))
    fun save(id: String, now: Long): CounterState {
        if (name.isBlank()) return this
        val item = CounterHistoryItem(id, name.trim(), count, now)
        return CounterState(history = (listOf(item) + history).take(100))
    }
}
```

`CounterState` obsahuje název, číselnou hodnotu a historii. Výchozí konstruktor představuje prázdné počítadlo. Metoda `copy` vrací novou hodnotu s upravenými položkami; původní objekt se nemění.

`increment()` a `decrement()` přesouvají pravidla číselných mezí do modelu. `save(id, now)` vytvoří snímek a vrátí stav s prázdným formulářem a novou historií. ID a čas přijímá jako parametry, proto jej můžeme testovat s předem známými hodnotami. `take(100)` ponechá maximálně sto nejnovějších položek.

Anotaci potřebuje jak celý stav, tak vnořený model snímku. Samotná anotace bez serializačního pluginu nestačí. Výsledný JSON zahrnuje také název, takže běžné oddělovače jako `|` nebo `;;;` nemusíme ručně escapovat.

### Krok 3 – Repository pro DataStore

Vytvořte třídu, která dostává úložiště přes konstruktor. UI nebude znát formát JSON ani klíč uložených dat.

**Vytvořte nový soubor `app/src/main/java/cz/utb/ap7mt/counter/CounterRepository.kt`:**

<!-- source-file: app/src/main/java/cz/utb/ap7mt/counter/CounterRepository.kt -->
```kotlin
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
```

`stringPreferencesKey("counter_state")` určuje klíč jednoho JSON řetězce. `state` je Flow, tedy zdroj postupně přicházejících hodnot. `map` převede Preferences na náš model. Když klíč zatím neexistuje, vrátíme výchozí CounterState.

`update` je `suspend` funkce. Přijímá transformaci starého stavu na nový a provede ji uvnitř DataStore `edit`. Aktuální hodnotu čteme **uvnitř téže transakce**, ve které ji zapisujeme. Dvě po sobě jdoucí operace tak nesmějí obě vycházet ze stejné zastaralé hodnoty.

Uložení snímku a vynulování formuláře je jedna změna CounterState a jeden zápis. Při selhání se neuloží jen polovina této operace. Neplatný JSON nebo záporná uložená hodnota vyvolají chybu; kód je tiše nenahradí prázdnou historií. Význam `suspend` a životní cyklus spuštěné práce rozebíráme u ViewModelu níže.

### Krok 4 – Jedna instance úložiště pro aplikaci

Vytvořte `CounterApplication.kt` a registrujte tuto Application v manifestu. Jde o novou třídu vedle MainActivity, nikoliv o její náhradu.

**Vytvořte nový soubor `app/src/main/java/cz/utb/ap7mt/counter/CounterApplication.kt`:**

<!-- source-file: app/src/main/java/cz/utb/ap7mt/counter/CounterApplication.kt -->
```kotlin
package cz.utb.ap7mt.counter

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.counterDataStore by preferencesDataStore(name = "counter")

class CounterApplication : Application() {
    val counterRepository by lazy { CounterRepository(counterDataStore) }
}
```

**Nahraďte celý obsah souboru `app/src/main/AndroidManifest.xml`:**

<!-- source-file: app/src/main/AndroidManifest.xml -->
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <application android:name=".CounterApplication" android:label="CounterApp" android:theme="@style/Theme.Counter" android:allowBackup="false" android:supportsRtl="true">
        <activity android:name=".MainActivity" android:exported="true" android:windowSoftInputMode="adjustResize">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

Delegát `Context.counterDataStore` leží mimo třídu a vytváří DataStore pro soubor se jménem `counter`. `counterRepository by lazy` vytvoří repository při prvním použití a pak vrací stejnou instanci. Application žije po dobu procesu a je vhodným místem pro tento jednoduchý kontejner závislostí.

Atribut `android:name=".CounterApplication"` říká Androidu, aby použil naši Application. Pokud jej vynecháte, pozdější přetypování applicationContext na CounterApplication selže. Nepřidávejte druhý element `<application>`; nahraďte nebo upravte existující.

Úložiště nevytváříme znovu při každém vykreslení obrazovky ani pro každou operaci. MainActivity ani Composable nepotřebují uchovávat cestu k souboru.

### Krok 5 – Načítání, živý stav a fronta zápisů

Nahraďte ViewModel novou verzí. Jeho konstruktor nyní vyžaduje CounterRepository, takže před dokončením dalšího kroku už nebude fungovat původní výchozí vytvoření `viewModel()` bez factory.

**Nahraďte celý obsah souboru `app/src/main/java/cz/utb/ap7mt/counter/CounterViewModel.kt`:**

<!-- source-file: app/src/main/java/cz/utb/ap7mt/counter/CounterViewModel.kt -->
```kotlin
package cz.utb.ap7mt.counter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

data class CounterUiState(
    val data: CounterState = CounterState(),
    val loading: Boolean = true,
    val error: String? = null,
)

class CounterViewModel(private val repository: CounterRepository) : ViewModel() {
    private val mutableUi = MutableStateFlow(CounterUiState())
    val ui = mutableUi.asStateFlow()
    private val operations = Channel<(CounterState) -> CounterState>(Channel.UNLIMITED)
    private var observation: Job? = null

    init {
        reload()
        viewModelScope.launch {
            for (operation in operations) {
                try {
                    repository.update(operation)
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    // Reject queued edits after an error, instead of replaying stale input later.
                    while (operations.tryReceive().isSuccess) { }
                    mutableUi.update { it.copy(error = "Zápis se nezdařil. Načtěte uložená data znovu.") }
                }
            }
        }
    }
    fun reload() {
        observation?.cancel()
        mutableUi.update { it.copy(loading = true, error = null) }
        observation = viewModelScope.launch {
            try {
                // Load once. While editing, the ViewModel owns the live UI state;
                // delayed disk emissions must not move the text cursor backwards.
                val data = repository.state.first()
                mutableUi.update { it.copy(data = data, loading = false) }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                mutableUi.update { it.copy(loading = false, error = "Uložená data nelze načíst. Nebyla přepsána.") }
            }
        }
    }
    private fun change(transform: (CounterState) -> CounterState) {
        if (!ui.value.loading && ui.value.error == null) {
            mutableUi.update { it.copy(data = transform(it.data)) }
            operations.trySend(transform)
        }
    }
    fun rename(name: String) = change { it.copy(name = name) }
    fun increment() = change { it.increment() }
    fun decrement() = change { it.decrement() }
    fun reset() = change { it.copy(count = 0) }
    fun save() {
        val id = UUID.randomUUID().toString()
        val time = System.currentTimeMillis()
        change { it.save(id, time) }
    }
    fun remove(id: String) = change { it.copy(history = it.history.filterNot { item -> item.id == id }) }
    override fun onCleared() { operations.close() }
}
```

Tento soubor přidává několik nových pojmů; nejprve si projděte jejich úlohu:

| Konstrukce | Úloha v příkladu |
| --- | --- |
| `CounterUiState` | Data obrazovky doplněná o příznak načítání a případnou chybu. |
| `MutableStateFlow` | Měnitelný držitel aktuálního stavu, který může UI pozorovat. |
| `asStateFlow()` | Zpřístupnění čtení bez veřejného setteru. |
| `viewModelScope.launch` | Spustí korutinu svázanou se životností ViewModelu. |
| `suspend` | Funkci lze pozastavit při čekání; voláme ji z korutiny nebo jiné suspend funkce. |
| `Channel` | Fronta operací čekajících na zápis. |
| `first()` | Počká na první hodnotu Flow a ukončí tento odběr. |

`reload()` nejprve nastaví načítání a získá první uložený stav. Teprve pak lze pracovat s formulářem. Nevycházíme z prázdných hodnot a nezapisujeme je přes existující data před načtením.

Po načtení vlastní **živý stav** ViewModel. `change()` jej mění okamžitě a stejnou transformaci vloží do fronty. Díky tomu psaní nečeká na disk a opožděný výsledek zápisu nevrací starší text do pole. Jedna korutina zpracovává operace za sebou a zapisuje je přes repository. Tato výuková varianta předpokládá jediného vlastníka změn počítadla.

Při chybě zápisu se čekající operace zahodí a UI nabídne nové načtení posledních uložených dat. Zobrazení nové hodnoty probíhá dříve než dokončení jejího zápisu. `CancellationException` znovu vyhazujeme, protože ukončení korutiny při zániku ViewModelu není běžná chyba úložiště.

Rozhraní metod `rename`, `increment`, `decrement`, `reset`, `save` a `remove` zůstává podobné. `save()` vytvoří ID a čas jednou, aby transformace použitá pro živý stav i pro disk vytvořila tentýž snímek.

### Krok 6 – Factory a stav v Compose

Nahraďte kořenovou Composable funkci. CounterScreen, HistoryScreen a MainActivity se v tomto kroku měnit nemusí.

**Nahraďte celý obsah souboru `app/src/main/java/cz/utb/ap7mt/counter/CounterApp.kt`:**

<!-- source-file: app/src/main/java/cz/utb/ap7mt/counter/CounterApp.kt -->
```kotlin
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
```

`applicationContext` získáme z LocalContext a převedeme na registrovanou CounterApplication. `viewModelFactory` s `initializer` určuje, jak vytvořit ViewModel s repository v konstruktoru. Samotné získání instance stále provádí `viewModel()`, takže zachovává správnou životnost.

`val ui by model.ui.collectAsStateWithLifecycle()` zpřístupňuje StateFlow jako stav čitelný v Compose. Podmínky nejprve zobrazí indikátor načítání, potom případnou chybu a až potom formulář nebo historii. Tím zabráníme úpravám nad nenačtenými daty.

Místo `model.name` a `model.count` nyní předáváme `ui.data.name` a `ui.data.count`. Metody pro události se předávají stejně jako dřív. Tlačítko „Načíst znovu“ znovu spustí čtení; neprovádí reset ani mazání souboru.

**Průběžná kontrola:** Po spuštění lze upravit a uložit počítadlo. Pokud aplikace spadne při přetypování Application, vraťte se ke kroku s manifestem.

### Krok 7 – Testy stavu, úložiště a obrazovky

Vytvořte dva lokální testovací soubory v `src/test` a jeden test obrazovky v `src/androidTest`. Starý CounterViewModelTest z předchozí větve následně odstraňte podle samostatné části níže.

**Vytvořte nový soubor `app/src/test/java/cz/utb/ap7mt/counter/CounterStateTest.kt`:**

<!-- source-file: app/src/test/java/cz/utb/ap7mt/counter/CounterStateTest.kt -->
```kotlin
package cz.utb.ap7mt.counter

import org.junit.Assert.*
import org.junit.Test

class CounterStateTest {
    @Test fun bounds() {
        assertEquals(0, CounterState().decrement().count)
        assertEquals(Int.MAX_VALUE, CounterState(count = Int.MAX_VALUE).increment().count)
    }
    @Test fun snapshotAndResetAreOneChange() {
        val original = CounterState("  Hosté  ", 7)
        val saved = original.save("one", 42)
        assertEquals("", saved.name)
        assertEquals(0, saved.count)
        assertEquals(CounterHistoryItem("one", "Hosté", 7, 42), saved.history.single())
        assertEquals(7, original.count)
        assertEquals(CounterState(), CounterState("  ").save("empty", 1).copy(name = ""))
    }
    @Test fun retainsOnlyNewestHundred() {
        var state = CounterState()
        repeat(101) { state = state.copy(name = "N").save(it.toString(), it.toLong()) }
        assertEquals(100, state.history.size)
        assertEquals("100", state.history.first().id)
        assertEquals("1", state.history.last().id)
    }
}
```

**Vytvořte nový soubor `app/src/test/java/cz/utb/ap7mt/counter/CounterRepositoryTest.kt`:**

<!-- source-file: app/src/test/java/cz/utb/ap7mt/counter/CounterRepositoryTest.kt -->
```kotlin
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
```

**Vytvořte nový soubor `app/src/androidTest/java/cz/utb/ap7mt/counter/CounterAppTest.kt`:**

<!-- source-file: app/src/androidTest/java/cz/utb/ap7mt/counter/CounterAppTest.kt -->
```kotlin
package cz.utb.ap7mt.counter

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class CounterAppTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun rapidTypingSavingAndTabs() {
        compose.waitUntil(10_000) {
            compose.onAllNodesWithText("Název počítadla").fetchSemanticsNodes().isNotEmpty()
        }
        val name = "Návštěvníci | ;;; " + System.nanoTime()
        compose.onNodeWithText("Reset").performClick()
        compose.onNodeWithText("Název počítadla").performTextReplacement(name)
        repeat(5) { compose.onNodeWithText("+1").performClick() }
        compose.onNodeWithText("Název počítadla").assertTextContains(name)
        compose.onNodeWithText("Uložit do historie").performScrollTo().performClick()
        compose.onNodeWithText("Historie").performClick()
        compose.onNodeWithText(name).assertIsDisplayed()
        compose.onNodeWithText("5").assertIsDisplayed()
        compose.onAllNodesWithText("Smazat záznam")[0].performClick()
        compose.onNodeWithText(name).assertDoesNotExist()
    }
}
```

`CounterStateTest` ověřuje číselné hranice, vytvoření snímku s resetem formuláře a omezení historie na sto položek. Nepotřebuje Android.

`CounterRepositoryTest` používá skutečný DataStore v dočasné složce. Ověřuje souběžné přírůstky, JSON s nestandardními znaky, zavření původního úložiště a obnovení dat novou instancí. Původní scope se před znovuotevřením ukončí; dva aktivní DataStore pro jeden soubor bychom vytvářet neměli. Druhý test vloží neplatný JSON a kontroluje, že neúspěšná změna původní obsah nepřepíše.

`CounterAppTest` běží v Androidu a obsluhuje skutečné UI: zadání názvu, přírůstky, uložení, přepnutí záložky a smazání snímku. `createAndroidComposeRule` spustí MainActivity; `waitUntil` počká na načtení formuláře. Test vytváří vlastní rozpoznatelný název, aby nebyl závislý na jiném uloženém snímku.

## Odstranění nahrazených testů

Následující testy patří ke starému rozhraní. Nové testy uvedené výše je nahrazují, proto tyto původní soubory **odstraňte** (Delete v IDE). Nemažte celý adresář testů:

- `app/src/test/java/cz/utb/ap7mt/counter/CounterViewModelTest.kt`

<!-- delete-file: app/src/test/java/cz/utb/ap7mt/counter/CounterViewModelTest.kt -->

Pokud starý test ponecháte, může volat konstruktor nebo metody, které už neexistují, a blokovat testovací sestavení.

## Ověření aplikace

Po dokončení všech kroků spusťte aplikaci přes Run. Vyzkoušejte tyto situace v uvedeném pořadí:

| Akce | Očekávaný výsledek |
| --- | --- |
| Spustit aplikaci poprvé | Zobrazí se prázdné počítadlo po dokončení načtení. |
| Uložit dva snímky a rozepsat další počítadlo | Historie i aktuální formulář mají očekávané hodnoty. |
| Po dokončení zápisu vynutit ukončení a znovu spustit | Obnoví se název, hodnota i historie. |
| Smazat snímek a aplikaci znovu spustit | Smazaný snímek se nevrátí. |
| Rychle několikrát stisknout +1 a psát název | Po ustálení zápisů souhlasí zobrazený i znovu načtený stav. |
| Použít název s uvozovkami a oddělovači | Název se po obnovení nezmění. |

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

Toto cvičení přidává i testy v `app/src/androidTest`. Spusťte emulátor nebo připojte testovací zařízení a proveďte:

```bash
./gradlew connectedDebugAndroidTest
```

Ve Windows použijte `.\gradlew.bat connectedDebugAndroidTest`. Tyto testy používají Android: v CV5 skutečnou obrazovku, v CV7 také Room databázi. Zelené lokální testy nejsou náhradou za jejich spuštění.

### Jak skutečně ověřit nové spuštění

Při testu obnovy nestačí otočit displej ani pouze přejít na domovskou obrazovku. V nastavení Androidu otevřete informace o aplikaci CounterApp, použijte Vynutit ukončení a potom aplikaci znovu spusťte. Nepoužívejte „Vymazat úložiště“ ani odinstalaci: tím byste úmyslně odstranili data, která chcete obnovit.

Tento milník používá drobnou historii v jednom JSON. Pro databázové dotazy a větší kolekce si v CV7 vyzkoušíme Room.

## Nejčastější problémy

| Problém | Kontrola nebo oprava |
| --- | --- |
| Serializer for class is not found | Přidejte serializační plugin a @Serializable k oběma modelům; proveďte Sync. |
| Cannot create an instance of CounterViewModel | Použijte factory, která předá CounterRepository. |
| ClassCastException na CounterApplication | Zkontrolujte android:name v manifestu a package třídy. |
| Chyba o více DataStore pro soubor | Nevytvářejte další instance v aktivitě ani v repository při každém volání. |
| Starý test nejde přeložit | Odstraňte nahrazený CounterViewModelTest podle pokynů. |
| Poškozená data se nenačtou | Chyba je úmyslně viditelná; neřešte ji automatickým přepsáním prázdným stavem. |

## Samostatná práce

Přidejte akci „Vymazat celou historii“ s potvrzením. Musí zachovat rozepsaný název a hodnotu. Doplňte test právě tohoto rozdílu oproti resetu celého stavu.

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
git commit -m "Dokonci CV5: Trvalé uložení počítadla pomocí DataStore"
git status
```

Před `git add` ověřte, že změny patří k tomuto cvičení. Příkaz zahrnuje také odstraněné testy v `app`; adresáře build zůstávají ignorované. Referenční změny lze prohlédnout bez přepnutí větve:

```bash
git diff origin/first_counter_viewmodel_tabs..origin/first_counter_persistance -- app gradle
```

Případné rozšíření ze samostatné práce uložte do dalšího commitu, aby bylo zřejmé, co tvoří základní řešení.

## Dokumentace

- [DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
- [Korutiny v Kotlinu](https://kotlinlang.org/docs/coroutines-basics.html)
