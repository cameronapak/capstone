package com.example.mobilemechanic.model.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.mobilemechanic.R
import com.example.mobilemechanic.mechanic.EXTRA_REQUEST
import com.example.mobilemechanic.mechanic.MechanicWelcomeActivity
import com.example.mobilemechanic.mechanic.REQ_CODE_MORE_INFO
import com.example.mobilemechanic.mechanic.map.MechanicMoreInformationActivity
import com.example.mobilemechanic.model.Request
import com.example.mobilemechanic.shared.BasicDialog
import java.text.SimpleDateFormat
import java.util.*

class RequestListAdapter(var context: Context, var requests: ArrayList<Request>) :
    RecyclerView.Adapter<RequestListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestListAdapter.ViewHolder
    {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.recyclerview_item_request, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return requests.size
    }

    override fun onBindViewHolder(holder: RequestListAdapter.ViewHolder, position: Int) {
        val request = requests[position]
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

            holder.timeStamp.text = if(request.completedOn!! > 0){
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

        holder.infoButton.setOnClickListener {
            val intent = Intent(context, MechanicMoreInformationActivity::class.java)
            intent.putExtra(EXTRA_REQUEST, requests[position])
            (context as Activity).startActivityForResult(intent, REQ_CODE_MORE_INFO)
        }

        holder.choiceButton.setOnClickListener {
            val choiceDialog =
                (context as MechanicWelcomeActivity).createChoiceDialog(holder.choiceButton.text.toString())
            choiceDialog.show()
        }
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val name = itemView.findViewById<TextView>(R.id.id_client_name)
        val timeStamp = itemView.findViewById<TextView>(R.id.id_time_stamp)
        val description = itemView.findViewById<TextView>(R.id.id_description)
        val status = itemView.findViewById<TextView>(R.id.id_service_type)
        //val location = itemView.findViewById<TextView>(R.id.text_distance)
        val infoButton = itemView.findViewById<Button>(R.id.id_button_info)
        val choiceButton = itemView.findViewById<Button>(R.id.id_select)
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