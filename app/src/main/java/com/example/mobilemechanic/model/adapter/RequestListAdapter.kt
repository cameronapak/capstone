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
                val intent = Intent(context, MechanicMoreInformationActivity::class.java)
                intent.putExtra(EXTRA_REQUEST, requests[position])
                (context as Activity).startActivityForResult(intent, REQ_CODE_MORE_INFO)
            }

            choiceButton.setOnClickListener {
                val choiceDialog =
                    (context as MechanicWelcomeActivity).createChoiceDialog(choiceButton.text.toString(),
                        requests[position])
                choiceDialog.show()
            }
        }
    }
}