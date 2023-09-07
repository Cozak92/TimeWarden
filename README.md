TimeWarden
---
The A Kotlin library offers features for "time travel," "time freezing," and "time acceleration," simplifying the testing of code that's sensitive to time changes. This tool streamlines the process of mocking LocalDateTime, ZonedDateTime, and Instant.

Inspired By [Timecop](https://github.com/travisjeffery/timecop)

---

### Installation


---
### FEATURES

- Freeze time to a specific point in time
- Travel to a specific point in time
- Accelerate time by a given factor (minutes)
- No dependencies. you can use it in any project
- Support for LocalDateTime, ZonedDateTime, and Instant
- Nested calls to `freeze` and `travel` are supported. each block will be executed in the context of the time it specifies

---
### USAGE

#### Freeze Time

```kotlin
LocalDateTime.now().freeze {
    sleep(2000)
    localDateTimeNow() == it // ==> true
}

// or...

TimeWarden.freeze(LocalDateTime.now()) {
    sleep(2000)
    localDateTimeNow() == it // ==> true
}


in Progression..
```
