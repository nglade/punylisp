package de.skuolari.punylisp

import de.skuolari.punylisp.values.*

abstract class Environment(open val outer: Environment? = null, binds: PunyList = empty, expressions: List<PunyValue> = emptyList()) {

    abstract fun set(key: Symbol, value: PunyValue)

    fun set(key: String, value: PunyValue) = set(Symbol(key), value)

    abstract fun find(symbol: Symbol): Environment?

    abstract fun get(symbol: Symbol): PunyValue

    // inheriting classes should call this in their constructors to add supplied bindings to this environment.
    protected val bindingInitializer: () -> Unit

    init {
        assert(binds.size == expressions.size)
        bindingInitializer = {
            for (i in 0 until binds.size) {
                this.set(binds[i] as Symbol, expressions[i])
            }
        }
    }
}

/**
 * Thrown to indicate that a requested symbol
 *
 * could not be found within an environment hierarchy.
 */
class SymbolNotFoundException(symbol: Symbol) : PunyException("Symbol not found : ${symbol.s}")