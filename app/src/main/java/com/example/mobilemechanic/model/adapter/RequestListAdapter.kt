package com.example.mobilemechanic.model.adapter

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.mobilemechanic.R
import com.example.mobilemechanic.mechanic.EXTRA_REQUEST
import com.example.mobilemechanic.mechanic.REQ_CODE_MORE_INFO
import com.example.mobilemechanic.mechanic.map.MechanicMoreInformationActivity
import com.example.mobilemechanic.model.Request
import com.example.mobilemechanic.model.Status
import com.example.mobilemechanic.shared.BasicDialog
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.dialog_container_basic.*
import java.text.SimpleDateFormat
import java.util.*

class RequestListAdapter(var context: Activity, var requests: ArrayList<Request>) :
    RecyclerView.Adapter<RequestListAdapter.ViewHolder>() {

    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var requestRef: CollectionReference

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val name = itemView.findViewById<TextView>(R.id.id_client_name)
        val timeStamp = itemView.findViewById<TextView>(R.id.id_time_stamp)
        val description = itemView.findViewById<TextView>(R.id.id_description)
        val status = itemView.findViewById<TextView>(R.id.id_service_type)
        //val location = itemView.findViewById<TextView>(R.id.text_distance)
        val primaryButton = itemView.findViewById<Button>(R.id.id_primary_btn)
        val secondaryButton = itemView.findViewById<Button>(R.id.id_secondary_btn)
//        val choiceButton = itemView.findViewById<Button>(R.id.id_select)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestListAdapter.ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.recyclerview_item_request, parent, false)

        mFirestore = FirebaseFirestore.getInstance()
        requestRef = mFirestore.collection("Requests")
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return requests.size
    }

    override fun onBindViewHolder(holder: RequestListAdapter.ViewHolder, position: Int) {
        val request = requests[position]
        handleRequestViewType(request, holder)

        //fill card view
        holder.name.text =
            "${requests[position].clientInfo?.basicInfo?.firstName} ${requests[position].clientInfo?.basicInfo?.lastName}"
        holder.status.text = request.status.toString()
        holder.description.text = request.comment
        //location.text = "0 mi"
        /*TO DO
        Add: - profile photo url downloads photo into image container
             - location calculation
         */

        holder.timeStamp.text = if (request.completedOn!! > 0) {
            val time = Date(request.completedOn!!)
            val dateFormat = SimpleDateFormat("MMM d, y")
            val date = dateFormat.format(time)
            context.getString(R.string.complete_on, date)
        } else {
            val time = Date(request.postedOn!!)
            val dateFormat = SimpleDateFormat("MMM d, y")
            val date = dateFormat.format(time)
            context.getString(R.string.request_on, date)
        }

        holder.secondaryButton.setOnClickListener {
            val intent = Intent(context, MechanicMoreInformationActivity::class.java)
            intent.putExtra(EXTRA_REQUEST, requests[position])
            (context).startActivityForResult(intent, REQ_CODE_MORE_INFO)
        }

        holder.primaryButton.setOnClickListener {
            handlePrimaryOnClick(request)
        }
    }

    private fun handleRequestViewType(request: Request, holder: RequestListAdapter.ViewHolder) {
        if (request.status == Status.Request) {
            holder.primaryButton.text = "Accept"
            holder.secondaryButton.text = "More"
        }

        if (request.status == Status.Active){
            holder.primaryButton.text = "Complete"
            holder.secondaryButton.text = "Manage"
        }
    }

    private fun handlePrimaryOnClick(request: Request) {
        if (request.status == Status.Request) {
            createAcceptDialog(request)
        }

        if (request.status == Status.Active){
            createCompleteDialog(request)
        }
    }

    // TODO: do handleSecondaryOnClick() here


    private fun createAcceptDialog(request: Request)
    {
        val container = context.layoutInflater.inflate(R.layout.dialog_container_basic, null)
        val dialogBody = context.layoutInflater.inflate(R.layout.dialog_body_choice, null)
        val acceptDialog = BasicDialog.Builder.apply{
            title = context.getString(R.string.label_choice_accept)
            positive = context.getString(R.string.yes)
            negative = context.getString(R.string.label_cancel_add_service)
        }.build(context, container, dialogBody)
        acceptDialog .show()

        acceptDialog.id_positive.setOnClickListener {
            acceptRequest(request)
            acceptDialog .dismiss()
        }

        acceptDialog.id_negative.setOnClickListener {
            acceptDialog .dismiss()
        }
    }

    private fun createCompleteDialog(request: Request)
    {
        val container = context.layoutInflater.inflate(R.layout.dialog_container_basic, null)
        val dialogBody = context.layoutInflater.inflate(R.layout.dialog_body_complete, null)

        val completeDialog = BasicDialog.Builder.apply{
            title = context.getString(R.string.label_choice_complete)
            positive = context.getString(R.string.label_choice_confirm)
            negative = context.getString(R.string.label_cancel_add_service)
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
        val acceptedOn = System.currentTimeMillis() / 1000
        requestRef.document(request.objectID)
            .update("status", Status.Active, "acceptedOn", acceptedOn)
            ?.addOnSuccessListener {
                Toast.makeText(context, "Accepted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, context.getString(R.string.err_accept_fail), Toast.LENGTH_SHORT).show()
            }
    }

    private fun completeRequest(request: Request) {
        val completedOn = System.currentTimeMillis() / 1000
        requestRef.document(request.objectID)
            .update("status", Status.Complete, "completedOn", completedOn)
            ?.addOnSuccessListener {
                Toast.makeText(context, "Service Completed!!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, context.getString(R.string.err_complete_fail), Toast.LENGTH_SHORT).show()
            }
    }
}