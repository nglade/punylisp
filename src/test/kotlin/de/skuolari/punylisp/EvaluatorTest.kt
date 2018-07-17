package de.skuolari.punylisp

import de.skuolari.punylisp.values.*
import org.junit.Assert.*
import org.junit.Test


class EvaluatorTest {

    @Test
    fun invoke() {
        val env = defaultEnvironment()
        val expectedSize = env.size + 1
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
        env.assertShape(expectedSize, null)
    }

    @Test
    fun testDo() {
        val env = defaultEnvironment()
        re("(do (def! a 0) (def! x 1) (def! y 2) (def! z 3) (def! a 42))", env)
        assertEquals(1.wrap(), env.get("x"))
        assertEquals(2.wrap(), env.get("y"))
        assertEquals(3.wrap(), env.get("z"))
        assertEquals(42.wrap(), env.get("a"))
    }

    @Test
    fun testIf() {
        val env = defaultEnvironment()
        val values = mapOf(
                "x" to 42.wrap(), "y" to 42.wrap(), "z" to 3.wrap())
        values.forEach {
            env.set(it.key, it.value)
            assertTrue(re("(if (= ${it.key} ${it.value.unwrap<Int>()}) #t #f)", env).unwrap())
            assertTrue(re("(if (< ${it.key} ${it.value.unwrap<Int>()}) #f #t)", env).unwrap())
            assertTrue(re("(if (> ${it.key} ${it.value.unwrap<Int>()}) #f #t)", env).unwrap())
            assertTrue(re("(if (= ${it.key} 0) #f #t)", env).unwrap())
        }
    }

    @Test
    fun evalList() {
        val result = re("(list 42 \"test\" #t #f -1.0)", defaultEnvironment())
        assertEquals(punyList(42, "test", true, false, -1.0), result)
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

    fun Environment.get(s: String) = get(Symbol(s))
    fun MapEnvironment.isEmpty() = this.size == 0 && outer == null
    fun MapEnvironment.assertShape(expectedSize: Int, expectedOuter: Environment?) {
        assertEquals(expectedSize, size)
        assertEquals(expectedOuter, outer)
    }
}