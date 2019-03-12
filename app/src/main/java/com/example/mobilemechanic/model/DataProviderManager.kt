package com.example.mobilemechanic.model

object DataProviderManager {
    fun getAllStates(): ArrayList<String> {
        return arrayListOf(
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
            "WY"
        )
    }

    fun getAllServices(): ArrayList<String> {
        return arrayListOf(
            "Oil Change",
            "Tire Change",
            "Check Engine Light"
        )
    }

    fun getServicePriceLabel(): ArrayList<String> {
        return arrayListOf("All", "Under $10", "Under $20", "Under $50", "Under $100", "Under $500")
    }

    fun getServicePriceValue(): ArrayList<Double> {
        return arrayListOf(Double.MAX_VALUE, Double.MAX_VALUE, 10.0, 20.0, 50.0, 100.0, 500.0)
    }
}