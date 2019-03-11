package com.example.mobilemechanic.client.garage

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import com.example.mobilemechanic.R
import com.example.mobilemechanic.client.CLIENT_TAG
import com.example.mobilemechanic.model.Vehicle
import com.example.mobilemechanic.model.dto.VehicleMake
import com.example.mobilemechanic.shared.BasicDialog
import com.example.mobilemechanic.shared.HintSpinnerAdapter
import com.example.mobilemechanic.shared.utility.ScreenManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_garage.*
import kotlinx.android.synthetic.main.dialog_body_add_vehicle.*
import kotlinx.android.synthetic.main.dialog_container_basic.*
import org.json.JSONArray
import java.util.*
import kotlin.collections.ArrayList


const val MIN_YEAR = 1950

class GarageActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var vehicleRef: CollectionReference
    private lateinit var allVehicleMaker: ArrayList<VehicleMake>
    private lateinit var vehicleModelAdapter: HintSpinnerAdapter
    private lateinit var vehicleModelSpinner: Spinner
    private var allVehicleModel: ArrayList<String> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()
        vehicleRef = mFirestore.collection("Accounts/${mAuth.currentUser?.uid}/Vehicles")
        Log.d(CLIENT_TAG, "[GarageActivity] User uid: ${mAuth?.currentUser?.uid}")
        Log.d(CLIENT_TAG, "[GarageActivity] User email: ${mAuth?.currentUser?.email}")
        setContentView(com.example.mobilemechanic.R.layout.activity_garage)
        setUpGarageActivity()
    }

    private fun setUpGarageActivity() {
        allVehicleMaker = loadVehicleJSONFromAssets()
        setUpActionBar()
        setUpOnAddVehicle()
    }

    private fun setUpActionBar() {
        setSupportActionBar(id_garage_toolbar as Toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.apply {
            title = "Garage"
            subtitle = "Manage your vehicles"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setUpOnAddVehicle() {
        id_add_vehicle.setOnClickListener {
            openAddVehicleDialog()
        }
    }

    private fun openAddVehicleDialog() {
        val dialogContainer = layoutInflater.inflate(com.example.mobilemechanic.R.layout.dialog_container_basic, null)
        val dialogBody = layoutInflater.inflate(com.example.mobilemechanic.R.layout.dialog_body_add_vehicle, null)
        val basicDialog = BasicDialog.Builder.apply {
            title = "Add"
            positive = "Add"
            negative = "Cancel"
        }.build(this, dialogContainer, dialogBody)

        setUpSpinners(basicDialog)
        handleDialogClick(basicDialog)
        basicDialog.show()
    }

    private fun setUpSpinners(basicDialog: BasicDialog) {
        yearSpinner(basicDialog)
        makeSpinner(basicDialog)
        modelSpinner(basicDialog)
    }

    private fun yearSpinner(basicDialog: BasicDialog) {
        val years = ArrayList<String>()
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        years.add("Year")
        for (year in currentYear downTo MIN_YEAR) {
            years.add(year.toString())
        }
        basicDialog.id_vehicle_year.adapter =
            HintSpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, years)
    }

    private fun makeSpinner(basicDialog: BasicDialog) {
        basicDialog.id_vehicle_make.onItemSelectedListener = this
        val makes = ArrayList<String>()
        makes.add("Make")
        allVehicleMaker.forEach {
            makes.add(it.brand)
        }
        basicDialog.id_vehicle_make.adapter =
            HintSpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, makes)
    }

    private fun modelSpinner(basicDialog: BasicDialog) {
        allVehicleModel.add("Model")
        vehicleModelAdapter = HintSpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, allVehicleModel)
        vehicleModelSpinner = basicDialog.findViewById(R.id.id_vehicle_model)
        vehicleModelSpinner.adapter = vehicleModelAdapter
    }

    private fun handleDialogClick(basicDialog: BasicDialog) {
        basicDialog.id_positive.setOnClickListener {
            addVehicleToFirestore(basicDialog)
        }

        basicDialog.id_negative.setOnClickListener {
            basicDialog.dismiss()
        }
    }

    private fun addVehicleToFirestore(basicDialog: BasicDialog) {
        val year = basicDialog.id_vehicle_year.selectedItem.toString()
        val make = basicDialog.id_vehicle_make.selectedItem.toString()
        val model = basicDialog.id_vehicle_model.selectedItem.toString()
        val vehicle = Vehicle("", year, make, model, "")

        if (isFilled(basicDialog)) {
            vehicleRef.document().set(vehicle)
                ?.addOnSuccessListener {
                    Toast.makeText(this, "Vehicle added successfuly", Toast.LENGTH_LONG).show()
                }?.addOnFailureListener {
                    Toast.makeText(this, "Unable to add vehicle", Toast.LENGTH_LONG).show()
                }
            basicDialog.dismiss()
        } else {
            Toast.makeText(this, "Please fill all information", Toast.LENGTH_LONG).show()
        }

    }

    private fun loadVehicleJSONFromAssets(): ArrayList<VehicleMake> {
        var vehicleMakes = ArrayList<VehicleMake>()
        val gson = Gson()
        try {
            val inputStream = assets.open("jsonData/vehicles.json")
            val buffer = ByteArray(inputStream.available())
            inputStream.read(buffer)
            inputStream.close()

            val text = String(buffer)
            val jsonArray = JSONArray(text)
            val type = object : TypeToken<ArrayList<VehicleMake>>() {}.type
            vehicleMakes = gson.fromJson(jsonArray.toString(), type)

            Log.d(CLIENT_TAG, "[GarageActivity] jsonObj $vehicleMakes")
        } catch (ex: Exception) {
            Log.d(CLIENT_TAG, "[GarageActivity] exception ${ex.message}")
        }
        return vehicleMakes
    }

    private fun isFilled(basicDialog: BasicDialog): Boolean {
        if (basicDialog.id_vehicle_year.selectedItemPosition == 0 ||
            basicDialog.id_vehicle_make.selectedItemPosition == 0 ||
            basicDialog.id_vehicle_model.selectedItemPosition == 0
        ) {
            return false
        }
        return true
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        Log.d(CLIENT_TAG, "[GarageActivity] make spinner ${parent.selectedItem}")
        updateVehicleModelSpinner(parent.selectedItem.toString())
    }

    private fun updateVehicleModelSpinner(brand: String) {
        allVehicleMaker.forEach {
            if (it.brand == brand) {
                allVehicleModel.clear()
                allVehicleModel.add("Model")
                allVehicleModel = it.models
            }
        }
        vehicleModelAdapter = HintSpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, allVehicleModel)
        vehicleModelSpinner.adapter = vehicleModelAdapter
        Log.d(CLIENT_TAG, "[GarageActivity] models $allVehicleModel")
    }

    override fun onResume() {
        super.onResume()
        ScreenManager.hideStatusAndBottomNavigationBar(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
