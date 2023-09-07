package timeWarden

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.temporal.Temporal

fun Temporal.freeze(block: (Temporal) -> Unit) {
    TimeWarden.freeze(this, block)
}

fun Temporal.travel(block: (Temporal) -> Unit) {
    TimeWarden.travel(this, block)
}

fun Temporal.scale(scaling: Long, block: (Temporal?) -> Unit) {
    TimeWarden.scale(this, scaling, block)
}