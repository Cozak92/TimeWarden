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

fun zonedDateTimeNow(): ZonedDateTime {
    return TimeWarden.now(ZonedDateTime::class.java) as ZonedDateTime
}

fun localDateTimeNow(): LocalDateTime {
    return TimeWarden.now(LocalDateTime::class.java) as LocalDateTime
}

fun instantNow(): Instant {
    return TimeWarden.now(Instant::class.java) as Instant
}

