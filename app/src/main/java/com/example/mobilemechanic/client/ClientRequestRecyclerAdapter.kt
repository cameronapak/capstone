package com.example.mobilemechanic.client

import android.app.Activity
import android.app.Dialog
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.mobilemechanic.R
import com.example.mobilemechanic.model.Request
import com.example.mobilemechanic.model.Status
import com.example.mobilemechanic.shared.BasicDialog
import com.example.mobilemechanic.shared.utility.DateTimeManager
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.dialog_container_basic.*
import java.util.*

class ClientRequestRecyclerAdapter(val context: Activity, val dataset: ArrayList<Request>) :
    RecyclerView.Adapter<ClientRequestRecyclerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.id_mechanic_name)
        val profileImage = itemView.findViewById<CircleImageView>(R.id.id_profile_image)
        val requestTimeStamp = itemView.findViewById<TextView>(R.id.id_request_status_time_stamp)
        val serviceType = itemView.findViewById<TextView>(R.id.id_service_type)
        val requestStatus = itemView.findViewById<TextView>(R.id.id_request_status)
        val primaryButton = itemView.findViewById<Button>(R.id.id_primary_btn)
        val secondaryButton = itemView.findViewById<Button>(R.id.id_secondary_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientRequestRecyclerAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_item_client_request, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val request = dataset[position]
        val mechanicFirstName = request.mechanicInfo?.basicInfo?.firstName
        val mechanicLastName = request.mechanicInfo?.basicInfo?.lastName
        var timestamp = ""

        if (request.status == Status.Request) {
            timestamp = "Requsted on ${DateTimeManager.millisToDate(request.postedOn, "MMM d, y")}"
        } else if (request.status == Status.Active) {
            timestamp = "Accepted on ${DateTimeManager.millisToDate(request.acceptedOn, "MMM d, y")}"
        }

        holder.name.text = "$mechanicFirstName $mechanicLastName"
        holder.serviceType.text = "${request.service?.serviceType} for ${request.vehicle}."
        holder.requestTimeStamp.text = timestamp
        holder.requestStatus.text = request.status?.name

        val mechanicPhoto = request.mechanicInfo?.basicInfo?.photoUrl
        if (mechanicPhoto.isNullOrEmpty() || mechanicPhoto.isNullOrEmpty()) {
            Picasso.get().load(R.drawable.ic_circle_profile).into(holder.profileImage)
            Log.d(CLIENT_TAG, "[ClientRequestRecyclerAdapter] no photoUrl")
        } else {
            Log.d(CLIENT_TAG, "[ClientRequestRecyclerAdapter] photoUrl $mechanicPhoto")
            Picasso.get().load(Uri.parse(mechanicPhoto)).into(holder.profileImage)
        }

        if (request.status == Status.Request) {
            holder.requestStatus.text = "Pending"
            holder.primaryButton.text = "Cancel"
            holder.secondaryButton.visibility = View.GONE

        } else if (request.status == Status.Active) {
            holder.requestStatus.text = "Active"
            holder.primaryButton.text = "Pay"
            holder.secondaryButton.text = "Cancel"
            holder.secondaryButton.visibility = View.VISIBLE
        }

        holder.primaryButton.setOnClickListener {
            handlePrimaryButtonClick(request)
        }

        holder.secondaryButton.setOnClickListener {
            handleSecondaryButtonClick(request)
        }
    }

    private fun handlePrimaryButtonClick(request: Request) {
        if (request.status == Status.Request) {
            val container = context.layoutInflater.inflate(R.layout.dialog_container_basic, null)
            val body = context.layoutInflater.inflate(R.layout.dialog_body_confirmation, null)
            val basicDialog = BasicDialog.Builder.apply {
                title = "Cancel Request"
                positive = "Confirm"
                negative = "Cancel"
            }.build(context, container, body)
            basicDialog.show()

            handleDialogClick(basicDialog)
        }

        if (request.status == Status.Active) {
            // TODO: Go to payment activity

        }

    }

    private fun handleSecondaryButtonClick(request: Request) {
        if (request.status == Status.Active) {
            val container = context.layoutInflater.inflate(R.layout.dialog_container_basic, null)
            val body = context.layoutInflater.inflate(R.layout.dialog_body_confirmation, null)
            val basicDialog = BasicDialog.Builder.apply {
                title = "Cancel Request"
                positive = "Confirm"
                negative = "Cancel"
            }.build(context, container, body)
            basicDialog.show()
        }
    }

    private fun handleDialogClick(basicDialog: Dialog) {
        basicDialog.id_positive.setOnClickListener {
            Log.d(CLIENT_TAG, "[ClientRequestRecyclerAdapter] confirm cancel request")
            // TODO: cancel request

            basicDialog.dismiss()
        }

        basicDialog.id_negative.setOnClickListener {
            basicDialog.dismiss()
        }
    }

    override fun getItemCount() = dataset.size
}