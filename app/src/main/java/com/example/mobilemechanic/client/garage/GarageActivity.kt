package com.example.mobilemechanic.client.garage

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import com.example.mobilemechanic.R
import com.example.mobilemechanic.client.CLIENT_TAG
import com.example.mobilemechanic.model.Vehicle
import com.example.mobilemechanic.model.dto.VehicleBrand
import com.example.mobilemechanic.shared.BasicDialog
import com.example.mobilemechanic.shared.HintSpinnerAdapter
import com.example.mobilemechanic.shared.utility.ScreenManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
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
    private lateinit var mFirestorage: FirebaseStorage
    private lateinit var storageBrandsRef: StorageReference

    private lateinit var vehicleRef: CollectionReference
    private lateinit var vehicleBrands: ArrayList<VehicleBrand>
    private lateinit var vehicleModelAdapter: HintSpinnerAdapter
    private lateinit var vehicleModelSpinner: Spinner
    private var allVehicleModel: ArrayList<String> = ArrayList()

    private lateinit var viewManager: LinearLayoutManager
    private lateinit var garageRecyclerAdapter: ClientGarageRecyclerAdapter
    private var ownedVehicles = ArrayList<Vehicle>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()
        mFirestorage = FirebaseStorage.getInstance()
        storageBrandsRef = mFirestorage.reference

        vehicleRef = mFirestore.collection("Accounts/${mAuth.currentUser?.uid}/Vehicles")
        Log.d(CLIENT_TAG, "[GarageActivity] User uid: ${mAuth?.currentUser?.uid}")
        Log.d(CLIENT_TAG, "[GarageActivity] User email: ${mAuth?.currentUser?.email}")
        setContentView(com.example.mobilemechanic.R.layout.activity_garage)
        setUpGarageActivity()
    }

    private fun setUpGarageActivity() {
        loadVehicleBrandsFromJSONAsset()
        setUpActionBar()
        setUpOnAddVehicle()
        setUpRecyclerVehicle()
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

    private fun setUpRecyclerVehicle() {
        viewManager = LinearLayoutManager(this)
        garageRecyclerAdapter = ClientGarageRecyclerAdapter(this, ownedVehicles)
        id_garage_recyclerview.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = garageRecyclerAdapter
        }
        reactiveGarageRecyclerView()
    }

    private fun loadVehicleBrandsFromJSONAsset() {
        vehicleBrands = loadVehicleDataJSONFromAssets()
    }

    private fun reactiveGarageRecyclerView() {
        mFirestore.collection("Accounts/${mAuth.currentUser?.uid}/Vehicles")
            .addSnapshotListener { querySnapshot, exception ->
                if (exception != null) {
                    Log.w(CLIENT_TAG, exception.message)
                    return@addSnapshotListener
                }
                ownedVehicles.clear()
                for (doc in querySnapshot!!) {
                    val vehicle = doc.toObject(Vehicle::class.java)
                    Log.d(CLIENT_TAG, "[GarageActivity] owned vehicle $vehicle")
                    ownedVehicles.add(vehicle)
                }
                garageRecyclerAdapter.notifyDataSetChanged()
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
        for (year in currentYear downTo MIN_YEAR) {
            years.add(year.toString())
        }
        basicDialog.id_vehicle_year.adapter =
            HintSpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, years, "Year")
    }

    private fun makeSpinner(basicDialog: BasicDialog) {
        basicDialog.id_vehicle_make.onItemSelectedListener = this
        val makes = ArrayList<String>()
        vehicleBrands.forEach {
            makes.add(it.brand)
        }
        basicDialog.id_vehicle_make.adapter =
            HintSpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, makes, "Make")
    }

    private fun modelSpinner(basicDialog: BasicDialog) {
        vehicleModelAdapter =
            HintSpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, allVehicleModel, "Model")
        vehicleModelSpinner = basicDialog.findViewById(R.id.id_vehicle_model)
        vehicleModelSpinner.adapter = vehicleModelAdapter
    }

    private fun handleDialogClick(basicDialog: BasicDialog) {
        basicDialog.id_positive.setOnClickListener {
            retrieveVehicleImageUrlAndSaveToFirestore(basicDialog)
        }

        basicDialog.id_negative.setOnClickListener {
            basicDialog.dismiss()
        }
    }

    private fun retrieveVehicleImageUrlAndSaveToFirestore(basicDialog: BasicDialog) {
        val year = basicDialog.id_vehicle_year.selectedItem.toString()
        val make = basicDialog.id_vehicle_make.selectedItem.toString()
        val model = basicDialog.id_vehicle_model.selectedItem.toString()
        val vehicle = Vehicle("", year, make, model, "")

        if (isFilled(basicDialog)) {
            retrieveVehicleImageUrl(vehicle)
            basicDialog.dismiss()
        } else {
            Toast.makeText(this, "Please fill all information", Toast.LENGTH_LONG).show()
        }

    }

    private fun retrieveVehicleImageUrl(vehicle: Vehicle) {
        storageBrandsRef.child("Brands/${vehicle.make}/${vehicle.model}.png")
            .downloadUrl.addOnSuccessListener {
            vehicle.photoUrl = it.toString()
            Log.d(CLIENT_TAG, "[GarageActivity] vehicle image uri $it")
            saveVehicleToFirestore(vehicle)
        }.addOnFailureListener {
            Toast.makeText(this, "No image exist.", Toast.LENGTH_LONG).show()
        }
    }

    private fun saveVehicleToFirestore(vehicle: Vehicle) {
            val vehicleDoc = vehicleRef.document()
            vehicle.objectID = vehicleDoc.id
            Log.d(CLIENT_TAG, "[GarageActivity] vehicle docId ${vehicle.objectID}")
            vehicleDoc.set(vehicle)
                ?.addOnSuccessListener {
                    Toast.makeText(this, "Vehicle added successfuly", Toast.LENGTH_LONG).show()
                }?.addOnFailureListener {
                    Toast.makeText(this, "Unable to add vehicle", Toast.LENGTH_LONG).show()
                }
    }

    private fun loadVehicleDataJSONFromAssets(): ArrayList<VehicleBrand> {
        var vehicleMakes = ArrayList<VehicleBrand>()
        val gson = Gson()
        try {
            val inputStream = assets.open("jsonData/vehicles.json")
            val buffer = ByteArray(inputStream.available())
            inputStream.read(buffer)
            inputStream.close()

            val text = String(buffer)
            val jsonArray = JSONArray(text)
            val type = object : TypeToken<ArrayList<VehicleBrand>>() {}.type
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
        var selectedMake = VehicleBrand()
        vehicleBrands.forEach {
            if (it.brand == brand) {
                selectedMake = it
            }
        }
        allVehicleModel.clear()
        allVehicleModel.addAll(selectedMake.models)
        vehicleModelAdapter = HintSpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, allVehicleModel, "Model")
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
