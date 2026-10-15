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

    @Test fun snapshotsAreIndependentAndRemovedById() {
        val model = CounterViewModel()
        model.save()
        assertTrue(model.history.isEmpty())
        repeat(2) { model.rename(" Stejný "); model.increment(); model.save() }
        assertEquals("", model.name)
        assertEquals(0, model.count)
        assertEquals(2, model.history.size)
        assertEquals("Stejný", model.history.first().name)
        model.increment()
        assertEquals(1, model.history.first().value)
        val retained = model.history.last()
        model.remove(model.history.first().id)
        assertEquals(listOf(retained), model.history)
    }
}
