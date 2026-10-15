package cz.utb.ap7mt.counter

import org.junit.Assert.*
import org.junit.Test

class CounterStateTest {
    @Test fun bounds() {
        assertEquals(0, CounterState().decrement().count)
        assertEquals(Int.MAX_VALUE, CounterState(count = Int.MAX_VALUE).increment().count)
    }
    @Test fun snapshotAndResetAreOneChange() {
        val original = CounterState("  Hosté  ", 7)
        val saved = original.save("one", 42)
        assertEquals("", saved.name)
        assertEquals(0, saved.count)
        assertEquals(CounterHistoryItem("one", "Hosté", 7, 42), saved.history.single())
        assertEquals(7, original.count)
        assertEquals(CounterState(), CounterState("  ").save("empty", 1).copy(name = ""))
    }
    @Test fun retainsOnlyNewestHundred() {
        var state = CounterState()
        repeat(101) { state = state.copy(name = "N").save(it.toString(), it.toLong()) }
        assertEquals(100, state.history.size)
        assertEquals("100", state.history.first().id)
        assertEquals("1", state.history.last().id)
    }
}
