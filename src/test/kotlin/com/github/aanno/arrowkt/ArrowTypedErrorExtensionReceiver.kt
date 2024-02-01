package com.github.aanno.arrowkt

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import arrow.core.raise.fold
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

fun Raise<UserNotFound>.isValid(user: User): User {
    ensure(user.id > 0) { UserNotFound("User without a valid id: ${user.id}") }
    return user
}

class ArrowTypedErrorExtensionReceiver : ShouldSpec({
    should("user invalid") {
        fold(
            { isValid(User(-2)) },
            { err: UserNotFound -> err },
            { user: User -> user.id shouldBe UserNotFound("User without a valid id: -2") }
        )
    }
    should("user valid") {
        fold(
            { isValid(User(1)) },
            { err: UserNotFound -> err },
            { user: User -> user.id shouldBe 1 }
        )
    }
})
