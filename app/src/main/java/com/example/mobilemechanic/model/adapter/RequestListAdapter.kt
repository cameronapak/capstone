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

class RequestListAdapter(var context: Context, var requests: ArrayList<Request>) :
    RecyclerView.Adapter<RequestListAdapter.ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestListAdapter.ViewHolder
    {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.request_card_view, parent, false)
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
            //references to text views
            val serviceType = itemView.findViewById<TextView>(R.id.text_service_type)
            val timeStamp = itemView.findViewById<TextView>(R.id.text_time_stamp)
            val description = itemView.findViewById<TextView>(R.id.text_description)
            val status = itemView.findViewById<TextView>(R.id.text_status)
            val location = itemView.findViewById<TextView>(R.id.text_distance)
            val infoButton = itemView.findViewById<Button>(R.id.id_button_info)
            val choiceButton = itemView.findViewById<Button>(R.id.id_button_choice)

            //fill card view
            serviceType.text = requests[position].service.serviceType
            status.text = requests[position].status.name
            description.text = requests[position].description
            //********** implement distance later ***************************/
            location.text = "0 mi"

            timeStamp.text = if(requests[position].timeCompleted > 0){
                context.getString(R.string.complete_on, requests[position].timeCompleted.toString())
            } else {
                context.getString(R.string.request_on, requests[position].timePosted.toString())
            }

            infoButton.setOnClickListener {
                val intent = Intent(context, MechanicMoreInformationActivity::class.java)
                intent.putExtra(EXTRA_REQUEST, requests[position])
                (context as Activity).startActivityForResult(intent, REQ_CODE_MORE_INFO)
            }

            choiceButton.setOnClickListener {
                val choiceDialog =
                    (context as MechanicWelcomeActivity).createChoiceDialog(choiceButton.text.toString())
                choiceDialog.show()
            }
        }
    }
}