# AP7MT – CounterApp

Výukový projekt pro první kroky v Jetpack Compose. Z jedné obrazovky postupně vytvoříme aplikaci s ViewModelem, historií, záložkami a trvalým uložením. Celý postup je určený pro práci ve cvičení i samostatné dokončení doma.

## Naklonování

V terminálu přejděte do složky pro své projekty a spusťte (stejně na macOS, Linuxu i ve Windows PowerShellu):

```bash
git clone https://github.com/radek-vala-utb/ak7mt-first-counter.git
cd ak7mt-first-counter
git switch main
git switch -c student/cv01
```

Pokud už projekt máte, znovu jej neklonujte. Přejděte do jeho existující složky a pokračujte podle příslušného návodu. Lokální adresář autora může být pojmenovaný `ap7mt-counter`; URL tohoto repozitáře je `ak7mt-first-counter`.

## Přehled cvičení

| Návod | Výchozí větev | Referenční řešení | Co přibude |
| --- | --- | --- | --- |
| [CV1 – Compose a stav](docs/cv01-first-counter.md) | `main` | `first_counter` | Název, hodnota, +1, −1 a Reset. |
| [CV2 – ViewModel](docs/cv02-viewmodel.md) | `first_counter` | `first_counter_viewmodel` | Stav mimo Composable a testy pravidel. |
| [CV3 – Historie](docs/cv03-history.md) | `first_counter_viewmodel` | `first_counter_viewmodel_list` | Nezávislé snímky a seznam pod formulářem. |
| [CV4 – Záložky](docs/cv04-tabs.md) | `first_counter_viewmodel_list` | `first_counter_viewmodel_tabs` | Samostatná Historie se sdílenými daty. |
| [CV5 – DataStore](docs/cv05-datastore.md) | `first_counter_viewmodel_tabs` | `first_counter_persistance` | Obnova formuláře i historie po novém spuštění. |

`main` obsahuje pouze startovací aplikaci s úvodním textem. Pro CV1 začněte od něj. Název `first_counter_persistance` zachovává původní zadání; při přepínání jej pište přesně takto.

## Jak číst návody a pracovat s kódem

Každý návod obsahuje cíl, výchozí a cílovou větev, stručný přehled a podrobnou realizaci s kódem. Bloky označené cestou k souboru představují celý obsah k vložení. Existující soubor nahraďte; nový vytvořte ve správném balíčku. Nevkládejte novou třídu pod starou ani nespojujte dvě definice téže funkce.

Výchozí větev obsahuje stav před cvičením, cílová referenční větev hotové řešení. Pro vlastní práci si založte `student/...`. Pokud pokračujete ve svém funkčním řešení, další větev založte z něj. Přepnutí na referenční větev je alternativa, když potřebujete známý výchozí bod.

Před přepínáním vždy spusťte `git status`. Rozpracované změny nejprve dokončete a commitněte. Existující studentskou větev otevřete přes `git switch student/...`; `-c` použijte pouze při jejím prvním vytvoření.

## Prostředí a spuštění

Projekt používá **AGP 9.4.1, Gradle 9.6.0, Android SDK 37 a JDK 25**. Verze knihoven jsou zapsané v `gradle/libs.versions.toml`; pro cvičení je neměňte podle náhodného staršího návodu. AGP používá vestavěnou podporu Kotlinu. Aplikace potřebuje Android 8/API 26 nebo novější.

1. Nainstalujte Git a Android Studio podporující uvedený AGP.
2. V Android Studiu zvolte **Open** a otevřete kořen naklonované složky, kde leží `settings.gradle.kts`. Neotvírejte pouze `app`.
3. V **SDK Manageru** nainstalujte požadovanou platformu Android SDK 37 a doporučené nástroje, pokud chybějí.
4. V nastavení projektu pro Gradle vyberte **Gradle JDK** odpovídající JDK 25. Zde byl projekt ověřen s Embedded JDK dodaným Android Studiem; verzi zkontrolujte, pokud máte jinou instalaci.
5. Počkejte na **Gradle Sync**. Při prvním spuštění je potřeba připojení pro stažení Wrapperu a závislostí.
6. V **Device Manageru** vytvořte a spusťte emulátor, případně připojte vlastní testovací zařízení s povoleným USB debugging. Pro shodu s ověřeným prostředím lze použít Pixel 8 / API 37.
7. Na horní liště vyberte konfiguraci **app**, cílové zařízení a **Run**. Android Studio aplikaci sestaví, nainstaluje a otevře.

Android Studio vytvoří místní `local.properties` s cestou k SDK. Tento soubor, adresáře `build`, `.gradle` a osobní nastavení IDE nejsou určeny ke sdílení. V repozitáři je pro ně připravený `.gitignore`.

## Orientace v souborech

```text
README.md                         rozcestník a spuštění
app/build.gradle.kts              konfigurace Android modulu a jeho závislosti
app/src/main/AndroidManifest.xml  registrace aplikace, aktivity a oprávnění
app/src/main/java/                Kotlin kód aplikace
app/src/main/res/                 Android zdroje
app/src/test/                     lokální testy na počítači
app/src/androidTest/              testy vyžadující Android zařízení nebo emulátor
docs/                            návody jednotlivých cvičení
gradle/libs.versions.toml         katalog verzí a aliasů knihoven
gradlew, gradlew.bat              spouštění přiloženého Gradle Wrapperu
```

