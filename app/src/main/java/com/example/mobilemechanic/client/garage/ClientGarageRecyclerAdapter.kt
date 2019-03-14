package com.example.mobilemechanic.client.garage

import android.app.Activity
import android.app.Dialog
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
import com.google.firebase.firestore.CollectionReference
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dialog_body_add_vehicle.*
import kotlinx.android.synthetic.main.dialog_container_basic.*
import org.json.JSONArray
import java.util.*

class ClientGarageRecyclerAdapter(val context: Activity, val dataset: ArrayList<Vehicle>) :
    RecyclerView.Adapter<ClientGarageRecyclerAdapter.ViewHolder>(), AdapterView.OnItemSelectedListener {

    private lateinit var basicDialog: Dialog
    private lateinit var vehicleRef: CollectionReference
    private var vehicleBrands: ArrayList<VehicleBrand> = loadVehicleDataJSONFromAssets()
    private lateinit var vehicleModelAdapter: HintSpinnerAdapter
    private lateinit var vehicleModelSpinner: Spinner
    private var allVehicleModel: ArrayList<String> = ArrayList()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val vehicleTitle = itemView.findViewById<TextView>(R.id.id_vehicle_title)
        val vehicleImage = itemView.findViewById<ImageView>(R.id.id_vehicle_image)
        val removeButton = itemView.findViewById<Button>(R.id.id_remove)
        val updateButton = itemView.findViewById<Button>(R.id.id_update)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientGarageRecyclerAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_vehicle, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vehicle = dataset[position]
        holder.vehicleTitle.text = "${vehicle.year} ${vehicle.make} ${vehicle.model}"
        if (!vehicle.photoUrl.isNullOrEmpty()) {
            Picasso.get().load(vehicle.photoUrl).into(holder.vehicleImage)
        }

        holder.removeButton.setOnClickListener {
            Log.d(CLIENT_TAG, "[ClientGarageRecyclerAdapter] remove vehicle")
            // TODO: Remove vehicle here.
            // use document(vehicle.objectID) to tell firestore what document to remove




        }

        holder.updateButton.setOnClickListener {
            val container = context.layoutInflater.inflate(R.layout.dialog_container_basic, null)
            val body = context.layoutInflater.inflate(R.layout.dialog_body_add_vehicle, null)
            val basicDialog = BasicDialog.Builder.apply {
                title = "Update Vehicle"
                positive = "Save"
                negative = "Cancel"
            }.build(context, container, body)

            handleDialogOnClick(basicDialog, vehicle)
            setUpSpinners(basicDialog)
            basicDialog.show()
        }
    }

    private fun handleDialogOnClick(basicDialog: BasicDialog, vehicle: Vehicle) {

        basicDialog.id_negative.setOnClickListener {
            basicDialog.dismiss()
        }

        basicDialog.id_positive.setOnClickListener {
            val year = basicDialog.id_vehicle_year.selectedItem.toString()
            val make = basicDialog.id_vehicle_make.selectedItem.toString()
            val model = basicDialog.id_vehicle_model.selectedItem.toString()
            Log.d(CLIENT_TAG, "[ClientGarageRecyclerAdapter] update dialog $year, $make, $model")
            // TODO: Update vehicle to firestore here.





            basicDialog.dismiss()
        }
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
            HintSpinnerAdapter(context, android.R.layout.simple_spinner_dropdown_item, years, "Year")
    }

    private fun makeSpinner(basicDialog: BasicDialog) {
        basicDialog.id_vehicle_make.onItemSelectedListener = this
        val makes = ArrayList<String>()
        vehicleBrands.forEach {
            makes.add(it.brand)
        }
        basicDialog.id_vehicle_make.adapter =
            HintSpinnerAdapter(context, android.R.layout.simple_spinner_dropdown_item, makes, "Make")
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
            Log.d(CLIENT_TAG, "[GarageActivity] jsonObj $vehicleMakes")
        } catch (ex: Exception) {
            Log.d(CLIENT_TAG, "[GarageActivity] exception ${ex.message}")
        }
        return vehicleMakes
    }

    private fun modelSpinner(basicDialog: BasicDialog) {
        vehicleModelAdapter =
            HintSpinnerAdapter(context, android.R.layout.simple_spinner_dropdown_item, allVehicleModel, "Model")
        vehicleModelSpinner = basicDialog.findViewById(R.id.id_vehicle_model)
        vehicleModelSpinner.adapter = vehicleModelAdapter
    }

    override fun getItemCount() = dataset.size

    override fun onNothingSelected(p0: AdapterView<*>?) {}

    private fun updateVehicleModelSpinner(brand: String) {
        var selectedMake = VehicleBrand()
        vehicleBrands.forEach {
            if (it.brand == brand) {
                selectedMake = it
            }
        }
        allVehicleModel.clear()
        allVehicleModel.addAll(selectedMake.models)
        vehicleModelAdapter = HintSpinnerAdapter(context, android.R.layout.simple_spinner_dropdown_item, allVehicleModel, "Model")
        vehicleModelSpinner.adapter = vehicleModelAdapter
        Log.d(CLIENT_TAG, "[GarageActivity] models $allVehicleModel")
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        Log.d(CLIENT_TAG, "[GarageActivity] make spinner ${parent.selectedItem}")
        updateVehicleModelSpinner(parent.selectedItem.toString())
    }
}