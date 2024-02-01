package com.github.aanno.arrowkt

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import arrow.core.right
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

fun User.isValid(): Either<UserNotFound, Unit> =
    either {
        ensure(id > 0) { UserNotFound("User without a valid id: $id") }
    }

fun process(user: User?): Either<UserNotFound, Long> =
    either {
        ensureNotNull(user) { UserNotFound("Cannot process null user") }
        user.id // smart-casted to non-null
    }

class ArrowTypedErrorInValue : ShouldSpec({
    should("user invalid") {
        User(-1).isValid() shouldBe UserNotFound("User without a valid id: -1").left()
    }
    should("user valid") {
        User(101).isValid() shouldBe Unit.right()
    }
    should("process invalid") {
        process(null) shouldBe UserNotFound("Cannot process null user").left()
    }
})
