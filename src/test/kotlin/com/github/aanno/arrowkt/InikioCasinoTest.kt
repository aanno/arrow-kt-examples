package com.github.aanno.arrowkt

import com.github.aanno.arrowkt.Done.LOSE
import com.github.aanno.arrowkt.Done.WIN
import com.github.aanno.arrowkt.FlipCoin.Outcome.HEADS
import io.kotest.core.spec.style.ShouldSpec

enum class Done { WIN, LOSE }

class InikioCasinoTest : ShouldSpec({
    should("2 coins heads win") {
        val doubleCoin =
            casino {
                val o1 = flipCoin()
                val o2 = flipCoin()
                if (o1 == HEADS && o2 == HEADS) {
                    WIN
                } else {
                    LOSE
                }
            }
    }
})
