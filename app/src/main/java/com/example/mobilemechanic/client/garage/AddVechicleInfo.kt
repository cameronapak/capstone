package com.example.mobilemechanic.client.garage

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.mobilemechanic.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_vechicle_info.*
import java.util.*

class AddVechicleInfo : AppCompatActivity() {
    var mAuth: FirebaseAuth? = null
    var db: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_vechicle_info)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()


        id_add_button.setOnClickListener {addVechicleInfo() }


        id_cancel_button.setOnClickListener { cancelVechicleInfo() }

        // add years

        var years = arrayListOf<String>()
        years.add("Year")

        var currentYear = Calendar.getInstance().get(Calendar.YEAR)
        for (i in 1950..currentYear) {

            years.add(Integer.toString(i))
        }
        id_add_year.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, years)

        id_add_year.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

            }
        }

        // add make
        var makes= arrayOf("Make","Audi", "BMW", "Ferrari", "Ford", "Honda", "Hyundai", "Infiniti", "Jeep",
            "Kia", "Lexus", "Mazda", "Mitsubishi", "Nissan", "Porshe", "Subaru", "Suzuki", "Toyota",
            "Volkswagen","Volvo")
        id_add_make.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,makes)

        id_add_make.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

            }
        }

        //add model
        var models = arrayOf("Model", "Audi A4", "Audi R8", "BMW 8 Series", "BMW X5", "Ferrari F430", "Ferrari FF",
            "Ford Explorer", "Ford Focus", "Accord", "Civic", "Mazda 3", "Mazda 6", "Nissan Sentra",
            "Nissan Maxima", "Toyota Corolla", "Toyota Camry")

        id_add_model.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, models)

        id_add_model.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

            }
        }


    }
    private fun addVechicleInfo(){

        var selectedYear = id_add_year.selectedItem.toString().toIntOrNull()
        var selectedModel = id_add_model.selectedItem.toString()
        var selectedMake = id_add_make.selectedItem.toString()
       // var vehicle = VechicleInformation(selectedYear, selectedModel, selectedMake)

        if(selectedModel =="Model" || selectedMake=="Make" || selectedYear == null){
            Toast.makeText(this, "Please all items", Toast.LENGTH_SHORT ).show()
        }
        else {

            /* db?.collection("Acounts")?.document(mAuth?.currentUser!!.uid)
            ?.collection("vehicles")?.document()
            ?.set(vehicle)?.addOnSuccessListener {*/
            Toast.makeText(
                this, " Added\n You Selected: " +
                        "$selectedYear\n $selectedModel\n $selectedMake", Toast.LENGTH_SHORT
            ).show()
            /* }
            ?.addOnFailureListener {
                Toast.makeText(this, "Failed to Add", Toast.LENGTH_SHORT).show()
            }*/
        }
    }

    private fun cancelVechicleInfo(){
        val i = Intent(this, GarageActivity::class.java)
        startActivity(i)
    }

}


