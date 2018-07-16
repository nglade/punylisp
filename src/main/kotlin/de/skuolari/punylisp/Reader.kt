package de.skuolari.punylisp

import de.skuolari.punylisp.values.*
import java.util.regex.Pattern

class Reader(val input: String) {
    enum class Prefix(val prefix: String, val expanded: String = "") {
        PAREN("("), QUOTE("'", "quote"), BACK_TICK("`", "quasiquote"),TILDE_AT("~@", "splice-unquote"),  TILDE("~", "unquote"), OTHER("");

        fun action(r: Reader) =
                when (this) {
                    Reader.Prefix.QUOTE,
                    Reader.Prefix.BACK_TICK,
                    Reader.Prefix.TILDE_AT,
                    Reader.Prefix.TILDE -> {
                        r.next()
                        punyList(Symbol(expanded), readForm(r))
                    }
                    Reader.Prefix.OTHER -> readAtom(r)
                    else -> {
                        r.next()
                        readList(r)
                    }
                }
    }


    companion object {
        const val delimiter = "[\\s,]*"
        const val tat = "~@"
        const val ssc = "[\\[\\](){}'`@~]"
        const val stringLiteral = "\\\"(.|[^\\\\]\\\")*\""
        const val comments = ";.*"
        const val symb = """[^\s\[\]{}()'\"`,;]*"""

        val pattern = Pattern.compile("$delimiter($tat|$ssc|$stringLiteral|$comments|$symb)")

        fun readForm(r: Reader): PunyValue {
            val p = r.peek()
            return if (p == null) Nil
            else Prefix.values().first { p.startsWith(it.prefix) }.action(r)
        }


        /**
         * Reads in any of the following:
         * * the boolean values <code>true,false</code>
         * * integer and double literals
         * * comments
         * * string literals
         * * symbols
         */
        fun readAtom(r: Reader): PunyValue {
            val a = r.next()!!
            val first = a.first()
            when {
                first == '"' -> return a.substring(1, a.length - 1)
                        .replace("\\\\", "\\")
                        .replace("\\\"", "\"")
                        .replace("\\n", "\n")
                        .wrap()
                a == "#t" -> return true.wrap()
                a == "#f" -> return false.wrap()
                else -> {
                    try {
                        val n = Integer.parseInt(a)
                        return n.wrap()
                    } catch (nf1: NumberFormatException) {
                    }
                    try {
                        return java.lang.Double.parseDouble(a).wrap()
                    } catch (nf2: NumberFormatException) {
                    }
                    if (first == ';') return Nil
                    else return Symbol(a)
                }
            }
        }

        fun readList(r: Reader): PunyList {
            val list = PunyList()
            var next: String?
            while (true) {
                next = r.peek()
                when {
                    next == null -> throw SyntaxErrorException("Missing closing paren")
                    next.first() == ')' -> {
                        r.next()
                        return list
                    }
                    else -> list.add(readForm(r))
                }
            }
        }
    }

    var iterator = tokenize()

    fun tokenize(): MutableListIterator<String> {
        val result = ArrayList<String>()
        for (match in pattern.matcher(input).results()) {
            for (i in 1 until match.groupCount())
                if (match.group(i) != null && match.group(i).isNotEmpty())
                    result.add(match.group(i))
        }
        return result.listIterator()
    }

    fun next() = if (iterator.hasNext()) iterator.next() else null

    fun peek() = next()?.also { iterator.previous() }

}

class SyntaxErrorException(message: String) : PunyException(message)