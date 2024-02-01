package com.github.aanno.arrowkt

import fp.serrano.inikio.plugin.InitialStyleDSL

@InitialStyleDSL
sealed interface Casino<out A>

data class Done<out A>(val result: A) : Casino<A>

data class FlipCoin<out A>(val next: (Outcome) -> Casino<A>) : Casino<A> {
    enum class Outcome { HEADS, TAILS }
}
