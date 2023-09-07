package timeWarden

import java.lang.RuntimeException
import java.time.*
import java.time.temporal.ChronoUnit
import java.time.temporal.Temporal
import java.util.*

fun zonedDateTimeNow(): ZonedDateTime {
    return TimeWarden.now(ZonedDateTime::class.java) as ZonedDateTime
}

fun localDateTimeNow(): LocalDateTime {
    return TimeWarden.now(LocalDateTime::class.java) as LocalDateTime
}

fun instantNow(): Instant {
    return TimeWarden.now(Instant::class.java) as Instant
}


object TimeWarden {
    private val timeStack: Stack<TimeStackItem> = Stack()
    val isThreadSafe: Boolean = false
    val isMocked: Boolean
        get() = timeStack.isNotEmpty()


    fun undo() {
        timeStack.pop()
    }

    fun unMock() {
        timeStack.clear()
    }

    fun freeze(time: Temporal, block: (Temporal) -> Unit) {
        push(time, TimeStackItem.MockType.FREEZE)

        try {
            block(time)
        } finally {
            timeStack.pop()
        }
    }

    fun freeze(time: Temporal) {
        push(time, TimeStackItem.MockType.FREEZE)
    }

    fun travel(time: Temporal, block: (Temporal) -> Unit) {
        push(time, TimeStackItem.MockType.TRAVEL)

        try {
            block(time)
        } finally {
            timeStack.pop()
        }
    }
    fun travel(time: Temporal) {
        push(time, TimeStackItem.MockType.TRAVEL)
    }

    fun scale(time: Temporal? = null, scale: Long, block: (Temporal?) -> Unit) {

        scaleCheck(scale)
        push(time ?: ZonedDateTime.now(), TimeStackItem.MockType.SCALE, scale)
        try {
            block(time)
        } finally {
            timeStack.pop()
        }
    }

    fun scale(time: Temporal? = null, scale: Long) {
        scaleCheck(scale)
        push(time ?: ZonedDateTime.now(), TimeStackItem.MockType.SCALE, scale)
    }

    private fun scaleCheck(scale: Long) = if (scale <= 0) throw IllegalArgumentException("Scale must be greater than 0") else scale

    fun now(clazz: Class<*>): Temporal {
        val timeStackItem = try {
            timeStack.peek()
        } catch (e: EmptyStackException) {
            throw RuntimeException("TimeWarden has not been mocked")
        }


        return when (timeStackItem.mockType) {
            TimeStackItem.MockType.FREEZE -> convert(timeStackItem.time, clazz)
            TimeStackItem.MockType.TRAVEL -> convert(makeTravel(timeStackItem), clazz)
            TimeStackItem.MockType.SCALE -> convert(makeScale(timeStackItem), clazz)
        }
    }


    private fun makeTravel(item: TimeStackItem): Temporal {
        val elapsed = when (val originalTime = item.originalTime) {
            is LocalDateTime -> Duration.between(originalTime, LocalDateTime.now())
            is ZonedDateTime -> Duration.between(originalTime.toInstant(), ZonedDateTime.now().toInstant())
            is Instant -> Duration.between(originalTime, Instant.now())
            else -> throw IllegalArgumentException("Unsupported Temporal type")
        }.toSeconds()

        return item.time.plus(elapsed, ChronoUnit.SECONDS)
    }

    private fun makeScale(item: TimeStackItem): Temporal {
        val elapsed = when (val originalTime = item.originalTime) {
            is LocalDateTime -> Duration.between(originalTime, LocalDateTime.now())
            is ZonedDateTime -> Duration.between(originalTime.toInstant(), ZonedDateTime.now().toInstant())
            is Instant -> Duration.between(originalTime, Instant.now())
            else -> throw IllegalArgumentException("Unsupported Temporal type")
        }.toSeconds()

        val scaledElapsed = elapsed * item.scaling
        return item.time.plus(scaledElapsed, ChronoUnit.MINUTES)
    }

    private fun push(
        time: Temporal,
        mockType: TimeStackItem.MockType,
        scale: Long = 0,
        timeUnit: ChronoUnit = ChronoUnit.MINUTES
    ) {
        timeStack.push(TimeStackItem(mockType, time, scale, timeUnit))
    }

    private fun convert(time: Temporal, clazz: Class<*>): Temporal {
        when (clazz) {
            LocalDateTime::class.java -> {
                return when (time) {
                    is LocalDateTime -> time
                    is ZonedDateTime -> time.toLocalDateTime()
                    is Instant -> LocalDateTime.ofInstant(time, ZoneId.systemDefault())
                    else -> throw IllegalArgumentException("Unsupported Temporal type for LocalDateTime conversion")
                }
            }

            ZonedDateTime::class.java -> {
                return when (time) {
                    is LocalDateTime -> time.atZone(ZoneId.systemDefault())
                    is ZonedDateTime -> time
                    is Instant -> time.atZone(ZoneId.systemDefault())
                    else -> throw IllegalArgumentException("Unsupported Temporal type for ZonedDateTime conversion")
                }
            }

            else -> throw IllegalArgumentException("Unsupported class type")
        }
    }


}