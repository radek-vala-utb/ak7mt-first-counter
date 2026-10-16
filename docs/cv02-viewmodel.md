# CV2 – Stav počítadla ve ViewModelu

V CV1 vlastní název i hodnotu funkce CounterScreen. Nyní přesuneme stav a jeho změny do CounterViewModelu. Obrazovka zůstane stejná, ale při otočení zařízení a znovuvytvoření aktivity se rozpracované počítadlo zachová.

## Cíl cvičení

- Rozdělit aplikaci na držitele stavu a zobrazovací funkci.
- Používat ViewModel získaný přes Compose `viewModel()`.
- Předávat do obrazovky hodnoty a obsluhy událostí (callbacky).
- Napsat jednoduchý test pravidel počítadla bez spouštění emulátoru.

## Příprava a výchozí stav

Pracujte v prvním repozitáři **ap7mt-counter**. Výchozí větev je **`first_counter`**, referenční výsledek **`first_counter_viewmodel`**. Otevřete kořen projektu v Android Studiu, dokončete Gradle Sync a nejprve spusťte výchozí aplikaci. Prostředí a spuštění popisuje [hlavní README](../README.md).

Pokud pokračujete ve vlastním funkčním řešení předchozího cvičení, založte novou větev přímo z něj. Následující příkazy jsou alternativa pro začátek z připravené referenční větve; spouštějte je v terminálu v kořeni správného repozitáře:

```bash
git status
git switch first_counter
git switch -c student/cv02
```

Před přepnutím musí být vaše změny uložené v commitu. Pokud pracovní větev už existuje, pokračujte příkazem `git switch student/cv02` bez `-c`. Při čerstvém klonování Git obvykle dohledá výchozí větev na origin; pokud ji nevidí, nejprve spusťte `git fetch origin`.

## Jak vkládat kód

Každý blok označený cestou k souboru níže obsahuje **celý výsledný soubor včetně importů**. Existující obsah nahraďte; u nového souboru jej vytvořte. Nevkládejte nové definice pod staré a nevytvářejte další bloky `plugins`, `dependencies` či další třídu se stejným názvem. Části kódu se odkazují jedna na druhou, proto může být rozpracovaný projekt mezi kroky dočasně označen chybami; kompletní sestavení proveďte až po propojení všech částí.

Kotlin soubory aplikace patří pod `app/src/main/java/cz/utb/ap7mt/counter/`. V Android Studiu použijte na příslušném balíčku **New → Kotlin Class/File** a zvolte soubor. Podbalíčky vytvářejte jako **Package**; řádek `package` v ukázce musí odpovídat umístění. Pokud IDE vygenerovalo prázdnou třídu, nahraďte ji celou. Značka `*` v některých importech zpřístupňuje veřejné názvy z daného balíčku; při kopírování zachovejte uvedené importy.

## Postup

1. Přidejte závislost pro ViewModel v Compose.
2. Přesuňte název, hodnotu a operace do nové třídy.
3. Vytvořte samostatný CounterScreen s parametry.
4. V aktivitě získejte ViewModel a propojte jej s obrazovkou.
5. Doplňte testy a ověřte změnu při otočení.

Následuje podrobná realizace. Po každé části si ověřte její význam; při červeném podtržení rozlišujte chybějící soubor z dalšího kroku a skutečný překlep.

### Krok 1 – Závislost pro ViewModel

V katalogu `gradle/libs.versions.toml` už je alias `androidx-lifecycle-viewmodel-compose`. Zde jej začneme používat. Otevřete **modulový** soubor `app/build.gradle.kts`, ne stejnojmenný soubor v kořeni.

**Nahraďte celý obsah souboru `app/build.gradle.kts`:**

<!-- source-file: app/build.gradle.kts -->
```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
```

Nový řádek je `implementation(libs.androidx.lifecycle.viewmodel.compose)`. Zbytek zachovává prostředí CV1. Alias s pomlčkami z TOML se v Kotlin DSL používá s tečkami. Po uložení spusťte Gradle Sync. Verze knihovny se bere z připraveného katalogu; další plugin není potřeba.

**Průběžná kontrola:** Gradle Sync dokončí načtení knihovny bez chyby.

### Krok 2 – Vlastník stavu CounterViewModel

V balíčku `cz.utb.ap7mt.counter` vytvořte soubor `CounterViewModel.kt`. Tato třída neobsahuje rozložení obrazovky.

**Vytvořte nový soubor `app/src/main/java/cz/utb/ap7mt/counter/CounterViewModel.kt`:**

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

    fun rename(value: String) { name = value }
    fun increment() { if (count < Int.MAX_VALUE) count++ }
    fun decrement() { if (count > 0) count-- }
    fun reset() { count = 0 }
}
```

`name` a `count` nyní patří ViewModelu. Zápis `private set` dovoluje ostatním částem stav číst, ale měnit jej lze pouze uvnitř této třídy. V UI proto nebudeme psát `model.count++`; zavoláme `model.increment()`.

Metody `rename`, `increment`, `decrement` a `reset` odpovídají událostem formuláře. Pravidla nuly a horní meze se přesunula z tlačítek sem. `reset()` stále zachovává název. `remember` zde není: životnost objektu nyní řídí ViewModel a jeho vlastník, nikoliv lokální zapamatování v Composable. Sledovatelnost hodnot zůstává zajištěna Compose State.

### Krok 3 – Obrazovka jako funkce s parametry

Původní `CounterScreen()` na konci `MainActivity.kt` odstraňte; za chvíli nahradíme i celý soubor aktivity. Vytvořte samostatný `CounterScreen.kt` s následujícím obsahem. Dvě definice stejné obrazovky současně nepotřebujeme.

**Vytvořte nový soubor `app/src/main/java/cz/utb/ap7mt/counter/CounterScreen.kt`:**

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
```

