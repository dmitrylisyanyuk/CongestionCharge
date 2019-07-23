package com.congestion.charge.data

import android.support.annotation.IntRange

data class CalcRule(
    val name:String,
    @IntRange(from = 0L,to = 23L) val hoursStart:Int,
    @IntRange(from = 0L,to = 59L) val minutesStart:Int,
    @IntRange(from = 0L,to = 23L) val hoursEnd:Int,
    @IntRange(from = 0L,to = 59L) val minutesEnd:Int,
    val hourRate:Double
    )