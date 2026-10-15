# CV3 – Snímky a seznam historie

## Cíl a výchozí stav

Navazujete na `first_counter_viewmodel`, cílové řešení je `first_counter_viewmodel_list`. Ověřte funkčnost předchozího milníku a čistý `git status`. Postupujte ve vlastní větvi:

```bash
git switch first_counter_viewmodel
git switch -c student/cv03
```

## Soubory

Kotlin soubory jsou v `app/src/main/java/cz/utb/ap7mt/counter/`: CounterHistoryItem.kt, CounterViewModel.kt, HistoryScreen.kt, MainActivity.kt. Závislosti najdete v `app/build.gradle.kts` a `gradle/libs.versions.toml`.

## Postup

1. Vytvořte datovou třídu snímku: id, name, value, createdAt. Identita není název ani datum.

2. Ve ViewModelu přidejte pozorovatelný neměnný List. Při vložení a smazání vytvářejte nový seznam, neměňte obyčejný mutableListOf na místě.

3. Přidejte Uložit do historie. Po trim nesmí být název prázdný. Vytvořte UUID a časový údaj, vložte snímek na začátek a vyprázdněte formulář.

4. Pod formulář přidejte seznam a prázdný stav. LazyColumn vykresluje snímky s key = id; formulář patří do její úvodní položky.

5. Mazání předává ID do ViewModelu. Změna živého počítadla nesmí měnit uložené snímky.

## Co si vysvětlit

Snímek ukládá kopii hodnot; key v LazyColumn musí být jedinečné ID.

## Ověření

Uložte dva stejně pojmenované snímky, změňte formulář, smažte jeden snímek. Druhý zůstane nezměněný. Otočení zachová historii, nové spuštění procesu ji ztratí.

```bash
./gradlew assembleDebug testDebugUnitTest lintDebug
```

Ve Windows použijte `.\gradlew.bat` místo `./gradlew`. Přesné připravené řešení porovnejte pomocí `git diff first_counter_viewmodel..first_counter_viewmodel_list -- app/src/main`.

## Samostatný úkol

Přidejte součet historie jako odvozenou hodnotu.

## Kontrolní seznam a Git

- [ ] Rozumím vlastníkovi stavu a směru událostí.
- [ ] Prošel jsem ruční scénáře a sestavení.
- [ ] Změny obsahují jen soubory tohoto cvičení, bez local.properties a build výstupů.

```bash
git status
git diff
git add app gradle build.gradle.kts
git commit -m "Dokonci CV3: Snímky a seznam historie"
```

Pokud projekt nesestavíte, nejprve ověřte Gradle JDK a SDK podle README. Chybějící importy doplňte podle typu, ne podle shody názvu z jiné knihovny.
