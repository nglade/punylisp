package de.skuolari.punylisp

import de.skuolari.punylisp.values.PunyList
import de.skuolari.punylisp.values.PunyValue
import de.skuolari.punylisp.values.Symbol
import de.skuolari.punylisp.values.empty

class MapEnvironment(outer: Environment?=null, binds: PunyList = empty, expressions: List<PunyValue> = empty) : Environment(outer, binds, expressions) {
    private val data  by lazy { HashMap<Symbol, PunyValue>()}

    override fun set(key: Symbol, value: PunyValue) {
        data[key] = value
    }

    override fun find(symbol: Symbol): Environment? = if (data.containsKey(symbol)) this else outer?.find(symbol)

    override fun get(symbol: Symbol): PunyValue {
        val v = data.getOrElse(symbol) { outer?.get(symbol) }
        if (v != null)
            return v
        else throw SymbolNotFoundException(symbol)
    }
    init {
        bindingInitializer()
    }

    val size get() = data.size
}