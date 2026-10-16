# CV4 – Samostatné záložky Počítadlo a Historie

V CV3 sdílí formulář a historie jednu obrazovku. Nyní je rozdělíme do dvou záložek. Datový model i operace ViewModelu zůstávají stejné; měníme uspořádání a propojení UI.

## Cíl cvičení

- Sestavit přepínání obsahu pomocí Material 3 záložek.
- Sdílet jednu instanci ViewModelu mezi oběma částmi.
- Odlišit stav aplikace od malé volby UI, jako je vybraný tab.

## Příprava a výchozí stav

Pracujte v prvním repozitáři **ap7mt-counter**. Výchozí větev je **`first_counter_viewmodel_list`**, referenční výsledek **`first_counter_viewmodel_tabs`**. Otevřete kořen projektu v Android Studiu, dokončete Gradle Sync a nejprve spusťte výchozí aplikaci. Prostředí a spuštění popisuje [hlavní README](../README.md).

Pokud pokračujete ve vlastním funkčním řešení předchozího cvičení, založte novou větev přímo z něj. Následující příkazy jsou alternativa pro začátek z připravené referenční větve; spouštějte je v terminálu v kořeni správného repozitáře:

```bash
git status
git switch first_counter_viewmodel_list
git switch -c student/cv04
```

Před přepnutím musí být vaše změny uložené v commitu. Pokud pracovní větev už existuje, pokračujte příkazem `git switch student/cv04` bez `-c`. Při čerstvém klonování Git obvykle dohledá výchozí větev na origin; pokud ji nevidí, nejprve spusťte `git fetch origin`.

## Jak vkládat kód

Každý blok označený cestou k souboru níže obsahuje **celý výsledný soubor včetně importů**. Existující obsah nahraďte; u nového souboru jej vytvořte. Nevkládejte nové definice pod staré a nevytvářejte další bloky `plugins`, `dependencies` či další třídu se stejným názvem. Části kódu se odkazují jedna na druhou, proto může být rozpracovaný projekt mezi kroky dočasně označen chybami; kompletní sestavení proveďte až po propojení všech částí.

Kotlin soubory aplikace patří pod `app/src/main/java/cz/utb/ap7mt/counter/`. V Android Studiu použijte na příslušném balíčku **New → Kotlin Class/File** a zvolte soubor. Podbalíčky vytvářejte jako **Package**; řádek `package` v ukázce musí odpovídat umístění. Pokud IDE vygenerovalo prázdnou třídu, nahraďte ji celou. Značka `*` v některých importech zpřístupňuje veřejné názvy z daného balíčku; při kopírování zachovejte uvedené importy.

## Postup

1. Vytvořte CounterApp, v něm získejte společný ViewModel a nastavte přepínání záložek.
2. Z aktivity zobrazte CounterApp a ověřte zachování stavu při přepnutí i otočení.

Následuje podrobná realizace. Po každé části si ověřte její význam; při červeném podtržení rozlišujte chybějící soubor z dalšího kroku a skutečný překlep.

### Krok 1 – Společný kořen obrazovek

Vytvořte nový soubor `CounterApp.kt`. Hotové `CounterScreen.kt`, `HistoryScreen.kt`, model historie a ViewModel ponechte beze změny.

