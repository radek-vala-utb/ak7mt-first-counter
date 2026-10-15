# CV5 – Perzistence přes DataStore

## Cíl a výchozí stav

Navazujete na `first_counter_viewmodel_tabs`, cílové řešení je `first_counter_persistance`. Ověřte funkčnost předchozího milníku a čistý `git status`. Postupujte ve vlastní větvi:

```bash
git switch first_counter_viewmodel_tabs
git switch -c student/cv05
```

## Soubory

Kotlin soubory jsou v `app/src/main/java/cz/utb/ap7mt/counter/`: CounterRepository.kt, CounterState.kt, CounterViewModel.kt, CounterApplication.kt. Závislosti najdete v `app/build.gradle.kts` a `gradle/libs.versions.toml`.

## Postup

1. Přidejte Preferences DataStore a Kotlin serialization. Model celého stavu zahrne název, hodnotu a historii.

2. V CounterRepository vytvořte jediný DataStore pro soubor. Stav serializujte jako JSON; názvy mohou obsahovat uvozovky, | i nové řádky.

3. Změny provádějte uvnitř edit nad právě uloženým stavem. Uložení snímku a reset formuláře jsou jedna transakce. Historii omezte na 100 posledních položek.

4. Předávejte repository do ViewModelu přes factory. UI sbírá stav pomocí collectAsStateWithLifecycle. Nejprve ukažte načítání, nikoliv zapisovatelný prázdný formulář.

5. Po prvním načtení vlastní živý stav ViewModel: UI aktualizujte ihned, aby pomalý disk nevracel starší text do vstupního pole. Stejné operace zapisujte v pořadí přes Channel ve viewModelScope. Při chybě zápisu ukažte chybu, přerušte čekající operace a umožněte opětovné načtení. Poškozený JSON neignorujte ani nepřepisujte.

6. Přidejte test obnovy přes novou instanci DataStore po uzavření původní a test JSON s nestandardními znaky.

## Co si vysvětlit

DataStore zde uchovává malý celek. Rozsáhlá data a dotazy patří do Room. Channel zachovává pořadí změn a transakce brání ztrátě současných aktualizací.

## Ověření

Uložte dva snímky, rozepište třetí a ukončete proces. Po novém spuštění se obnoví formulář i historie; smazaný snímek se nevrátí. Vyzkoušejte rychlé klikání.

```bash
./gradlew assembleDebug testDebugUnitTest lintDebug
```

Ve Windows použijte `.\gradlew.bat` místo `./gradlew`. Přesné připravené řešení porovnejte pomocí `git diff first_counter_viewmodel_tabs..first_counter_persistance -- app/src/main`.

## Samostatný úkol

Přidejte potvrzené smazání celé historie; reset formuláře ponechte samostatný.

## Kontrolní seznam a Git

- [ ] Rozumím vlastníkovi stavu a směru událostí.
- [ ] Prošel jsem ruční scénáře a sestavení.
- [ ] Změny obsahují jen soubory tohoto cvičení, bez local.properties a build výstupů.

```bash
git status
git diff
git add app gradle build.gradle.kts
git commit -m "Dokonci CV5: Perzistence přes DataStore"
```

Pokud projekt nesestavíte, nejprve ověřte Gradle JDK a SDK podle README. Chybějící importy doplňte podle typu, ne podle shody názvu z jiné knihovny.
