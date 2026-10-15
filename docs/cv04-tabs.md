# CV4 – Samostatná záložka historie

## Cíl a výchozí stav

Navazujete na `first_counter_viewmodel_list`, cílové řešení je `first_counter_viewmodel_tabs`. Ověřte funkčnost předchozího milníku a čistý `git status`. Postupujte ve vlastní větvi:

```bash
git switch first_counter_viewmodel_list
git switch -c student/cv04
```

## Soubory

Kotlin soubory jsou v `app/src/main/java/cz/utb/ap7mt/counter/`: CounterApp.kt, CounterScreen.kt, HistoryScreen.kt. Závislosti najdete v `app/build.gradle.kts` a `gradle/libs.versions.toml`.

## Postup

1. Vytvořte CounterApp s titulkem a dvěma Material 3 záložkami: Počítadlo a Historie.

2. Vybranou záložku uložte pomocí rememberSaveable. Při přepnutí vykreslete příslušnou obrazovku.

3. ViewModel získávejte nad přepínáním záložek. Obě části tak používají jedinou instanci.

4. CounterScreen dostává formulář a callbacky, HistoryScreen pouze historii a callback pro odstranění.

5. Odstraňte historii z původního formuláře. Navigační knihovna pro dvě prosté záložky není potřebná.

## Co si vysvětlit

ViewModel uvnitř různě scopeovaných destinací by mohl být jiný. Zde jej vlastní společná aktivita.

## Ověření

Rozepište počítadlo, přepněte tam a zpět, uložte snímek a zobrazte jej v Historii. Otočení zachová data i vybraný tab.

```bash
./gradlew assembleDebug testDebugUnitTest lintDebug
```

Ve Windows použijte `.\gradlew.bat` místo `./gradlew`. Přesné připravené řešení porovnejte pomocí `git diff first_counter_viewmodel_list..first_counter_viewmodel_tabs -- app/src/main`.

## Samostatný úkol

Přidejte do titulku Historie počet uložených snímků.

## Kontrolní seznam a Git

- [ ] Rozumím vlastníkovi stavu a směru událostí.
- [ ] Prošel jsem ruční scénáře a sestavení.
- [ ] Změny obsahují jen soubory tohoto cvičení, bez local.properties a build výstupů.

```bash
git status
git diff
git add app gradle build.gradle.kts
git commit -m "Dokonci CV4: Samostatná záložka historie"
```

Pokud projekt nesestavíte, nejprve ověřte Gradle JDK a SDK podle README. Chybějící importy doplňte podle typu, ne podle shody názvu z jiné knihovny.
