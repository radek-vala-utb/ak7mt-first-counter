package cz.utb.ap7mt.counter

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class CounterAppTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun rapidTypingSavingAndTabs() {
        compose.waitUntil(10_000) {
            compose.onAllNodesWithText("Název počítadla").fetchSemanticsNodes().isNotEmpty()
        }
        val name = "Návštěvníci | ;;; " + System.nanoTime()
        compose.onNodeWithText("Reset").performClick()
        compose.onNodeWithText("Název počítadla").performTextReplacement(name)
        repeat(5) { compose.onNodeWithText("+1").performClick() }
        compose.onNodeWithText("Název počítadla").assertTextContains(name)
        compose.onNodeWithText("Uložit do historie").performScrollTo().performClick()
        compose.onNodeWithText("Historie").performClick()
        compose.onNodeWithText(name).assertIsDisplayed()
        compose.onNodeWithText("5").assertIsDisplayed()
        compose.onAllNodesWithText("Smazat záznam")[0].performClick()
        compose.onNodeWithText(name).assertDoesNotExist()
    }
}