Kotlin balíček aplikace je `cz.utb.ap7mt.counter`. V některých větvích je kód přímo v něm, v dalších přibývají podbalíčky. Ne všechny soubory uvedené v pozdějším návodu existují už na `main`; vznikají právě během cvičení.

## Sestavení a testy v terminálu

Otevřete terminál v kořeni repozitáře. Používejte přiložený Wrapper, ne libovolnou globální instalaci Gradle.

macOS s výchozí instalací Android Studia:

```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew assembleDebug testDebugUnitTest lintDebug
```

Linux nebo jiná instalace: nastavte `JAVA_HOME` na svou JDK 25 a použijte stejný příkaz `./gradlew`. Na Windows PowerShellu nastavte `JAVA_HOME` na skutečnou složku JDK a spusťte:

```powershell
.\gradlew.bat assembleDebug testDebugUnitTest lintDebug
```

| Úloha | Co ověřuje | Potřebuje emulátor? |
| --- | --- | --- |
| `assembleDebug` | Překlad a sestavení debug aplikace. | Ne |
| `testDebugUnitTest` | Lokální testy pravidel a pomocných tříd. | Ne |
| `lintDebug` | Statickou analýzu Android projektu. | Ne |
| `connectedDebugAndroidTest` | Instrumentační testy UI a případně databáze. | Ano |

Očekávaný konec je `BUILD SUCCESSFUL`. V časných větvích nemusí ještě být vlastní testy; `NO-SOURCE` není potvrzení otestování uživatelského chování. Proto má každý návod také ruční scénáře.

Pokud dané cvičení obsahuje instrumentační testy, se spuštěným emulátorem použijte:

```bash
./gradlew connectedDebugAndroidTest
```

Ve Windows použijte `.\gradlew.bat connectedDebugAndroidTest`. Lokální report najdete v `app/build/reports/tests/testDebugUnitTest/index.html`; lint v `app/build/reports/lint-results-debug.html`. Debug APK vzniká v `app/build/outputs/apk/debug/app-debug.apk`.

## Uložení práce a porovnání

Po dokončení kroku zkontrolujte `git diff`, přidejte pouze soubory popsané v návodu a vytvořte commit. Nepřidávejte soubory s místními cestami nebo build výstupy. Samostatné úkoly ukládejte odděleně, aby základní řešení šlo snadno rozpoznat.

Pokud Git při commitu nezná vaše jméno nebo e-mail, nastavte je pro tento repozitář pomocí `git config user.name` a `git config user.email` se svými skutečnými údaji. Nezadávejte do příkazů heslo ani přístupový token.

## Časté potíže při spuštění

| Problém | Co zkontrolovat |
| --- | --- |
| Projekt se neotevře jako Android projekt | Otevřete složku se settings.gradle.kts, nikoliv jen app/src. |
| Gradle hlásí chybějící nebo nepodporovanou Javu | Gradle JDK v IDE a JAVA_HOME v terminálu jsou dvě oddělená nastavení; obě musí ukazovat na vhodné JDK. |
| SDK location not found | Nainstalujte SDK a nechte IDE vytvořit místní local.properties. Nekopírujte cestu ze spolužákova počítače. |
| Unresolved reference po přidání knihovny | Proveďte Gradle Sync a zkontrolujte import i modulovou závislost. |
| Git nezná výukovou větev | Ověřte správný repozitář a spusťte git fetch origin. |
| Přepnutí větve blokují změny | Zkontrolujte git status a svou práci uložte do commitu. |
| Testy zařízení hlásí no connected devices | Spusťte emulátor a ověřte, že je vidět v Android Studiu. |

## Pravidla počítadla

Hodnota začíná na nule, neklesá pod ni a nesmí přetéct přes Int.MAX_VALUE. Reset nastaví jen hodnotu; název zachovává. Od CV3 lze uložit pojmenovaný snímek do historie. Uložení formulář vyprázdní, historie se řadí od nejnovějšího. Poslední větev uchovává nejvýše 100 posledních snímků.

Prohlédnutí rozdílu CV1 a CV2 bez přepnutí z vlastní práce:

```bash
git diff origin/first_counter..origin/first_counter_viewmodel -- app
```

## Další postup a zdroje

Po CV5 pokračujte v [druhém repozitáři se svátky](https://github.com/radek-vala-utb/ap7mt-counter-holidays), kde přidáme REST API a Room. Metodicky navazujeme na práci s CounterApp v AP5PM.

- [Compose State](https://developer.android.com/develop/ui/compose/state)
- [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)
- [DataStore](https://developer.android.com/topic/libraries/architecture/datastore)

## Ověření návodů

Kód vložený do návodů byl ověřen rekonstrukcí projektů z jednotlivých kroků. Výsledky: [ověření návodů CV2–CV7](docs/overeni-navodu.md).

Záznam předchozího ověření aplikace: [OVERENI.md](OVERENI.md).
