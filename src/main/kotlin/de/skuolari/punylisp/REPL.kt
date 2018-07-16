package de.skuolari.punylisp

import de.skuolari.punylisp.Reader.Companion.readForm
import de.skuolari.punylisp.values.PunyException
import de.skuolari.punylisp.values.PunyValue

fun read(s: String) = readForm(Reader(s))
fun eval(s: PunyValue, env: Environment) = try {
    Evaluator(s, env)
} catch (e: Exception) {
    if (e is PunyException) e else PunyException(inner = e)
}

fun print(s: PunyValue) =
        if (s is Exception)
            System.err.println(s.message)
        else System.out.println(
                s.format(true))
fun re(s:String, env:Environment) = eval(read(s),env)
fun rep(s: String, env: Environment) = de.skuolari.punylisp.print(re(s, env))

fun defaultEnvironment() = MapEnvironment().apply {
    core.forEach { s, value -> set(s, value) }
    re("(def! not (fn* (a) (if a #f #t)))", this)
}

fun main(vararg args: String) {
    var input = ""
    val replEnvironment = defaultEnvironment()
    while (true) {
        rep(input, replEnvironment)
        input = readLine()!!
    }
}
