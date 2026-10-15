# AP7MT – CounterApp

Výukový Android projekt pro prezenční cvičení. Každá větev je samostatně spustitelný milník. Návody popisují přechod z předchozího řešení; cílová větev obsahuje referenční výsledek.

| Cvičení | Výchozí větev | Referenční výsledek |
| --- | --- | --- |
| [CV1: Compose a stav](docs/cv01-first-counter.md) | `main` | `first_counter` |
| [CV2: ViewModel](docs/cv02-viewmodel.md) | `first_counter` | `first_counter_viewmodel` |
| [CV3: Historie](docs/cv03-history.md) | `first_counter_viewmodel` | `first_counter_viewmodel_list` |
| [CV4: Záložky](docs/cv04-tabs.md) | `first_counter_viewmodel_list` | `first_counter_viewmodel_tabs` |
| [CV5: DataStore](docs/cv05-datastore.md) | `first_counter_viewmodel_tabs` | `first_counter_persistance` |

Název `persistance` je záměrně zachovaný podle zadání.

## Prostředí a spuštění

Android Studio podporující AGP 9.4.1, Android SDK 37, JDK 25 (Embedded JDK v Android Studiu), emulátor nebo zařízení s Androidem 8/API 26 a vyšším. Gradle 9.6.0 se stáhne přes přiložený Wrapper. Verze knihoven jsou v `gradle/libs.versions.toml`. AGP používá vestavěný Kotlin.

1. Otevřete kořen projektu v Android Studiu a nechte proběhnout Gradle Sync.
2. SDK Managerem nainstalujte požadovanou platformu, v nastavení Gradle zvolte Embedded JDK.
3. Vyberte výchozí větev cvičení a založte vlastní větev, například:

```bash
git switch first_counter
git switch -c student/viewmodel
```

4. V Device Manageru spusťte emulátor, vyberte konfiguraci `app` a Run.

Android Studio vytvoří neversionovaný `local.properties` s cestou k SDK. Do repozitáře jej nepřidávejte. Pro terminál nastavte `JAVA_HOME` na JDK; macOS s výchozí instalací Android Studia:

```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew assembleDebug testDebugUnitTest lintDebug
```

Windows PowerShell, po nastavení JDK a SDK:

```powershell
.\gradlew.bat assembleDebug testDebugUnitTest lintDebug
```

Instrumentační testy se spouštějí s připojeným emulátorem příkazem `./gradlew connectedDebugAndroidTest` (Windows `.\gradlew.bat connectedDebugAndroidTest`).

## Práce s referenčními řešeními

Vlastní změny ukládejte do větví `student/...`; před přepnutím zkontrolujte `git status` a změny commitněte. Rozdíl milníků lze prohlédnout například `git diff first_counter..first_counter_viewmodel`. Všechny návody čtěte od začátku. Hotové řešení je opora pro kontrolu, nikoliv náhrada samostatné práce.

Pravidla: hodnota neklesá pod nulu, Reset mění pouze hodnotu, do historie se ukládá pojmenovaný snímek a následně se formulář vyprázdní. Historie se řadí od nejnovějšího. Poslední větev uchovává maximálně 100 posledních záznamů.

## Zdroje

- [Compose State](https://developer.android.com/develop/ui/compose/state)
- [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)
- [DataStore](https://developer.android.com/topic/libraries/architecture/datastore)

Metodická návaznost: AP5PM CV2–CV4. Další repozitář `ap7mt-counter-holidays` přidává REST API a Room.
