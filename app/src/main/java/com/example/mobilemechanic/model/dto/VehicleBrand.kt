package com.example.mobilemechanic.model.dto

data class VehicleBrand(var brand: String, var models: ArrayList<String>) {

    constructor():this("", ArrayList())
}