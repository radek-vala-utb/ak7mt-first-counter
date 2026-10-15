# CV2 – Stav ve ViewModelu

## Cíl a výchozí stav

Navazujete na `first_counter`, cílové řešení je `first_counter_viewmodel`. Ověřte funkčnost předchozího milníku a čistý `git status`. Postupujte ve vlastní větvi:

```bash
git switch first_counter
git switch -c student/cv02
```

## Soubory

Kotlin soubory jsou v `app/src/main/java/cz/utb/ap7mt/counter/`: CounterViewModel.kt, CounterScreen.kt, MainActivity.kt. Závislosti najdete v `app/build.gradle.kts` a `gradle/libs.versions.toml`.

## Postup

1. Přidejte závislost lifecycle-viewmodel-compose. Vytvořte CounterViewModel dědící z ViewModel.

2. Přesuňte name, count a operace do ViewModelu. Stav má private set, UI jej mění pouze přes metody.

3. CounterScreen přijímá name, count a callbacky. Nemusí vědět, kdo stav vlastní; lze jej snadno zobrazit v Preview.

4. V kořeni Compose získávejte instanci pomocí viewModel<CounterViewModel>(). Nepoužívejte běžný konstruktor při rekompozici.

5. Propojte hodnoty a reference metod s obrazovkou. Spusťte jednotkové testy hranice nuly, resetu a změny názvu.

## Co si vysvětlit

ViewModel není trvalé úložiště. Události směřují z UI do ViewModelu, stav opačně.

## Ověření

Změňte název a hodnotu, otočte emulátor: data zůstanou. Ukončení procesu ViewModel nepřežije. Vzhled a tlačítka zůstávají stejné.

```bash
./gradlew assembleDebug testDebugUnitTest lintDebug
```

Ve Windows použijte `.\gradlew.bat` místo `./gradlew`. Přesné připravené řešení porovnejte pomocí `git diff first_counter..first_counter_viewmodel -- app/src/main`.

## Samostatný úkol

Doplňte test nezávislosti dvou instancí ViewModelu.

## Kontrolní seznam a Git

- [ ] Rozumím vlastníkovi stavu a směru událostí.
- [ ] Prošel jsem ruční scénáře a sestavení.
- [ ] Změny obsahují jen soubory tohoto cvičení, bez local.properties a build výstupů.

```bash
git status
git diff
git add app gradle build.gradle.kts
git commit -m "Dokonci CV2: Stav ve ViewModelu"
```

Pokud projekt nesestavíte, nejprve ověřte Gradle JDK a SDK podle README. Chybějící importy doplňte podle typu, ne podle shody názvu z jiné knihovny.
