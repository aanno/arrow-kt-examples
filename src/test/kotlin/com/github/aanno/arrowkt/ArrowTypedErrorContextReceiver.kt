package com.github.aanno.arrowkt

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import arrow.core.raise.fold
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

context(Raise<UserNotFound>)
fun User.isValid(): Unit = ensure(id > 0) { UserNotFound("User without a valid id: $id") }

fun Raise<UserNotFound>.process(user: User?): Long {
    ensureNotNull(user) { UserNotFound("Cannot process null user") }
    return user.id // smart-casted to non-null
}

class ArrowTypedErrorContextReceiver : ShouldSpec({
    should("process valid") {
        fold(
            { process(User(1)) },
            { err: UserNotFound -> err },
            { i: Long -> i shouldBe 1L }
        )
    }
})
