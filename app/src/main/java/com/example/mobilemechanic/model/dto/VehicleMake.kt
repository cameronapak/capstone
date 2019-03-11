package com.example.mobilemechanic.model.dto

data class VehicleMake(var brand: String, var models: ArrayList<String>) {

    constructor():this("", ArrayList())
}