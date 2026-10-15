# CV1 – Compose a lokální stav

## Cíl a výchozí stav

Navazujete na `main`, cílové řešení je `first_counter`. Ověřte funkčnost předchozího milníku a čistý `git status`. Postupujte ve vlastní větvi:

```bash
git switch main
git switch -c student/cv01
```

## Soubory

Kotlin soubory jsou v `app/src/main/java/cz/utb/ap7mt/counter/`: MainActivity.kt. Závislosti najdete v `app/build.gradle.kts` a `gradle/libs.versions.toml`.

## Postup

1. V `MainActivity` ponechte `setContent`, `MaterialTheme` a `Surface`. Přidejte `CounterScreen` a zavolejte jej uvnitř `Surface`.

2. V Composable vytvořte `name` pomocí `remember { mutableStateOf("") }` a `count` pomocí `remember { mutableIntStateOf(0) }`. Importy `getValue` a `setValue` umožní delegaci `by`.

3. Použijte Column, OutlinedTextField, Text a tlačítka. TextField dostává `value = name` a změnu vrací přes `onValueChange`.

4. Implementujte +1, −1 a Reset. Snížení omezte nulou i v obsluze události. Přidání omezte maximem Int, aby nedošlo k přetečení.

5. Použijte safeDrawingPadding a vertikální scroll, aby byl formulář použitelný na malém displeji i s klávesnicí.

## Co si vysvětlit

Běžná proměnná není pozorovatelný stav. rememberSaveable umí uchovat malý stav UI při obnově; zde jej záměrně nepoužíváme, abychom viděli rozdíl vůči ViewModelu.

## Ověření

Název se ihned mění; 0 → +1 → 1 → −1 → 0; další −1 je zakázané; Reset nemění název. Otočení aktivity s lokálním remember stav ztratí. Rekompozice jej naopak zachová.

```bash
./gradlew assembleDebug testDebugUnitTest lintDebug
```

Ve Windows použijte `.\gradlew.bat` místo `./gradlew`. Přesné připravené řešení porovnejte pomocí `git diff main..first_counter -- app/src/main`.

## Samostatný úkol

Přidejte tlačítko +10 s ošetřením přetečení.

## Kontrolní seznam a Git

- [ ] Rozumím vlastníkovi stavu a směru událostí.
- [ ] Prošel jsem ruční scénáře a sestavení.
- [ ] Změny obsahují jen soubory tohoto cvičení, bez local.properties a build výstupů.

```bash
git status
git diff
git add app gradle build.gradle.kts
git commit -m "Dokonci CV1: Compose a lokální stav"
```

Pokud projekt nesestavíte, nejprve ověřte Gradle JDK a SDK podle README. Chybějící importy doplňte podle typu, ne podle shody názvu z jiné knihovny.