Obrazovka už nemá vlastní `var name` ani `var count`. Dostává aktuální hodnoty jako parametry a při akci zavolá předanou funkci.

| Parametr | Typ | Význam |
| --- | --- | --- |
| `name` | `String` | Aktuální název pro pole a nadpis. |
| `count` | `Int` | Aktuální hodnota pro velký text a dostupnost tlačítek. |
| `onNameChange` | `(String) -> Unit` | Funkce přijímající nový název; nic nevrací. |
| `onIncrement`, `onDecrement`, `onReset` | `() -> Unit` | Funkce bez parametrů; nic nevrací. |
| `modifier` | `Modifier` | Úpravy rozložení předané rodičem. |

`Unit` lze číst jako „bez výsledné hodnoty“. Callback je funkce, kterou obrazovce předá její volající. Díky tomu obrazovka nepotřebuje znát třídu ViewModelu. UI dál zakazuje nepovolené akce pomocí `enabled`; pravidla samotné změny hodnoty hlídá ViewModel.

### Krok 4 – Propojení v MainActivity

Nahraďte celý soubor aktivity. Tím zároveň odstraníte případný zbytek původní lokální definice obrazovky.

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize().safeDrawingPadding()) {
                    val model: CounterViewModel = viewModel()
                    CounterScreen(
                        model.name, model.count, model::rename,
                        model::increment, model::decrement, model::reset,
                        Modifier.verticalScroll(rememberScrollState()),
                    )
                }
            }
        }
    }
}
```

`val model: CounterViewModel = viewModel()` požádá o instanci spojenou s aktuálním vlastníkem; zde je to aktivita. Nepište v Composable `CounterViewModel()` ručně: obcházeli byste správu životnosti.

`model::rename` je **reference na metodu**. Předává funkci k pozdějšímu zavolání při psaní. Naproti tomu `model.rename("Text")` by provedlo změnu hned. Podobně předáváme `model::increment`, `model::decrement` a `model::reset`.

Tok jedné akce je `kliknutí → callback → metoda ViewModelu → nový stav → aktualizace UI`. `Modifier.verticalScroll(rememberScrollState())` zůstává odpovědností zobrazovací části, nikoliv ViewModelu.

**Průběžná kontrola:** Zadejte název, zvyšte hodnotu a otočte emulátor. Po znovuvytvoření aktivity se zobrazí stejné údaje.

### Krok 5 – Jednotkové testy pravidel

Vytvořte test v `app/src/test/java/cz/utb/ap7mt/counter/`. Pozor na rozdíl oproti složce `main`: test se nepřidává do aplikace.

**Vytvořte nový soubor `app/src/test/java/cz/utb/ap7mt/counter/CounterViewModelTest.kt`:**

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
}
```

`@Test` označuje testovací metodu. Každý test začíná novým ViewModelem, provede akce a porovná výsledek pomocí `assertEquals`. První test ověřuje nulovou mez a běžné zvýšení/snížení; druhý ověřuje, že reset ponechá název.

V testu je běžný konstruktor `CounterViewModel()` v pořádku: zkoumáme izolovaná pravidla, ne správu životního cyklu Androidu. Tyto testy samy neověřují otočení zařízení; to zůstává ruční kontrolou.

## Ověření aplikace

Po dokončení všech kroků spusťte aplikaci přes Run. Vyzkoušejte tyto situace v uvedeném pořadí:

| Akce | Očekávaný výsledek |
| --- | --- |
| Zadat název a několikrát zvýšit hodnotu | UI funguje jako v CV1. |
| Snížit až na nulu | Další snížení není možné. |
| Resetovat pojmenované počítadlo | Hodnota je 0, název zůstává. |
| Otočit zařízení se znovuvytvořením aktivity | Název a hodnota zůstanou. |
| Vynutit ukončení aplikace a spustit ji znovu | Stav může začít znovu: ViewModel není úložiště na disk. |

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

ViewModel řeší životnost stavu během změn konfigurace. Perzistenci po ukončení procesu přidáme až v CV5; prostý návrat z domovské obrazovky proces nutně neukončuje.

## Nejčastější problémy

| Problém | Kontrola nebo oprava |
| --- | --- |
| Unresolved reference: viewModel | Zkontrolujte závislost a import androidx.lifecycle.viewmodel.compose.viewModel; dokončete Sync. |
| Chyba při přiřazení do model.count | Setter je privátní. Volejte metodu increment/decrement/reset. |
| Po otočení se stav ztrácí | Použijte viewModel(), nikoliv CounterViewModel() v Composable. |
| Více verzí CounterScreen | Odstraňte původní funkci z MainActivity; ponechte samostatný soubor. |

## Samostatná práce

Doplňte test, který vytvoří dva ViewModely, změní první a ověří, že druhý zůstal ve výchozím stavu. Stav nesmí být uložený v globální proměnné.

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
git commit -m "Dokonci CV2: Stav počítadla ve ViewModelu"
git status
```

Před `git add` ověřte, že změny patří k tomuto cvičení. Příkaz zahrnuje také odstraněné testy v `app`; adresáře build zůstávají ignorované. Referenční změny lze prohlédnout bez přepnutí větve:

```bash
git diff origin/first_counter..origin/first_counter_viewmodel -- app gradle
```

Případné rozšíření ze samostatné práce uložte do dalšího commitu, aby bylo zřejmé, co tvoří základní řešení.

## Dokumentace

- [ViewModel – přehled](https://developer.android.com/topic/libraries/architecture/viewmodel)
