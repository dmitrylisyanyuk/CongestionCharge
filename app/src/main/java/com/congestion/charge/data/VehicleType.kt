package com.congestion.charge.data

enum class VehicleType(private val vehicleName: String) {

    CAR("car"),
    MOTORBIKE("motorbike"),
    VAN("van");

    override fun toString(): String {
        return name
    }

}