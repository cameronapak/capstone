package com.example.mobilemechanic.model.adapter

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.example.mobilemechanic.R
import com.example.mobilemechanic.mechanic.EXTRA_REQUEST
import com.example.mobilemechanic.mechanic.map.MechanicManageJobActivity
import com.example.mobilemechanic.mechanic.map.MechanicMoreInformationActivity
import com.example.mobilemechanic.model.Request
import com.example.mobilemechanic.model.Status
import com.example.mobilemechanic.shared.BasicDialog
import com.example.mobilemechanic.shared.Toasty
import com.example.mobilemechanic.shared.ToastyType
import com.example.mobilemechanic.shared.utility.AddressManager
import com.example.mobilemechanic.shared.utility.ScreenManager
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_mechanic_welcome.*
import kotlinx.android.synthetic.main.dialog_container_basic.*
import java.text.SimpleDateFormat
import java.util.*


class RequestListAdapter(var context: Activity, var requests: ArrayList<Request>) :
    RecyclerView.Adapter<RequestListAdapter.ViewHolder>() {

    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var requestRef: CollectionReference

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val profilePhoto = itemView.findViewById<CircleImageView>(R.id.id_client_requesting_image)
        val name = itemView.findViewById<TextView>(R.id.id_client_name)
        val timeStamp = itemView.findViewById<TextView>(R.id.id_time_stamp)
        val description = itemView.findViewById<TextView>(R.id.id_description)
        val serviceType = itemView.findViewById<TextView>(R.id.id_service_type)
        val status = itemView.findViewById<TextView>(R.id.id_status)
        val distance = itemView.findViewById<TextView>(R.id.id_distance)
        val directionsButton = itemView.findViewById<ImageButton>(R.id.id_directions_btn)
        val primaryButton = itemView.findViewById<Button>(R.id.id_primary_btn)
        val secondaryButton = itemView.findViewById<Button>(R.id.id_secondary_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestListAdapter.ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.recyclerview_item_request, parent, false)

        mFirestore = FirebaseFirestore.getInstance()
        requestRef = mFirestore.collection(context.getString(R.string.ref_requests))
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return requests.size
    }

    override fun onBindViewHolder(holder: RequestListAdapter.ViewHolder, position: Int) {
        val request = requests[position]
        handleRequestViewType(request, holder)

        holder.name.text =
            "${request.clientInfo?.basicInfo?.firstName} ${request.clientInfo?.basicInfo?.lastName}"
        holder.status.text = request.status.toString()
        holder.serviceType.text = "${request.service?.serviceType} for ${request.vehicle}"
        holder.description.text = request.comment
        holder.distance.text = context.getString(R.string.miles, getDistanceFromClient(request))
        holder.timeStamp.text = if (request.acceptedOn!! > 0) {
            val time = Date(request.acceptedOn!!)
            val dateFormat = SimpleDateFormat("MMM d, y")
            val date = dateFormat.format(time)
            context.getString(R.string.accept_on, date)
        } else {
            val time = Date(request.postedOn!!)
            val dateFormat = SimpleDateFormat("MMM d, y")
            val date = dateFormat.format(time)
            context.getString(R.string.request_on, date)
        }

        if (request.clientInfo?.basicInfo?.photoUrl.isNullOrEmpty()) {
            Picasso.get().load(R.drawable.ic_circle_profile).into(holder.profilePhoto)
        } else {
            Picasso.get().load(Uri.parse(request.clientInfo?.basicInfo?.photoUrl)).into(holder.profilePhoto)
        }

        holder.secondaryButton.setOnClickListener {
            handleSecondaryOnClick(request)
        }

        holder.primaryButton.setOnClickListener {
            handlePrimaryOnClick(request)
        }

        holder.directionsButton.setOnClickListener {
            handleDirectionsOnClick(request)
        }
    }

    private fun handleRequestViewType(request: Request, holder: RequestListAdapter.ViewHolder) {
        if (request.status == Status.Request) {
            holder.primaryButton.text = context.getString(R.string.label_choice_accept)
            holder.secondaryButton.text = context.getString(R.string.label_button_info_more)
            holder.directionsButton.visibility = View.GONE
        }
        else if(request.status == Status.Active)
        {
            holder.secondaryButton.visibility = View.GONE
            holder.primaryButton.text = context.getString(R.string.label_button_info_manage)
            holder.directionsButton.visibility = View.VISIBLE
        }
        else if (request.status == Status.Paid) {
            holder.primaryButton.text = context.getString(R.string.label_choice_complete)
            holder.secondaryButton.text = context.getString(R.string.label_button_info_manage)
            holder.directionsButton.visibility = View.VISIBLE
        }
    }

    private fun handlePrimaryOnClick(request: Request) {
        if (request.status == Status.Request) {
            createAcceptDialog(request)
        }
        else if (request.status == Status.Paid) {
            createCompleteDialog(request)
        }
        else if (request.status == Status.Active) {
            val intent = Intent(context, MechanicManageJobActivity::class.java)
            intent.putExtra(EXTRA_REQUEST, request)
            context.startActivity(intent)
        }
    }

    private fun handleSecondaryOnClick(request: Request) {
        if (request.status == Status.Request) {
            val intent = Intent(context, MechanicMoreInformationActivity::class.java)
            intent.putExtra(EXTRA_REQUEST, request)
            context.startActivity(intent)
        }
        else if (request.status == Status.Paid) {
            val intent = Intent(context, MechanicManageJobActivity::class.java)
            intent.putExtra(EXTRA_REQUEST, request)
            context.startActivity(intent)
        }
    }

    private fun handleDirectionsOnClick(request: Request) {
//        val address = request.clientInfo?.address.toString()
//        val latLong = AddressManager.convertAddressToLatLng(context, request.clientInfo?.address)

        val uri = String.format(
            Locale.ENGLISH,
            "http://maps.google.com/maps?daddr=%f,%f (%s)",
            request.clientInfo?.address?._geoloc?.lat,
            request.clientInfo?.address?._geoloc?.lng,
            request.clientInfo?.address.toString()
        )
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.setPackage("com.google.android.apps.maps")
        context.startActivity(intent)
    }

    private fun createAcceptDialog(request: Request) {
        val container = context.layoutInflater.inflate(R.layout.dialog_container_basic, null)
        val dialogBody = context.layoutInflater.inflate(R.layout.dialog_body_choice, null)
        val acceptDialog = BasicDialog.Builder.apply {
            title = context.getString(R.string.label_choice_accept)
            positive = context.getString(R.string.yes)
            negative = context.getString(R.string.label_cancel_add_service)
        }.build(context, container, dialogBody)
        acceptDialog.show()

        acceptDialog.id_positive.setOnClickListener {
            acceptRequest(request)
            acceptDialog.dismiss()
        }

        acceptDialog.id_negative.setOnClickListener {
            acceptDialog.dismiss()
        }
    }

    private fun createCompleteDialog(request: Request) {
        val container = context.layoutInflater.inflate(R.layout.dialog_container_basic, null)
        val dialogBody = context.layoutInflater.inflate(R.layout.dialog_body_complete, null)

        val completeDialog = BasicDialog.Builder.apply {
            title = context.getString(R.string.label_choice_complete)
            positive = context.getString(R.string.label_choice_confirm)
            negative = context.getString(R.string.text_cancel)
        }.build(context, container, dialogBody)

        completeDialog.show()

        completeDialog.id_positive.setOnClickListener {
            completeRequest(request)
            completeDialog.dismiss()
        }

        completeDialog.id_negative.setOnClickListener {
            completeDialog.dismiss()
        }
    }

    private fun acceptRequest(request: Request) {
        val acceptedOn = System.currentTimeMillis()
        requestRef.document(request.objectID)
            .update(
                "status", Status.Active,
                "acceptedOn", acceptedOn
            )
            ?.addOnSuccessListener {
                Toasty.makeText(context, "Success", ToastyType.SUCCESS)
            }
            .addOnFailureListener {
                Toasty.makeText(context, "Fail", ToastyType.FAIL)
            }
    }

    private fun completeRequest(request: Request) {
        val completedOn = System.currentTimeMillis()
        requestRef.document(request.objectID)
            .update("status", Status.Completed, "completedOn", completedOn)
            ?.addOnSuccessListener {
                Toasty.makeText(context, "Success", ToastyType.SUCCESS)
            }
            .addOnFailureListener {
                Toasty.makeText(context, "Fail", ToastyType.FAIL)
            }
    }

    private fun getDistanceFromClient(request: Request): Double {
            val clientLocation = Location("client").apply {
                latitude = request.clientInfo?.address?._geoloc!!.lat
                longitude = request.clientInfo?.address?._geoloc!!.lng
            }

            val mechanicLocation = Location("mechanic").apply {
                latitude = request.mechanicInfo?.address?._geoloc!!.lat
                longitude = request.mechanicInfo?.address?._geoloc!!.lng
            }

        return AddressManager.getDistanceMI(clientLocation, mechanicLocation)
    }
}