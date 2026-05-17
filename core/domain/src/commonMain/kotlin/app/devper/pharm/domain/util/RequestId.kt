package app.devper.pharm.domain.util

import kotlin.random.Random

fun newClientRequestId(): String {
    val a = Random.nextLong().toULong().toString(16).padStart(16, '0')
    val b = Random.nextLong().toULong().toString(16).padStart(16, '0')
    return "kmp-$a$b"
}
