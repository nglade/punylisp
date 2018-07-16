package de.skuolari.punylisp.values

typealias PunyInt = Wrapper<Int>

operator fun PunyInt.plus(punyInt: PunyInt) = PunyInt(this.value + punyInt.value)
operator fun PunyInt.minus(punyInt: PunyInt) = PunyInt(this.value - punyInt.value)
operator fun PunyInt.times(punyInt: PunyInt) = PunyInt(this.value * punyInt.value)
operator fun PunyInt.div(punyInt: PunyInt) = PunyInt(this.value / punyInt.value)
operator fun PunyInt.compareTo(punyInt: PunyInt) = value.compareTo(punyInt.value)