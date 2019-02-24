package com.example.mobilemechanic.model.adapter

import android.app.Activity
import android.app.Dialog
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
import kotlinx.android.synthetic.main.dialog_container_basic.view.*
import java.text.SimpleDateFormat
import java.util.*

class RequestListAdapter(var context: Activity, var requests: ArrayList<Request>) :
    RecyclerView.Adapter<RequestListAdapter.ViewHolder>()
{

    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var requestRef: CollectionReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestListAdapter.ViewHolder
    {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.recyclerview_item_request, parent, false)
        mFirestore = FirebaseFirestore.getInstance()
        requestRef = mFirestore?.collection(context.getString(R.string.ref_requests))
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return requests.size
    }

    override fun onBindViewHolder(holder: RequestListAdapter.ViewHolder, position: Int)
    {
        holder.bindItem(position)
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v)
    {
        fun bindItem(position: Int)
        {
            val timeStamp = itemView.findViewById<TextView>(R.id.id_time_stamp)
            val description = itemView.findViewById<TextView>(R.id.id_description)
            val status = itemView.findViewById<TextView>(R.id.id_service_type)
            //val location = itemView.findViewById<TextView>(R.id.text_distance)
            val infoButton = itemView.findViewById<Button>(R.id.id_button_info)
            val choiceButton = itemView.findViewById<Button>(R.id.id_button_update)

            //fill card view
            //name.text = "${requests[position].clientInfo.firstName} ${requests[position].clientInfo.lastName}"
            status.text = requests[position].status.name
            description.text = requests[position].description
            //location.text = "0 mi"
            /*TO DO
            Add: - profile photo url downloads photo into image container
                 - location calculation
             */

            timeStamp.text = if(requests[position].timeCompleted > 0){
                val time = Date(requests[position].timeCompleted)
                val dateFormat = SimpleDateFormat("MMM d, y")
                val date = dateFormat.format(time)
                context.getString(R.string.complete_on, date)
            } else {
                val time = Date(requests[position].timePosted)
                val dateFormat = SimpleDateFormat("MMM d, y")
                val date = dateFormat.format(time)
                context.getString(R.string.request_on, date)
            }

            infoButton.setOnClickListener {
                when(requests[position].status)
                {
                    Status.Request -> {
                        val intent = Intent(context, MechanicMoreInformationActivity::class.java)
                        intent.putExtra(EXTRA_REQUEST, requests[position])
                        context.startActivityForResult(intent, REQ_CODE_MORE_INFO)
                    }

                    Status.Active -> {
//                        val intent = Intent(context, MechanicMoreInformationActivity::class.java)
//                        intent.putExtra(EXTRA_REQUEST, requests[position])
//                        (context as Activity).startActivityForResult(intent, REQ_CODE_MORE_INFO)
                        //Go to ManageJob Activity...
                    }
                }
            }

            choiceButton.setOnClickListener {
                lateinit var choiceDialog: Dialog

                when(requests[position].status)
                {
                    Status.Request -> {
                        choiceDialog = createAcceptDialog(requests[position].objectID)
                    }

                    Status.Active -> {
                        choiceDialog = createCompleteDialog(requests[position].objectID)
                    }
                }
                choiceDialog.show()
            }
        }
    }

    private fun createAcceptDialog(requestID: String): Dialog
    {
        val dialogContainer = context.layoutInflater.inflate(R.layout.dialog_container_basic, null)
        val dialogBody = context.layoutInflater.inflate(R.layout.dialog_body_choice, null)

        val choiceDialog = BasicDialog.Builder.apply{
            title = context.getString(R.string.label_choice_accept)
            positive = context.getString(R.string.yes)
            negative = context.getString(R.string.label_cancel_add_service)
        }.build(context, dialogContainer, dialogBody)

        dialogContainer.id_positive.setOnClickListener {
            //request.timeAccepted = System.currentTimeMillis()
            requestRef.document(requestID)
                .update("status", Status.Active)
                .addOnFailureListener {
                    Toast.makeText(context, context.getString(R.string.err_accept_fail), Toast.LENGTH_SHORT).show()
                }
            choiceDialog.dismiss()
        }

        dialogContainer.id_negative.setOnClickListener {
            choiceDialog.dismiss()
        }

        return choiceDialog
    }

    private fun createCompleteDialog(requestID: String): Dialog
    {
        val dialogContainer = context.layoutInflater.inflate(R.layout.dialog_container_basic, null)
        val dialogBody = context.layoutInflater.inflate(R.layout.dialog_body_complete, null)

        val choiceDialog = BasicDialog.Builder.apply{
            title = context.getString(R.string.label_choice_complete)
            positive = context.getString(R.string.label_choice_confirm)
            negative = context.getString(R.string.label_cancel_add_service)
        }.build(context, dialogContainer, dialogBody)

        dialogContainer.id_positive.setOnClickListener {
            requestRef.document(requestID)
                .update("status", Status.Complete, "timeCompleted", System.currentTimeMillis())
                .addOnFailureListener {
                    Toast.makeText(context, context.getString(R.string.err_complete_fail), Toast.LENGTH_SHORT).show()
                }
            choiceDialog.dismiss()
            choiceDialog.dismiss()
        }

        dialogContainer.id_negative.setOnClickListener {
            choiceDialog.dismiss()
        }

        return choiceDialog
    }
}