**Vytvořte nový soubor `app/src/main/java/cz/utb/ap7mt/counter/CounterApp.kt`:**

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
```

`CounterApp` získává jeden ViewModel parametrem `model: CounterViewModel = viewModel()`. Volání leží nad podmínkou, která vybírá obsah. Obě záložky proto pracují s tou samou instancí.

`selectedTab` je index: 0 znamená Počítadlo, 1 Historii. `PrimaryTabRow` vykreslí záložkovou lištu a jednotlivé `Tab` nastaví vybraný index. `forEachIndexed` poskytuje index i název z pole popisků. Kliknutí pouze změní `selectedTab` a podmínka pod lištou zobrazí odpovídající Composable.

Volba záložky používá `rememberSaveable`, protože jde o malý stav UI vhodný k obnově. Název, hodnota a historie dál patří ViewModelu. Při odstranění formuláře z kompozice přepnutím na Historii proto nezmizí.

V samostatném tabu Počítadlo vracíme formuláři `verticalScroll`; zde již není uvnitř LazyColumn z CV3. Tab Historie používá svou vlastní `HistoryScreen`. V jednom okamžiku zobrazujeme pouze jednu část.

### Krok 2 – Jednodušší aktivita

Nahraďte aktivitu. Jejím úkolem je nyní vytvořit téma, plochu a zavolat CounterApp; nebude sestavovat seznam ani získávat vlastní druhý ViewModel.

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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize().safeDrawingPadding()) {
                    CounterApp()
                }
            }
        }
    }
}
```

V `Surface` je pouze `CounterApp()`. Původní `LazyColumn` s formulářem a historií už do aktivity nepatří. ViewModel získává CounterApp; pro tyto dvě záložky nepotřebujeme navigační knihovnu ani back stack.

Zkontrolujte, že `HistoryScreen` skutečně dostává `model.history`, nikoliv prázdný lokálně vytvořený seznam. `CounterScreen` i `HistoryScreen` se nadále dají chápat jako funkce, které dostávají data a callbacky.

**Průběžná kontrola:** Rozepište název a hodnotu, přepněte na Historii a zpět. Formulář musí zůstat rozepsaný. Uložte jej a ověřte snímek na druhé záložce.

## Ověření aplikace

Po dokončení všech kroků spusťte aplikaci přes Run. Vyzkoušejte tyto situace v uvedeném pořadí:

| Akce | Očekávaný výsledek |
| --- | --- |
| Rozpracovat počítadlo a přepnout tam a zpět | Název a hodnota zůstanou. |
| Uložit snímek a otevřít Historii | Snímek je ihned dostupný. |
| Smazat snímek v Historii | Seznam se aktualizuje. |
| Na záložce Historie otočit zařízení | Zůstane vybraná Historie a zachovají se snímky. |
| Vynutit ukončení procesu | Historie ještě není trvale uložená. |

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

V tomto kroku nepřidáváme ani neměníme jednotkové testy: pravidla ViewModelu jsou stejná jako v CV3. Existující testy spusťte znovu, nové chování záložek ověřte podle tabulky.

## Nejčastější problémy

| Problém | Kontrola nebo oprava |
| --- | --- |
| Přepnutím zmizí rozepsaný název | Stav nepatří do lokálního remember v CounterScreen; musí zůstat ve ViewModelu. |
| Každá záložka vidí jiná data | Předávejte jediný model získaný v CounterApp, nevytvářejte další vlastní instance. |
| Po otočení je vždy vybraná první záložka | selectedTab musí používat rememberSaveable. |
| Unresolved reference: PrimaryTabRow | Použijte Material 3 a závislosti připraveného projektu; neimportujte staré Material komponenty. |

## Samostatná práce

Zobrazte v popisku Historie počet snímků, například „Historie (3)“. Po uložení i smazání se údaj musí automaticky aktualizovat.

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
git commit -m "Dokonci CV4: Samostatné záložky Počítadlo a Historie"
git status
```

Před `git add` ověřte, že změny patří k tomuto cvičení. Příkaz zahrnuje také odstraněné testy v `app`; adresáře build zůstávají ignorované. Referenční změny lze prohlédnout bez přepnutí větve:

```bash
git diff origin/first_counter_viewmodel_list..origin/first_counter_viewmodel_tabs -- app gradle
```

Případné rozšíření ze samostatné práce uložte do dalšího commitu, aby bylo zřejmé, co tvoří základní řešení.

## Dokumentace

- [Compose – ukládání stavu UI](https://developer.android.com/develop/ui/compose/state-saving)
