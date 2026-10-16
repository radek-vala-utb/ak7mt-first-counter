# Ověření rozšířených návodů

Datum: 16. 10. 2026.

Pro každý návod byl vytvořen dočasný projekt z jeho výchozí Git větve. Do něj byly vloženy kompletní soubory z bloků kódu v návodu a odstraněny testy, které návod výslovně nahrazuje. Kontrola ověřila, že návod obsahuje všechny změněné zdrojové a konfigurační soubory příslušného milníku, nikoliv jen vybrané ukázky. Export Room schématu byl ponechán generátoru.

| Cvičení | Kompletní soubory v návodu | Lokální testy | Sestavení, testy a lint |
| --- | --- | --- | --- |
| CV2 | 5 | 2 | Úspěšné |
| CV3 | 6 | 3 | Úspěšné |
| CV4 | 2 | 3 | Úspěšné |
| CV5 | 12 | 5 | Úspěšné |

Ověřovací příkaz:

```bash
./gradlew assembleDebug testDebugUnitTest lintDebug
```

Pro CV5 a CV7 se navíc spustilo `assembleDebugAndroidTest`, které ověřilo překlad instrumentačních testů. Při této úpravě dokumentace nebyly testy znovu spuštěny na zařízení; jejich předchozí běh byl zaznamenán u implementace. Všechny nové příklady odpovídají již existujícím referenčním souborům, rozdíl může být jen v koncových mezerách.

Ověřovací prostředí: Gradle Wrapper 9.6.0, JDK 25 z Android Studia, SDK 37. Lint skončil bez chyb; ponechává stávající upozornění referenčního projektu, například na novější závislosti. Aplikační kód se při rozšíření návodů neměnil.

Obdobným postupem byly dříve ověřeny všechny postupné ukázky CV1. Kopie CV1–CV5 ve druhém repozitáři jsou opatřené upozorněním, že jejich realizace patří do prvního repozitáře.
