package com.example.mobilemechanic.model.adapter

import android.app.Activity
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.mobilemechanic.R
import com.example.mobilemechanic.model.Review
import com.example.mobilemechanic.model.messaging.Message
import com.example.mobilemechanic.shared.utility.DateTimeManager
import com.example.mobilemechanic.shared.utility.ScreenManager
import com.example.mobilemechanic.shared.utility.StringManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class ReviewListAdapter(var context: Activity, var items: ArrayList<Review>) :
    RecyclerView.Adapter<ReviewListAdapter.ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_item_mechanic_reviews, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

    }


}

