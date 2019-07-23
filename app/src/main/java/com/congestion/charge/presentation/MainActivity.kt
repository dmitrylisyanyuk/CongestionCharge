package com.congestion.charge.presentation

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.congestion.charge.*
import com.congestion.charge.calculator.CongestionChargeCalculator
import com.congestion.charge.data.CalcRule
import com.congestion.charge.data.VehicleType
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.IllegalStateException
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val carAmRule = CalcRule("AM",7,0,12,0,2.0)
    private val carPmRule = CalcRule("PM",12,0,19,0,2.5)
    private val motorbikeAmRule = CalcRule("AM",7,0,12,0,1.0)
    private val motorbikePmRule = CalcRule("PM",12,0,19,0,1.0)

    private val calcRules = mapOf(
        VehicleType.CAR to listOf(carAmRule,carPmRule),
        VehicleType.MOTORBIKE to listOf(motorbikeAmRule,motorbikePmRule),
        VehicleType.VAN to listOf(carAmRule,carPmRule)
    )

    private val congestionChargeCalculator = CongestionChargeCalculator(calcRules)

    var startDate:Calendar? = null

    var finishDate:Calendar? = null

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        vehicleTypeSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, VehicleType.values())
        txtStartDate.setOnClickListener {
            txtResult.text = null
            pickDateAndTime {
                startDate = it
                txtStartDate.text = SimpleDateFormat("d/MM/yyyy HH:mm").format(it.time)
            }
        }
        txtFinishDate.setOnClickListener {
            txtResult.text = null
            pickDateAndTime {
                finishDate = it
                txtFinishDate.text = SimpleDateFormat("d/MM/yyyy HH:mm").format(it.time)
            }
        }
        button.setOnClickListener {
            txtResult.text = null
            try {
                if (startDate == null || finishDate == null){
                    throw IllegalStateException("Date not set")
                }
                val result = congestionChargeCalculator.calculate(
                    VehicleType.valueOf(vehicleTypeSpinner.selectedItem.toString()),
                    startDate!!,
                    finishDate!!
                )
                txtResult.text = result.joinToString("\n\n"){
                    "Charge for ${Utils.millisToString(it.timeInMillis)} " +
                            "(${it.rule.name} rate): " +
                            "£${it.rule.toMoneyAmount(it.timeInMillis).toScaledBigDecimal()}"
                } + "\n\nTotal Charge: £${result.sumByDouble { it.rule.toMoneyAmount(it.timeInMillis) }.toScaledBigDecimal()}"
            } catch (e: Exception) {
                Toast.makeText(this,e.message,Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun pickDateAndTime(callback:(Calendar)->Unit){
        val calendar = Calendar.getInstance()
        DatePickerDialog(this,{_, year, month, dayOfMonth ->
            TimePickerDialog(this,{_, hourOfDay, minute ->
                callback.invoke(Calendar.getInstance().apply {
                    set(Calendar.YEAR,year)
                    set(Calendar.MONTH,month)
                    set(Calendar.DAY_OF_MONTH,dayOfMonth)
                    set(Calendar.HOUR_OF_DAY,hourOfDay)
                    set(Calendar.MINUTE,minute)
                    set(Calendar.SECOND,0)
                    set(Calendar.MILLISECOND,0)
                })
            },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true).show()
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show()
    }
}
