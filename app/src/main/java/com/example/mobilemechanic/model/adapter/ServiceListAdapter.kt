package com.example.mobilemechanic.model.adapter

import android.app.Activity
import android.app.Dialog
import android.app.Service
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.mobilemechanic.R
import com.example.mobilemechanic.mechanic.MECHANIC_TAG
import com.example.mobilemechanic.model.DataProviderManager
import com.example.mobilemechanic.model.algolia.ServiceModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.example.mobilemechanic.shared.BasicDialog
import com.example.mobilemechanic.shared.HintSpinnerAdapter
import kotlinx.android.synthetic.main.dialog_body_add_service.*
import kotlinx.android.synthetic.main.dialog_body_add_service.view.*
import kotlinx.android.synthetic.main.dialog_container_basic.*


class ServiceListAdapter(var context: Activity, var serviceArray: ArrayList<ServiceModel>) :
    RecyclerView.Adapter<ServiceListAdapter.ViewHolder>() {
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var serviceRef: CollectionReference
    private lateinit var basicDialog: Dialog

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceListAdapter.ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.recyclerview_item_service_manage, parent, false)
        mFirestore = FirebaseFirestore.getInstance()
        serviceRef = mFirestore?.collection("Services")
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return serviceArray.size
    }

    override fun onBindViewHolder(holder: ServiceListAdapter.ViewHolder, position: Int) {
        holder.bindItem(position)
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        fun bindItem(position: Int) {
            val serviceItem = serviceArray[position]
            Log.d(MECHANIC_TAG, "[ServiceListAdapter] objectID: ${serviceItem.objectID}")
            val serviceType = itemView.findViewById<TextView>(R.id.id_service_type)
            val price = itemView.findViewById<TextView>(R.id.id_price)
            val description = itemView.findViewById<TextView>(R.id.id_description)
            val updateBtn = itemView.findViewById<Button>(R.id.id_button_update)
            val removeBtn = itemView.findViewById<Button>(R.id.id_button_remove)

            serviceType.text = serviceItem.serviceType
            price.text = serviceItem.price.toString()
            description.text = serviceItem.description

            removeBtn.setOnClickListener {
                removeDialog(serviceItem)
            }

            updateBtn.setOnClickListener {
                updateDialog(serviceItem)
            }
        }
    }

    private fun removeDialog(serviceItem: ServiceModel) {
        val dialogContainer =
            context.layoutInflater.inflate(com.example.mobilemechanic.R.layout.dialog_container_basic, null)
        val dialogBody =
            context.layoutInflater.inflate(com.example.mobilemechanic.R.layout.dialog_body_remove_service, null)


        basicDialog = BasicDialog.Builder.apply {
            title = "Add Service"
            positive = "Add"
            negative = "Cancel"
        }.build(context, dialogContainer, dialogBody)
        basicDialog.show()
        basicDialog.id_positive.setOnClickListener {
            Log.d(MECHANIC_TAG, "[ServiceListAdapter] remove ${serviceItem.objectID}")
            serviceRef.document("${serviceItem.objectID}").delete().addOnSuccessListener {
                Toast.makeText(context, "Service Removed Successfully", Toast.LENGTH_LONG).show()
            }?.addOnFailureListener {
                Toast.makeText(context, "Failed to remove service", Toast.LENGTH_LONG).show()
                Log.w(MECHANIC_TAG, "Error deleting services", it)
            }
        }
        basicDialog.id_negative.setOnClickListener {
            basicDialog.dismiss()
        }

    }

    private fun updateDialog(serviceItem: ServiceModel) {
        val dialogContainer =
            context.layoutInflater.inflate(com.example.mobilemechanic.R.layout.dialog_container_basic, null)
        val dialogBody =
            context.layoutInflater.inflate(com.example.mobilemechanic.R.layout.dialog_body_add_service, null)

        val service = DataProviderManager.getAllServices()
        dialogBody.add_service_spinner.adapter =
            HintSpinnerAdapter(context, android.R.layout.simple_spinner_dropdown_item, service)
        this.basicDialog = BasicDialog.Builder.apply {
            title = "Update Service"
            positive = "Update"
            negative = "Cancel"
        }.build(context, dialogContainer, dialogBody)
        basicDialog.show()
        basicDialog.id_positive.setOnClickListener {
            val serviceType = basicDialog.add_service_spinner.selectedItem.toString().trim()
            val priceUpdate = basicDialog.label_price.text.toString().trim()
            val descriptionUpdate = basicDialog.label_comment.text.toString().trim()

            serviceRef.document("${serviceItem.objectID}")
                .update("serviceType", serviceType, "price",
                    parseDouble(priceUpdate), "description", descriptionUpdate)
                .addOnSuccessListener {
                Toast.makeText(context, "Service Update Successfully", Toast.LENGTH_LONG).show()
            }?.addOnFailureListener {
                Toast.makeText(context, "Failed to Updadate", Toast.LENGTH_LONG).show()
                Log.w(MECHANIC_TAG, "Error update services", it)
            }
            basicDialog.dismiss()
        }
        basicDialog.id_negative.setOnClickListener {
            basicDialog.dismiss()
        }
    }

    private fun parseDouble(strNumber: String?): Double {
        return if (strNumber != null && strNumber.isNotEmpty()) {
            strNumber.toDouble()
        } else {
            0.0
        }
    }
}

