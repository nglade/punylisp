package de.skuolari.punylisp

import de.skuolari.punylisp.values.PunyInt
import de.skuolari.punylisp.values.PunyValue
import de.skuolari.punylisp.values.Symbol
import de.skuolari.punylisp.values.Wrapper
import org.junit.Assert.*
import org.junit.Test
import kotlin.math.exp


class EvaluatorTest {

    @Test
    fun invoke() {
        val env = defaultEnvironment()
        val expectedSize = env.size +1
        re("(def! answer 42)", env)
        val value = env.get(Symbol("answer"))
        assertTrue(value is Wrapper<*> && value.value is Int)
        @Suppress("UNCHECKED_CAST")
        assertEquals(42, (value as PunyInt).value)
        env.assertShape(expectedSize, null)

    }

    @Test
    fun testLambda() {
        val program = "((fn* (x) (+ x 1)) 41)"
        val env = defaultEnvironment()
        val expectedSize = env.size
        val result = re(program, env)
        assertTrue(result is Wrapper<*> && result.value is Int)
        @Suppress("UNCHECKED_CAST")
        assertEquals(42, (result as PunyInt).value)
        env.assertShape(expectedSize,null)
    }

    @Test
    fun evalList() {
        TODO("assert that a list is returned that contains the value of each expression in the argument list")
    }

    @Test
    fun eval() {
        val e = Evaluator
        for (i in -10..10) {
            val p = PunyInt(i)
            val v = e(p, defaultEnvironment())
            assertEquals(p, v)
        }
        val set = Symbol("")
        TODO("assert that the evaluator evaluates symbols to the value inside the environment, calls eval() for each list element of a list, and returns other expressions unchanged.")

    }

    @Test
    fun factorial() {
        fun fac(i: Int): Int = if (i <= 0) 1 else i * fac(i - 1)
        val env = defaultEnvironment()
        (-100..100).forEach { i: Int ->
            // FIXME repl currently only evaluates one top-level form per read-eval call.
            val p1 = "(def! fac (fn* (x) (if (<= x 0) 1 (* x (fac (- x 1))))))"
            val p2 = "(fac $i)"
            re(p1, env)
            val result: PunyValue? = re(p2, env)
            assertNotNull(result)
            assertTrue(result is Wrapper<*> && result.value is Int)
            @Suppress("UNCHECKED_CAST")
            assertEquals(fac(i), (result as PunyInt).value)
        }
    }

    fun MapEnvironment.isEmpty() = this.size == 0 && outer == null

    fun MapEnvironment.assertShape(expectedSize: Int, expectedOuter: Environment?) {
        assertEquals(expectedSize, size)
        assertEquals(expectedOuter, outer)
    }
}