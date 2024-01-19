package com.github.aanno.arrowkt

import arrow.core.left
import arrow.core.right
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import com.github.aanno.arrowkt.FlipCoin.Outcome.*
import com.github.aanno.arrowkt.Done.*

enum class Done { WIN, LOSE }

class InikioCasinoTest : ShouldSpec({
    should("2 coins heads win") {
        val doubleCoin = casino {
            val o1 = flipCoin()
            val o2 = flipCoin()
            if (o1 == HEADS && o2 == HEADS) WIN
            else LOSE
        }
    }
})
