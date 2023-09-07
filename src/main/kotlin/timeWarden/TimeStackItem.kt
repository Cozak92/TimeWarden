package timeWarden

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.Temporal

internal class TimeStackItem(
    val mockType: MockType,
    val time: Temporal,
    val scaling: Long = 0,
    val timeUnit: ChronoUnit = ChronoUnit.MINUTES
) {

    val timeClazz: Class<*> = time.javaClass
    val originalTime: Temporal = when (time) {
        is LocalDateTime -> LocalDateTime.now()
        is ZonedDateTime -> ZonedDateTime.now()
        is Instant -> Instant.now()
        else -> throw IllegalArgumentException("Unsupported Temporal type")
    }


    enum class MockType {
        FREEZE,
        TRAVEL,
        SCALE,
    }

}