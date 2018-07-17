package de.skuolari.punylisp.values

interface PunyValue {
    /**
     * @param readably when true, value should be printed such that they can be parsed by a reader again.
     */
    fun format(readably: Boolean = true): String
}

object Nil : PunyValue {
    override fun format(readably: Boolean): String = ""
}

class PunyList(values: List<PunyValue>) : ArrayList<PunyValue>(values), PunyValue {
    override fun format(readably: Boolean) =
            joinToString(" ", "(", ")") { it.format() }

    constructor() : this(emptyList())
}

val empty = PunyList()

fun punyList(vararg content: PunyValue) = PunyList().apply { addAll(content) }
fun <T:Any> punyList(vararg content:T) = PunyList().apply { addAll(content.map { it.wrap() }) }
class Lambda(val b: (List<PunyValue>) -> PunyValue) : PunyValue {
    override fun format(readably: Boolean) = "<lambda>"

    operator fun invoke(l: List<PunyValue>) = b(l)
}

/**
 * Return a lambda that can be invoked by the evaluator.
 */
fun <V, W, R> ((V, W) -> R).makeLambda(): Lambda = Lambda(this.let {
    { list: List<PunyValue> ->
        val a = list[0] as Wrapper<*>
        val b = list[1] as Wrapper<*>
        // at the time, cannot guarantee that argument values
        // will be correctly typed.
        @Suppress("UNCHECKED_CAST")
        it(a.value as V, b.value as W).wrap()
    }
})

data class Symbol(val s: String) : PunyValue {
    override fun format(readably: Boolean) = s
}

fun String.makeSymbol() = Symbol(this)

open class Wrapper<T>(val value: T) : PunyValue {
    override fun format(readably: Boolean): String = if (this.value is String) {
        if (!readably) value
        else """"${value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")}""""
    } else if (this.value is Boolean) {
        if (this.value) "#t"
        else "#f"
    } else value.toString()

    override fun toString() = value.toString()
    override fun hashCode() = value!!.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Wrapper<*>

        if (value != other.value) return false

        return true
    }
}

/**
 * Convert any object to a wrapped value.
 */
fun <A> A.wrap(): Wrapper<A> = Wrapper(this)
fun <A> PunyValue.unwrap() = (this as Wrapper<A>).value
open class PunyException(message: String = "", inner: Exception? = null) : Exception(message, inner), PunyValue {
    override fun format(readably: Boolean) = message!!

}