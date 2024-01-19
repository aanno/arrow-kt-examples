package com.github.aanno.arrowkt

import arrow.core.Either
import arrow.core.raise.either

fun doSomething(): Either<Throwable, Int> = Either.Left(Throwable("Oh no"))

fun doSomethingElse(): Either<Throwable, Int> = Either.Right(5)

fun compliance(): Either<Throwable, Int> {
    return either {
        // should fail!
        // doSomething()
        // all right
        doSomething().bind()
        doSomethingElse().bind()
    }
}
