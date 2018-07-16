package de.skuolari.punylisp.values

typealias PunyDouble = Wrapper<Double>

operator fun PunyDouble.plus(punyDouble: PunyDouble) = PunyDouble(this.value + punyDouble.value)
operator fun PunyDouble.minus(punyInt: PunyDouble) = PunyDouble(this.value - punyInt.value)
operator fun PunyDouble.times(punyInt: PunyDouble) = PunyDouble(this.value * punyInt.value)
operator fun PunyDouble.div(punyInt: PunyDouble) = PunyDouble(this.value / punyInt.value)
operator fun PunyDouble.compareTo(punyInt: PunyDouble) = value.compareTo(punyInt.value)