# CV1 – První počítadlo v Jetpack Compose

V tomto cvičení vytvoříte jednoduchou Android aplikaci **CounterApp**. Uživatel zadá název počítadla, zvýší nebo sníží jeho hodnotu a může ji vynulovat. Celé řešení vytvoříte v jednom souboru `MainActivity.kt`.

## Cíl cvičení

Po dokončení budete umět:

- najít místo, kde se v aplikaci vytváří Compose UI,
- napsat vlastní funkci označenou `@Composable`,
- uložit název a číselnou hodnotu do pozorovatelného stavu,
- sestavit formulář pomocí `Column`, `Row`, `Text` a `OutlinedTextField`,
- reagovat na zadávání textu a kliknutí na tlačítko,
- vysvětlit, proč se po změně stavu změní obrazovka,
- ověřit aplikaci na emulátoru a uložit řešení do Gitu.

Výchozí stav je ve větvi **`main`**, hotový referenční výsledek ve větvi **`first_counter`**. V CV1 ještě není ViewModel, historie ani ukládání na disk. Po znovuvytvoření aktivity se název a hodnota vrátí do výchozího stavu.

## 1. Příprava projektu

Použijte připravený repozitář; nový projekt v Android Studiu nevytvářejte. Požadované SDK, JDK a verze nástrojů najdete v [hlavním README](../README.md#prostředí-a-spuštění).

Pokud repozitář ještě nemáte, otevřete terminál ve složce, ve které chcete mít projekty, a spusťte:

```bash
git clone https://github.com/radek-vala-utb/ak7mt-first-counter.git
cd ak7mt-first-counter
```

Příkazy fungují také ve Windows PowerShellu. Pokud již repozitář máte, přejděte do jeho existující složky a znovu jej neklonujte.

Zkontrolujte stav a vytvořte vlastní pracovní větev:

```bash
git status
git switch main
git switch -c student/cv01
```

`git status` nesmí před přepnutím hlásit vaše neuložené změny. Pokud je hlásí, nejprve je dokončete a uložte do commitu. `git switch main` vybere startovací projekt; následující příkaz vytvoří větev pro vaše řešení. Pokud `student/cv01` již existuje a chcete pokračovat v práci, použijte pouze `git switch student/cv01`.

V Android Studiu zvolte **Open**, vyberte kořen repozitáře se souborem `settings.gradle.kts` a počkejte na dokončení Gradle Sync. Neotvírejte pouze podsložku `app`. Spusťte emulátor, vyberte konfiguraci `app` a tlačítkem **Run** spusťte připravený projekt. Zobrazí se text „AP7MT – připraveno pro první počítadlo“.

> Pokud už vidíte hotové počítadlo nebo záložky, pravděpodobně jste v některé z pozdějších větví. Pro realizaci tohoto návodu začněte na `main`.

## 2. Který soubor budeme upravovat

Otevřete:

```text
app/src/main/java/cz/utb/ap7mt/counter/MainActivity.kt
```

V zobrazení projektu **Android** může být soubor pod `app → kotlin+java → cz.utb.ap7mt.counter`. Balíček může být zobrazen jako jeden název místo několika složek. K souboru se dostanete také vyhledáním názvu `MainActivity` v Android Studiu.

Pro CV1 nemusíte měnit Gradle, přidávat knihovny ani vytvářet XML rozložení. Rozhraní zapisujeme v Kotlinu pomocí Compose. Řádek `package cz.utb.ap7mt.counter` ponechte na začátku souboru; importy patří pod něj, před třídu a funkce.

## Postup

1. V `MainActivity` ponechte `setContent`, `MaterialTheme` a `Surface`. Přidejte `CounterScreen` a zavolejte jej uvnitř `Surface`.
2. V Composable vytvořte `name` pomocí `remember { mutableStateOf("") }` a `count` pomocí `remember { mutableIntStateOf(0) }`. Importy `getValue` a `setValue` umožní delegaci `by`.
3. Použijte Column, OutlinedTextField, Text a tlačítka. TextField dostává `value = name` a změnu vrací přes `onValueChange`.
4. Implementujte +1, −1 a Reset. Snížení omezte nulou i v obsluze události. Přidání omezte maximem Int, aby nedošlo k přetečení.
5. Použijte safeDrawingPadding a vertikální scroll, aby byl formulář použitelný na malém displeji i s klávesnicí.

Následující části rozpracovávají těchto pět bodů. Postupujte v uvedeném pořadí. Když je napsáno **nahraďte funkci**, odstraňte původní funkci včetně jejího těla a vložte novou. Nevytvářejte více funkcí `CounterScreen()` se stejnými parametry. Kompletní výsledný soubor je uveden za krokem 5.

### Krok 1 – Vlastní obrazovka CounterScreen

Nejprve oddělíme obsah obrazovky od třídy aktivity. **Nahraďte celý obsah `MainActivity.kt`** následujícím kódem. Importy už zahrnují i komponenty pro další kroky; některé proto mohou být zatím šedé jako nepoužité. To není chyba sestavení.

```kotlin
package cz.utb.ap7mt.counter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    CounterScreen()
                }
            }
        }
    }
}

@Composable
fun CounterScreen() {
    Text(text = "Zde bude počítadlo")
}
```

Co jednotlivé části znamenají:

| Část | Úloha |
| --- | --- |
| `MainActivity : ComponentActivity()` | Aktivita, do které Android vstoupí při otevření aplikace. |
| `onCreate()` | Místo pro nastavení obsahu při vytvoření aktivity. |
| `setContent { ... }` | Uvnitř vytváříme rozhraní pomocí Compose. |
| `MaterialTheme { ... }` | Zpřístupňuje barvy, typografii a další nastavení vzhledu. |
| `Surface { ... }` | Plocha, na které zobrazujeme obsah. |
| `Modifier.fillMaxSize()` | Rozšíří plochu do dostupné šířky i výšky. |
| `@Composable` | Označuje funkci, kterou lze použít při sestavování Compose UI. |
| `CounterScreen()` | Naše vlastní funkce s obsahem obrazovky. |

Funkce `CounterScreen` je **za poslední složenou závorkou třídy `MainActivity`**, tedy mimo třídu. Z aktivity ji voláme uvnitř `Surface`. Při dalších krocích budeme měnit především tuto funkci.

**Průběžná kontrola:** aplikaci spusťte přes Run. Musí zobrazit „Zde bude počítadlo“. Tím jste ověřili, že se nová funkce skutečně používá.

### Krok 2 – Název a hodnota jako stav

**Nahraďte pouze funkci `CounterScreen()`** následující verzí. Třídu `MainActivity` a importy ponechte:

```kotlin
@Composable
fun CounterScreen() {
    var name by remember { mutableStateOf("") }
    var count by remember { mutableIntStateOf(0) }

    Text(text = "Aktuální hodnota: $count")
}
```

`name` je textový název počítadla a začíná prázdným řetězcem `""`. `count` je celé číslo a začíná nulou.

- `var` dovoluje hodnotu měnit.
- `mutableStateOf` a `mutableIntStateOf` vytvářejí stav, jehož změny Compose sleduje.
- `remember` zachová tento stav mezi opakovanými vyhodnoceními stejné části rozhraní.
- `by` umožňuje číst a zapisovat `count` přímo, místo práce s `count.value`.

Pro zápis s `by` potřebujeme tyto importy, které už obsahuje kód z kroku 1:

```kotlin
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
```

Když později změníme hodnotu, Compose aktualizuje části UI, které ji používají. Tomuto opětovnému vyhodnocení se říká **rekompozice**. Podrobnosti jsou v [dokumentaci stavu Compose](https://developer.android.com/develop/ui/compose/state).

Samotné `var count = 0` by nevytvořilo pozorovatelný Compose stav. Pro tento příklad proto použijte celý uvedený zápis včetně `remember`.

**Průběžná kontrola:** po spuštění vidíte „Aktuální hodnota: 0“. Hodnotu zatím nelze změnit; tlačítka přidáme v kroku 4.

### Krok 3 – Formulář a zobrazení hodnoty

Nyní přidáme nadpis, pole pro název a velké číslo. **Nahraďte opět celou funkci `CounterScreen()`**:

```kotlin
@Composable
fun CounterScreen() {
    var name by remember { mutableStateOf("") }
    var count by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Počítadlo",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Název počítadla") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = name.ifBlank { "Moje počítadlo" },
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = count.toString(),
            style = MaterialTheme.typography.displayLarge
        )
    }
}
```

`Column` umisťuje prvky pod sebe v pořadí, v jakém jsou zapsané. `padding(24.dp)` přidává vnitřní odstup a `Arrangement.spacedBy(16.dp)` mezery mezi prvky. Jednotka `dp` vyjadřuje rozměr nezávislý na hustotě pixelů displeje.

`OutlinedTextField` je ohraničené vstupní pole. Důležité je propojení:

```kotlin
value = name,
onValueChange = { name = it },
```

`value` určuje právě zobrazený text. `onValueChange` je obsluha změny: když uživatel píše, dostane nový text jako parametr `it` a uloží jej do `name`. Složené závorky zde obsahují **lambdu**, tedy krátký blok kódu předaný jako funkci. Stejný zápis lze s pojmenovaným parametrem zapsat takto:

```kotlin
onValueChange = { newName ->
    name = newName
},
```

Použijte jednu z těchto variant, ne obě současně. Pole tak ukazuje stav a událost zadávání tento stav mění. Nadpis pod polem čte stejnou proměnnou, takže se při psaní také aktualizuje.

Další části formuláře:

| Zápis | Význam |
| --- | --- |
| `label = { Text("Název počítadla") }` | Popisek vstupního pole. |
| `singleLine = true` | Název zadáváme na jednom řádku. |
| `Modifier.fillMaxWidth()` | Pole využije dostupnou šířku. |
| `name.ifBlank { "Moje počítadlo" }` | Pokud je název prázdný nebo obsahuje jen mezery, zobrazí náhradní nadpis. Samotný `name` tím nemění. |
| `count.toString()` | Převede číslo na text pro komponentu `Text`. |
| `MaterialTheme.typography.displayLarge` | Vybere velký styl písma pro číselnou hodnotu. |

**Průběžná kontrola:** napište „Návštěvníci“. Stejný název se musí objevit pod polem. Po smazání názvu se opět ukáže „Moje počítadlo“. Číslo zůstává 0.

### Krok 4 – Tlačítka −1, +1 a Reset

Ve funkci `CounterScreen` najděte poslední `Text`, který zobrazuje `count.toString()`. **Za tento `Text`, stále uvnitř `Column`, vložte následující `Row`**. Dosavadní pole ani texty nemažte:

```kotlin
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    if (count > 0) {
                        count--
                    }
                },
                enabled = count > 0
            ) {
                Text("−1")
            }

            Button(
                onClick = {
                    if (count < Int.MAX_VALUE) {
                        count++
                    }
                },
                enabled = count < Int.MAX_VALUE
            ) {
                Text("+1")
            }

            OutlinedButton(
                onClick = { count = 0 },
                enabled = count > 0
            ) {
                Text("Reset")
            }
        }
```

`Row` umisťuje tlačítka vedle sebe; `spacedBy(8.dp)` mezi nimi vytvoří mezeru. `Button` používáme pro zvýšení a snížení, `OutlinedButton` pro vizuálně méně výrazný reset. Text uvnitř tlačítka je jeho popisek.

`onClick` určuje, co se stane po kliknutí. `count++` zvýší hodnotu o jedna, `count--` ji o jedna sníží a `count = 0` ji vynuluje. Změnu názvu zde neprovádíme, proto Reset zachovává pojmenování počítadla.

Parametr `enabled` řídí dostupnost tlačítka:

| Tlačítko | Kdy je dostupné | Co ještě kontroluje obsluha |
| --- | --- | --- |
| `−1` | `count > 0` | Stejná podmínka zabrání snížení pod nulu. |
| `+1` | `count < Int.MAX_VALUE` | Stejná podmínka zabrání přetečení čísla. |
| `Reset` | `count > 0` | Nastavení na nulu je vždy platná operace. |

Omezení v `enabled` dává uživateli zpětnou vazbu a podmínka v `onClick` výslovně vyjadřuje pravidlo změny hodnoty. `Int.MAX_VALUE` je **2 147 483 647**. Větší kladné číslo typ `Int` nepojme; bez kontroly by další zvýšení přeteklo do záporné hodnoty.

**Průběžná kontrola:** třikrát stiskněte `+1`, jednou `−1` a potom Reset. Postupně uvidíte 3, 2 a 0. Při nule jsou `−1` a Reset neaktivní, ale `+1` zůstává dostupné. Název se nemění.

### Krok 5 – Okraje obrazovky, posouvání a klávesnice

Na malé obrazovce nebo při otevřené klávesnici nemusí být na celý formulář dost místa. Dokončíme proto okraje a posouvání.

**A. V `MainActivity` nahraďte celý blok `Surface` tímto blokem:**

```kotlin
Surface(
    modifier = Modifier
        .fillMaxSize()
        .safeDrawingPadding()
) {
    CounterScreen()
}
```

`safeDrawingPadding()` zohlední bezpečnou oblast pro obsah, například systémové lišty a výřez displeje. Pevný odstup `24.dp` má jiný účel: vytváří prostor mezi formulářem a hranou jeho dostupné plochy. Další vysvětlení nabízí [dokumentace o okrajích okna](https://developer.android.com/develop/ui/compose/system/insets-ui).

**B. Ve funkci `CounterScreen` vložte za deklarace `name` a `count` tento řádek:**

```kotlin
val modifier = Modifier.verticalScroll(rememberScrollState())
```

**C. V existujícím `Column` změňte pouze parametr `modifier`:**

```kotlin
modifier = modifier.padding(24.dp),
```

Začátek funkce teď bude vypadat takto; obsah `Column` z předchozích kroků za ním ponechte:

```kotlin
@Composable
fun CounterScreen() {
    var name by remember { mutableStateOf("") }
    var count by remember { mutableIntStateOf(0) }
    val modifier = Modifier.verticalScroll(rememberScrollState())

    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Zde ponechte Text, OutlinedTextField, další texty a Row s tlačítky.
    }
}
```

Tento blok ukazuje umístění změn; komentář nenahrazuje skutečný obsah formuláře. Pokud si nejste jistí složenými závorkami, použijte kompletní soubor níže.

`verticalScroll` umožňuje svisle posouvat obsah. `rememberScrollState()` si pamatuje aktuální pozici posunu během práce s obrazovkou. V zápisu `modifier = modifier.padding(24.dp)` je vlevo název parametru `Column`, vpravo naše proměnná vytvořená o několik řádků výše.

V připraveném `AndroidManifest.xml` už má aktivita nastavené:

```xml
android:windowSoftInputMode="adjustResize"
```

Atribut nevkládejte znovu. Spolu s úpravou prostoru pro obsah a posouváním umožňuje vyzkoušet formulář s otevřenou softwarovou klávesnicí. Pokud se při použití klávesnice počítače v emulátoru klávesnice na displeji neukáže, zapněte v nastavení Androidu zobrazování softwarové klávesnice i při připojené fyzické klávesnici.

**Průběžná kontrola:** otočte emulátor na šířku, klepněte do pole a zobrazte klávesnici. Podle potřeby posuňte formulář nahoru; musí být možné dostat se k jeho tlačítkům. Zkontrolujte také, že text není pod systémovými lištami.

## Kompletní výsledný MainActivity.kt

Po dokončení pěti kroků může mít celý soubor následující podobu. **Tímto blokem lze nahradit celý `MainActivity.kt`**, včetně importů. Nepřidávejte jej za stávající třídu nebo funkci. Chování odpovídá referenční větvi `first_counter`; ukázka používá rozepsané importy a více řádků pro snazší čtení.

```kotlin
package cz.utb.ap7mt.counter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding()
                ) {
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

    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Počítadlo",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Název počítadla") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = name.ifBlank { "Moje počítadlo" },
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = count.toString(),
            style = MaterialTheme.typography.displayLarge
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    if (count > 0) {
                        count--
                    }
                },
                enabled = count > 0
            ) {
                Text("−1")
            }

            Button(
                onClick = {
                    if (count < Int.MAX_VALUE) {
                        count++
                    }
                },
                enabled = count < Int.MAX_VALUE
            ) {
                Text("+1")
            }

            OutlinedButton(
                onClick = { count = 0 },
                enabled = count > 0
            ) {
                Text("Reset")
            }
        }
    }
}
```

## Rekompozice versus nové vytvoření aktivity

Vyzkoušejte dva různé děje:

1. Zadejte název a několikrát klikněte na `+1`. Obrazovka se aktualizuje, ale název ani předchozí hodnota se neztratí.
2. Otočte zařízení tak, aby Android znovu vytvořil aktivitu. V tomto projektu se lokální stav vrátí na prázdný název a nulu.

První děj je aktualizace UI se zachováním stavu přes `remember`. Druhý vytváří aktivitu a její obsah znovu. V CV1 je tato ztráta očekávaná; stav zde neukládáme na disk. Samotný odchod na domovskou obrazovku nemusí aktivitu zničit, proto není spolehlivým testem nového vytvoření.

`rememberSaveable` je další možnost pro malý obnovitelný stav UI. V tomto cvičení ale ponechte `remember`, aby byl patrný výchozí způsob práce. V [CV2](cv02-viewmodel.md) přesuneme stav do ViewModelu.

## Spuštění a ověření výsledku

Změny uložte a spusťte aplikaci tlačítkem Run. Pro začátek nespoléhejte pouze na náhled editoru; následující situace vyzkoušejte v běžící aplikaci:

| Akce | Očekávaný výsledek |
| --- | --- |
| Nové vytvoření aktivity | Prázdné pole, „Moje počítadlo“, hodnota 0. |
| Zadat „Návštěvníci“ | Pod polem se zobrazí stejný název. |
| Třikrát `+1` | Hodnota 3. |
| Jednou `−1` | Hodnota 2. |
| Reset | Hodnota 0, název „Návštěvníci“ zůstává. |
| Při nule zkusit `−1` | Tlačítko je neaktivní, hodnota se nezmění. |
| Smazat název nebo zadat pouze mezery | Zobrazí se náhradní nadpis „Moje počítadlo“. |
| Otevřít klávesnici na malé obrazovce | K obsahu se lze dostat posunutím formuláře. |
| Otočit zařízení se znovuvytvořením aktivity | Stav se vrátí na výchozí hodnoty. |

Horní mez není potřeba ověřovat miliardami kliknutí. Volitelně dočasně změňte počáteční hodnotu na `mutableIntStateOf(Int.MAX_VALUE - 1)`, aplikaci znovu spusťte a jednou stiskněte `+1`. Číslo má být 2 147 483 647 a `+1` se má zakázat. **Před odevzdáním vraťte počáteční hodnotu na 0.**

### Kontrola sestavení v terminálu

Příkazy spouštějte v kořeni repozitáře, kde leží `gradlew` a `gradlew.bat`, nikoliv ve složce se souborem `MainActivity.kt`.

macOS / Linux:

```bash
./gradlew assembleDebug testDebugUnitTest lintDebug
```

Windows PowerShell:

```powershell
.\gradlew.bat assembleDebug testDebugUnitTest lintDebug
```

`assembleDebug` sestaví aplikaci, `testDebugUnitTest` spustí případné lokální testy a `lintDebug` provede statickou kontrolu. V základní CV1 větvi nejsou připravené vlastní jednotkové testy, proto může testovací úloha hlásit `NO-SOURCE`. Úspěšné sestavení tedy nenahrazuje výše uvedené ruční ověření. Celý příkaz má skončit `BUILD SUCCESSFUL`.

## Nejčastější problémy

| Problém | Co zkontrolovat |
| --- | --- |
| `Unresolved reference: remember`, `dp` nebo jiný název | Importy na začátku souboru; porovnejte je s kompletním příkladem. |
| Chyba u `by`, `getValue` nebo `setValue` | Přítomnost obou importů z `androidx.compose.runtime`. |
| Chyba, že Composable lze volat pouze z Composable | `CounterScreen()` musí být uvnitř `setContent` a jeho funkce musí mít `@Composable`. |
| `Conflicting overloads` nebo dvojí `CounterScreen` | Při nahrazování jste ponechali starou funkci; v souboru má být právě jedna. |
| Do pole nelze psát | `value = name` a `onValueChange = { name = it }` musí používat stejný stav. |
| Po kliknutí se nic nemění | `count` musí být Compose stav a `onClick` musí měnit právě tuto proměnnou. |
| Stále vidím původní úvodní text | V `Surface` odstraňte původní `Text` a zavolejte `CounterScreen()`. |
| Android Studio nabízí nesprávný Button/Text | Použijte importy z `androidx.compose.material3`, nikoliv běžné Android View komponenty. |
| Po otočení zmizí název a hodnota | U této implementace s `remember` je to očekávané. |
| Gradle nenajde JDK nebo SDK | Postupujte podle [nastavení prostředí v README](../README.md#prostředí-a-spuštění); neupravujte kvůli tomu Kotlin kód. |

## Samostatný úkol

Přidejte tlačítko `+10`. Musí zvýšit hodnotu o deset pouze tehdy, pokud se výsledek vejde do typu `Int`. Nestačí ověřit, že aktuální hodnota je menší než `Int.MAX_VALUE` — zamyslete se nad posledními devíti hodnotami před maximem. Pravidlo použijte v `enabled` i v `onClick`.

Pokud se po přidání tlačítko nevejde do původního řádku, umístěte jej na samostatný řádek pod stávající `Row`. Bonus nesmí znepřístupnit základní tlačítka na úzkém displeji.

## Kontrolní seznam a uložení do Gitu

- [ ] Pracuji ve své větvi `student/cv01` založené z `main`.
- [ ] V souboru je jediná `MainActivity` a jediná funkce `CounterScreen`.
- [ ] Rozumím proměnným `name`, `count`, pozorovatelnému stavu a `remember`.
- [ ] Název lze měnit a ihned se zobrazuje pod vstupním polem.
- [ ] `+1`, `−1` a Reset odpovídají pravidlům; hodnota neklesá pod nulu ani nepřetéká.
- [ ] Reset nemění název a výchozí hodnota je 0.
- [ ] Formulář jsem vyzkoušel s klávesnicí a na menší dostupné ploše.
- [ ] Dokážu vysvětlit, proč se při rekompozici stav zachová a při novém vytvoření aktivity ztratí.
- [ ] Sestavení a lint skončily bez chyb.

Prohlédněte změny a uložte upravený soubor:

```bash
git status
git diff -- app/src/main/java/cz/utb/ap7mt/counter/MainActivity.kt
git add app/src/main/java/cz/utb/ap7mt/counter/MainActivity.kt
git commit -m "Dokonci CV1: prvni pocitadlo v Compose"
git status
```

Nepřidávejte `local.properties`, obsah `build` ani osobní nastavení IDE. Pro tento postup stačí změna `MainActivity.kt`. Poslední `git status` by měl po uložení všech zamýšlených změn hlásit čistý pracovní strom.

Referenční řešení lze prohlížet i bez přepínání z vlastní práce:

```bash
git show origin/first_counter:app/src/main/java/cz/utb/ap7mt/counter/MainActivity.kt
```

Pokud danou vzdálenou větev ještě lokálně nevidíte, načtěte informace příkazem `git fetch origin`. Srovnávejte především chování a význam kódu; odlišné zalomení řádků či rozepsání importů není funkční chyba.
