package com.github.aanno.arrowkt

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import arrow.core.raise.ensure
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

data class User(val id: Long)
data class UserNotFound(val message: String)

fun User.isValid(): Either<UserNotFound, Unit> = either {
    ensure(id > 0) { UserNotFound("User without a valid id: $id") }
}

class ArrowTypedErrorInValue : ShouldSpec({
    should("user invalid") {
        User(-1).isValid() shouldBe  UserNotFound("User without a valid id: -1").left()
    }
})
