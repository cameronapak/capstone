package com.example.mobilemechanic.client.garage

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.mobilemechanic.R
import com.example.mobilemechanic.client.CLIENT_TAG
import com.example.mobilemechanic.model.Vehicle
import com.example.mobilemechanic.model.dto.VehicleBrand
import com.example.mobilemechanic.shared.BasicDialog
import com.example.mobilemechanic.shared.HintSpinnerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dialog_body_add_vehicle.*
import kotlinx.android.synthetic.main.dialog_body_confirmation.*
import kotlinx.android.synthetic.main.dialog_container_basic.*
import org.json.JSONArray
import java.util.*

class ClientGarageRecyclerAdapter(val context: Activity, val dataset: ArrayList<Vehicle>) :
    RecyclerView.Adapter<ClientGarageRecyclerAdapter.ViewHolder>(), AdapterView.OnItemSelectedListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirestore: FirebaseFirestore
    private var mFirestorage: FirebaseStorage = FirebaseStorage.getInstance()
    private var storageBrandsRef: StorageReference

    private var vehicleBrands: ArrayList<VehicleBrand> = loadVehicleDataJSONFromAssets()
    private lateinit var vehicleYearAdapter: HintSpinnerAdapter
    private lateinit var vehicleMakeAdapter: HintSpinnerAdapter
    private lateinit var vehicleModelSpinner: Spinner
    private var vehicleModelAdapter: HintSpinnerAdapter
    private var allVehicleModel: ArrayList<String> = ArrayList()

    init {
        storageBrandsRef = mFirestorage.reference
        vehicleModelAdapter =
            HintSpinnerAdapter(context, android.R.layout.simple_spinner_dropdown_item, allVehicleModel, "Models")
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val vehicleTitle = itemView.findViewById<TextView>(R.id.id_vehicle_title)
        val vehicleImage = itemView.findViewById<ImageView>(R.id.id_vehicle_image)
        val removeButton = itemView.findViewById<Button>(R.id.id_remove)
        val updateButton = itemView.findViewById<Button>(R.id.id_update)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientGarageRecyclerAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_vehicle, parent, false)

        mAuth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()

        view.setOnClickListener {
     
        }

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vehicle = dataset[position]
        holder.vehicleTitle.text = "${vehicle.year} ${vehicle.make} ${vehicle.model}"
        if (!vehicle.photoUrl.isNullOrEmpty()) {
            Picasso.get().load(vehicle.photoUrl).into(holder.vehicleImage)
        }

        holder.removeButton.setOnClickListener {
            removeVehicleDialog(vehicle)
        }

        holder.updateButton.setOnClickListener {
            updateVehicleDialog(vehicle)
        }
    }

    private fun removeVehicleDialog(vehicle: Vehicle){
        val container = context.layoutInflater.inflate(R.layout.dialog_container_basic, null)
        val body = context.layoutInflater.inflate(R.layout.dialog_body_confirmation, null)
        val basicDialog = BasicDialog.Builder.apply {
            title = "Remove Vehicle"
            positive = "Confirm"
            negative = "Cancel"
        }.build(context, container, body)

        basicDialog.id_confirmation_text.text = "Are you sure you want to remove this vehicle?"
        basicDialog.id_target_item.text = "$vehicle"
        handleRemoveDialogOnClick(basicDialog, vehicle)
        basicDialog.show()
    }

    private fun updateVehicleDialog(vehicle: Vehicle) {
        val container = context.layoutInflater.inflate(R.layout.dialog_container_basic, null)
        val body = context.layoutInflater.inflate(R.layout.dialog_body_add_vehicle, null)
        val basicDialog = BasicDialog.Builder.apply {
            title = "Update Vehicle"
            positive = "Save"
            negative = "Cancel"
        }.build(context, container, body)

        handleUpdateDialogOnClick(basicDialog, vehicle)
        basicDialog.show()
    }

    private fun handleRemoveDialogOnClick(basicDialog: BasicDialog, vehicle: Vehicle) {
        basicDialog.id_negative.setOnClickListener {
            basicDialog.dismiss()
        }

        basicDialog.id_positive.setOnClickListener {
            mFirestore.collection("Accounts/${mAuth.currentUser?.uid}/Vehicles")
                .document(vehicle.objectID)
                .delete().addOnSuccessListener {
                    Toast.makeText(context, "Removed successfully", Toast.LENGTH_LONG).show()
                }.addOnFailureListener {
                    Toast.makeText(context, "Removed failed", Toast.LENGTH_LONG).show()
                }
            basicDialog.dismiss()
        }
    }


    private fun handleUpdateDialogOnClick(basicDialog: BasicDialog, vehicle: Vehicle) {
        setUpSpinners(basicDialog, vehicle)

        basicDialog.id_negative.setOnClickListener {
            basicDialog.dismiss()
        }

        basicDialog.id_positive.setOnClickListener {
            val year = basicDialog.id_vehicle_year.selectedItem.toString()
            val make = basicDialog.id_vehicle_make.selectedItem.toString()
            val model = basicDialog.id_vehicle_model.selectedItem.toString()
            Log.d(CLIENT_TAG, "[ClientGarageRecyclerAdapter] update dialog $year, $make, $model")
            val newVehicle = Vehicle(vehicle.objectID, year, make, model, "")

            retrieveVehicleImageUrl(newVehicle)
            basicDialog.dismiss()
        }
    }

    private fun retrieveVehicleImageUrl(newVehicle: Vehicle) {
        storageBrandsRef.child("Brands/${newVehicle.make}/${newVehicle.model}.png")
            .downloadUrl.addOnSuccessListener {
            newVehicle.photoUrl = it.toString()
            Log.d(CLIENT_TAG, "[ClientGarageRecyclerAdapter] newVehicle image uri $it")
            saveVehicleToFirestore(newVehicle)
        }.addOnFailureListener {
            Toast.makeText(context, "No image exist.", Toast.LENGTH_LONG).show()
        }
    }

    private fun saveVehicleToFirestore(newVehicle: Vehicle) {
        mFirestore?.collection("Accounts/${mAuth.currentUser?.uid}/Vehicles")
            .document(newVehicle.objectID)
            .update(
                "year", newVehicle.year,
                "make", newVehicle.make,
                "model", newVehicle.model,
                "photoUrl", newVehicle.photoUrl)
            .addOnSuccessListener {
                Log.d(CLIENT_TAG, "[ClientGarageRecyclerAdapter] updated newVehicle successfully")
                Toast.makeText(context, "Updated successfully", Toast.LENGTH_LONG).show()
            }.addOnFailureListener {
                Log.d(CLIENT_TAG, "[ClientGarageRecyclerAdapter] updated newVehicle fail")
                Toast.makeText(context, "Updated fail", Toast.LENGTH_LONG).show()
            }

    }

    private fun setUpSpinners(basicDialog: BasicDialog, vehicle: Vehicle) {
        yearSpinner(basicDialog, vehicle)
        makeSpinner(basicDialog, vehicle)
        modelSpinner(basicDialog, vehicle)
    }

    private fun yearSpinner(basicDialog: BasicDialog, vehicle: Vehicle) {
        val years = ArrayList<String>()
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        for (year in currentYear downTo MIN_YEAR) {
            years.add(year.toString())
        }
        vehicleYearAdapter =
            HintSpinnerAdapter(context, android.R.layout.simple_spinner_dropdown_item, years, "Year")
        basicDialog.id_vehicle_year.adapter = vehicleYearAdapter

        val yearPosition = vehicleYearAdapter.getPosition(vehicle.year)
        basicDialog.id_vehicle_year.setSelection(yearPosition)
    }

    private fun makeSpinner(basicDialog: BasicDialog, vehicle: Vehicle) {
        basicDialog.id_vehicle_make.onItemSelectedListener = this
        val makes = ArrayList<String>()
        vehicleBrands.forEach {
            makes.add(it.brand)
        }
        vehicleMakeAdapter =
            HintSpinnerAdapter(context, android.R.layout.simple_spinner_dropdown_item, makes, "Make")
        basicDialog.id_vehicle_make.adapter = vehicleMakeAdapter

        val makePosition = vehicleMakeAdapter.getPosition(vehicle.make)
        basicDialog.id_vehicle_make.setSelection(makePosition)
    }

    private fun modelSpinner(basicDialog: BasicDialog, vehicle: Vehicle) {
        updateModelByBrandName(vehicle.make)
        vehicleModelSpinner = basicDialog.findViewById(R.id.id_vehicle_model)
        vehicleModelSpinner.adapter = vehicleModelAdapter
        var modelPosition = vehicleModelAdapter.getPosition(vehicle.model)
        Log.d(CLIENT_TAG, "[ClientGarageRecyclerAdapter] model position $modelPosition")
        vehicleModelSpinner.setSelection(modelPosition)
    }

    private fun loadVehicleDataJSONFromAssets(): ArrayList<VehicleBrand> {
        var vehicleMakes = ArrayList<VehicleBrand>()
        val gson = Gson()
        try {
            val inputStream = context.assets.open("jsonData/vehicles.json")
            val buffer = ByteArray(inputStream.available())
            inputStream.read(buffer)
            inputStream.close()

            val text = String(buffer)
            val jsonArray = JSONArray(text)
            val type = object : TypeToken<ArrayList<VehicleBrand>>() {}.type
            vehicleMakes = gson.fromJson(jsonArray.toString(), type)
            Log.d(CLIENT_TAG, "[ClientGarageRecyclerAdapter] jsonObj $vehicleMakes")
        } catch (ex: Exception) {
            Log.d(CLIENT_TAG, "[ClientGarageRecyclerAdapter] exception ${ex.message}")
        }
        return vehicleMakes
    }

    override fun getItemCount() = dataset.size

    override fun onNothingSelected(p0: AdapterView<*>?) {}

    private fun updateVehicleModelSpinner(brand: String) {
        updateModelByBrandName(brand)
        vehicleModelAdapter.notifyDataSetChanged()
        Log.d(CLIENT_TAG, "[ClientGarageRecyclerAdapter] models $allVehicleModel")
    }

    private fun updateModelByBrandName(brand: String) {
        vehicleBrands.forEach {
            if (it.brand == brand) {
                allVehicleModel.clear()
                allVehicleModel.add("Models")
                allVehicleModel.addAll(it.models)
                return@forEach
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        Log.d(CLIENT_TAG, "[ClientGarageRecyclerAdapter] make spinner ${parent.selectedItem}")
        updateVehicleModelSpinner(parent.selectedItem.toString())
    }
}