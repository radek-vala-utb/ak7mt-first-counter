# Ověření implementace – 15. 10. 2026

Ověřený zdrojový commit: `3c7c49dccf140d5f2a15efb0b1eb34b3851c1bd0`, větev `first_counter_persistance`.

Prostředí: JDK 25 z Android Studia, Gradle Wrapper 9.6.0, SDK 37. Instrumentační testy: emulátor Pixel 8, Android 17/API 37.

```bash
./gradlew assembleDebug testDebugUnitTest lintDebug connectedDebugAndroidTest
```

Výsledek: **BUILD SUCCESSFUL**, 5 lokálních testů a 1 instrumentačních testů, bez selhání. Lint nemá chyby; upozorňuje na dostupné novější závislosti, chybějící vlastní ikonu a konfiguraci záloh. Verze výukového základu nebyly automaticky aktualizovány.

Samostatně prošly assembleDebug, testDebugUnitTest a lintDebug také větve `main`, `first_counter`, `first_counter_viewmodel`, `first_counter_viewmodel_list` a `first_counter_viewmodel_tabs`.

Pokryté chování: hranice nuly a Int, nezávislé snímky, limit historie, JSON s oddělovači a uvozovkami, souběžné přírůstky, obnovení DataStore, ochrana před přepsáním poškozeného JSON a UI tok zadání → uložení → historie → smazání.
