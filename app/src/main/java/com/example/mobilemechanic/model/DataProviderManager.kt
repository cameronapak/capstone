package com.example.mobilemechanic.model

object DataProviderManager {
    fun getAllStates(): ArrayList<String> {
       return arrayListOf("State",
           "AK",
           "AL",
           "AR",
           "AS",
           "AZ",
           "CA",
           "CO",
           "CT",
           "DC",
           "DE",
           "FL",
           "GA",
           "GU",
           "HI",
           "IA",
           "ID",
           "IL",
           "IN",
           "KS",
           "KY",
           "LA",
           "MA",
           "MD",
           "ME",
           "MI",
           "MN",
           "MO",
           "MS",
           "MT",
           "NC",
           "ND",
           "NE",
           "NH",
           "NJ",
           "NM",
           "NV",
           "NY",
           "OH",
           "OK",
           "OR",
           "PA",
           "PR",
           "RI",
           "SC",
           "SD",
           "TN",
           "TX",
           "UT",
           "VA",
           "VI",
           "VT",
           "WA",
           "WI",
           "WV",
           "WY")
    }

    fun getAllServices(): ArrayList<String> {
        return arrayListOf("Services",
            "Oil Change",
            "Tire Change",
            "Check Engine Light"
            )
    }

    fun getAllVehicleMake() : ArrayList<String> {
        return arrayListOf("Make",
            "Toyota")
    }

    fun getToyotaModel() : ArrayList<String> {
        return arrayListOf("Venza", "Camry", "Avalon")
    }

    fun getHondaModel() : ArrayList<String> {
        return arrayListOf("Accord", "Civic", "Insight")
    }
}