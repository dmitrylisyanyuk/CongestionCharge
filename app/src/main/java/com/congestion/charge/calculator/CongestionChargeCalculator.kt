package com.congestion.charge.calculator

import com.congestion.charge.Utils
import com.congestion.charge.data.CalcRule
import com.congestion.charge.data.CalculationTime
import com.congestion.charge.data.VehicleType
import com.congestion.charge.endOfDay
import com.congestion.charge.startOfDay
import java.util.*

class CongestionChargeCalculator(private val calcRules: Map<VehicleType, List<CalcRule>>) {

    fun calculate(vehicleType: VehicleType, startDate: Calendar, endDate: Calendar): List<CalculationTime> {
        if (startDate.timeInMillis >= endDate.timeInMillis) {
            throw IllegalStateException("Start date can not be later than the end date")
        }
        val rules = calcRules[vehicleType] ?: throw IllegalStateException("not supported vehicleType")
        val calculationTime = rules.map { CalculationTime(rule = it) }
        if (Utils.isSameDay(startDate, endDate)) {
            //calculate in one day
            if (!Utils.isWeekend(startDate)) {
                calculationTime.forEach { calcDayCharge(it, startDate, endDate) }
            }
        } else {
            //calculate first day
            if (!Utils.isWeekend(startDate)) {
                calculationTime.forEach {
                    calcDayCharge(it, startDate, startDate.endOfDay())
                }
            }
            //calculate full days between first and last
            val current = startDate.clone() as Calendar
            current.add(Calendar.DAY_OF_MONTH, 1)
            while (!Utils.isSameDay(current, endDate)) {
                if (!Utils.isWeekend(current)) {
                    calculationTime.forEach {
                        it.timeInMillis += Math.max(it.rule.hoursEnd - it.rule.hoursStart, 0) * 60 * 60 * 1000
                        it.timeInMillis += (it.rule.minutesEnd - it.rule.minutesStart) * 60 * 1000
                    }
                }
                current.add(Calendar.DAY_OF_MONTH, 1)
            }
            //calculate last day
            if (!Utils.isWeekend(endDate)) {
                calculationTime.forEach {
                    calcDayCharge(it, endDate.startOfDay(), endDate)
                }
            }
        }
        return calculationTime
    }

    private fun calcDayCharge(calculationTime: CalculationTime, startDate: Calendar, endDate: Calendar) {
        val startInterval = (startDate.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, calculationTime.rule.hoursStart)
            set(Calendar.MINUTE, calculationTime.rule.minutesStart)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val endInterval = (startDate.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, calculationTime.rule.hoursEnd)
            set(Calendar.MINUTE, calculationTime.rule.minutesEnd)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        calculationTime.timeInMillis += Math.max(Math.min(endInterval.timeInMillis, endDate.timeInMillis)
                - Math.max(startInterval.timeInMillis, startDate.timeInMillis), 0)
    }
}