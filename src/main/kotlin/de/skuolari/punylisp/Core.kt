package de.skuolari.punylisp

import de.skuolari.punylisp.values.*


// type errors are expected and will be handled accordingly.
@Suppress("UNCHECKED_CAST")
val core = mapOf<String, PunyValue>(
        "list" to Lambda { values: List<PunyValue> ->
            PunyList(values)
        },
        "list?" to Lambda { v -> (v[0] is PunyList).wrap() },
        "empty?" to Lambda { (it.first() as List<PunyValue>).isEmpty().wrap() },
        "count" to Lambda { (it.first() as List<PunyValue>).size.wrap() },
        "=" to Lambda { (it[0] == it[1]).wrap() },
        "<" to numSwitch({ i: Int, j: Int -> (i < j).wrap() },
                { i: Double, j: Double -> (i < j).wrap() }),
        ">" to numSwitch({ i: Int, j: Int -> (i > j).wrap() },
                { i: Double, j: Double -> (i > j).wrap() }),
        "<=" to numSwitch({ i: Int, j: Int -> (i <= j).wrap() },
                { i: Double, j: Double -> (i <= j).wrap() }),
        ">=" to numSwitch({ i: Int, j: Int -> (i + j).wrap() },
                { i: Double, j: Double -> (i + j).wrap() }),
        "+" to numSwitch({ i: Int, j: Int -> (i + j).wrap() },
                { i: Double, j: Double -> (i + j).wrap() }),
        "-" to numSwitch({ i: Int, j: Int -> (i - j).wrap() },
                { i: Double, j: Double -> (i - j).wrap() }),
        "*" to numSwitch({ i: Int, j: Int -> (i * j).wrap() },
                { i: Double, j: Double -> (i * j).wrap() }),
        "/" to numSwitch({ i: Int, j: Int -> (i / j).wrap() },
                { i: Double, j: Double -> (i / j).wrap() })
)

fun numSwitch(i: (Int, Int) -> PunyValue, d: (kotlin.Double, kotlin.Double) -> PunyValue) = Lambda {
    val v1 = (it[0] as Wrapper<*>).value
    val v2 = (it[1] as Wrapper<*>).value
    if (v1 is Double) {
        if (v2 is Int) d(v1, v2.toDouble()) else
            d(v1, v2 as Double)
    } else {
        if (v2 is Double)
            d((v1 as Int).toDouble(), v2)
        else i(v1 as Int, v2 as Int)
    }
}
