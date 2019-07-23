package com.congestion.charge

import com.congestion.charge.data.CalcRule
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import java.util.concurrent.TimeUnit

object Utils {

    fun isSameDay(date1: Calendar, date2: Calendar): Boolean {
        return date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR)
                && date1.get(Calendar.DAY_OF_YEAR) == date2.get(Calendar.DAY_OF_YEAR)
    }

    fun isWeekend(date: Calendar): Boolean {
        val dayOfWeek = date.get(Calendar.DAY_OF_WEEK)
        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY
    }

    fun millisToString(millis: Long): String {
        return String.format(
            "%dh, %dm",
            TimeUnit.MILLISECONDS.toHours(millis),
            TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis))
        )
    }
}

fun CalcRule.toMoneyAmount(millis: Long):Double{
    return (hourRate * (millis / 1000.0 / 60 / 60) * 10).toInt() / 10.0
}

fun Double.toScaledBigDecimal():BigDecimal{
    return toBigDecimal().setScale(2, RoundingMode.DOWN)
}

fun Calendar.endOfDay(): Calendar {
    return (clone() as Calendar).apply {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 9999)
    }
}

fun Calendar.startOfDay(): Calendar {
    return (clone() as Calendar).apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
}