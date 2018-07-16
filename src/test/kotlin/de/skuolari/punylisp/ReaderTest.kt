package de.skuolari.punylisp

import de.skuolari.punylisp.Reader.Companion.readForm
import de.skuolari.punylisp.values.PunyList
import de.skuolari.punylisp.values.PunyValue
import de.skuolari.punylisp.values.Symbol
import de.skuolari.punylisp.values.punyList
import org.junit.Assert.assertEquals
import org.junit.Test

class ReaderTest{

    @Test
    fun testNested() {
        val input = "((fn* (x) (+ x 1)) 41)"
        val r = Reader(input)
        val list:List<PunyValue> = readForm(r) as PunyList
        assertEquals(2,list.size)
        print(list)
    }

    @Test
    fun testQuotation() {
        val x = Symbol("x")
        assertEquals(punyList(Symbol("quote"), x), read("'x"))
        assertEquals(punyList(Symbol("quasiquote"), x), read("`x"))
        assertEquals(punyList(Symbol("unquote"), x), read("~x"))
        assertEquals(punyList(Symbol("splice-unquote"), x), read("~@x"))
    }
}