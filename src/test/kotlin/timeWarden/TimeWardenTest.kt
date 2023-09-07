package timeWarden

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.Thread.sleep
import java.time.LocalDateTime

class TimeWardenTest {

    @Test
    fun `test freeze`() {
        val timeWarden = TimeWarden
        val time = LocalDateTime.now()
        timeWarden.freeze(time) {
            assert(timeWarden.isMocked)
            assert(localDateTimeNow() == time)
        }
        assert(!timeWarden.isMocked)

    }

    @Test
    fun `test undo`(){
        val timeWarden = TimeWarden
        val time = LocalDateTime.now()
        timeWarden.freeze(time)
        timeWarden.travel(time)
        assert(timeWarden.isMocked)
        timeWarden.undo()
        assert(timeWarden.isMocked)
        timeWarden.undo()
        assert(!timeWarden.isMocked)
    }

    @Test
    fun `test travel`(){
        val timeWarden = TimeWarden
        val time = LocalDateTime.now()
        timeWarden.travel(time) {
            assert(timeWarden.isMocked)
            sleep(1000)
            assert(localDateTimeNow() != time)
        }
        assert(!timeWarden.isMocked)
    }

    @Test
    fun `test scale`(){
        val timeWarden = TimeWarden
        val time = LocalDateTime.now()

        timeWarden.scale(time, 60) {
            assert(timeWarden.isMocked)
            sleep(1000)
            println(localDateTimeNow())
            Assertions.assertEquals(localDateTimeNow(), time.plusMinutes(60))
            assert(localDateTimeNow() != time)
        }
        assert(!timeWarden.isMocked)
    }

    @Test
    fun `test scale exception`(){
        val exception = assertThrows<IllegalArgumentException> {
            TimeWarden.scale(LocalDateTime.now(), 0) {}
        }
        Assertions.assertEquals(exception.message, "Scale must be greater than 0")
    }

    @Test
    fun `test stack`(){
        val timeWarden = TimeWarden
        val time = LocalDateTime.now()
        timeWarden.freeze(time) {
            timeWarden.travel(time.minusDays(5)) {
                sleep(1000)
                Assertions.assertEquals(localDateTimeNow(), time.minusDays(5).plusSeconds(1))
                timeWarden.scale(time.plusMinutes(2), 60) {
                    assert(timeWarden.isMocked)
                    sleep(1000)
                    println(localDateTimeNow())
                    Assertions.assertEquals(localDateTimeNow(), time.plusMinutes(62))
                    assert(localDateTimeNow() != time)
                }
                assert(timeWarden.isMocked)
                Assertions.assertEquals(localDateTimeNow(), time.minusDays(5).plusSeconds(2))
            }
            assert(timeWarden.isMocked)
        }
        assert(!timeWarden.isMocked)
    }

    @Test
    fun `test unMock`(){
        val timeWarden = TimeWarden
        val time = LocalDateTime.now()
        timeWarden.freeze(time)
        timeWarden.travel(time)
        timeWarden.scale(time, 60)
        assert(timeWarden.isMocked)
        timeWarden.unMock()
        assert(!timeWarden.isMocked)
    }

    @Test
    fun `test get diff type`(){
        LocalDateTime.now().freeze {
            zonedDateTimeNow()
        }
    }
}