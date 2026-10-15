package cz.utb.ap7mt.counter

import org.junit.Assert.*
import org.junit.Test

class CounterViewModelTest {
    @Test fun decrementStopsAtZero() {
        val model = CounterViewModel()
        model.decrement()
        assertEquals(0, model.count)
        model.increment(); model.increment(); model.decrement()
        assertEquals(1, model.count)
    }
    @Test fun resetPreservesName() {
        val model = CounterViewModel()
        model.rename("Návštěvníci"); model.increment(); model.reset()
        assertEquals("Návštěvníci", model.name)
        assertEquals(0, model.count)
    }
}
