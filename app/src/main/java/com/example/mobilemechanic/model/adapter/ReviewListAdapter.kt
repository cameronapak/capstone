package com.example.mobilemechanic.model.adapter

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.example.mobilemechanic.R
import com.example.mobilemechanic.client.CLIENT_TAG
import com.example.mobilemechanic.model.Request
import com.example.mobilemechanic.model.Review
import com.example.mobilemechanic.shared.utility.DateTimeManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso


class ReviewListAdapter(var context: Activity, var items: ArrayList<Review>) :
    RecyclerView.Adapter<ReviewListAdapter.ViewHolder>() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var requestRef: CollectionReference


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.id_recyclerview_mechanic_reviews_image)
        val name = view.findViewById<TextView>(R.id.id_recyclerview_mechanic_reviews_name)
        val description = view.findViewById<TextView>(R.id.id_recyclerview_mechanic_reviews_description)
        val postedOn = view.findViewById<TextView>(R.id.id_recyclerview_mechanic_reviews_postedOn)
        val rating = view.findViewById<RatingBar>(R.id.id_recyclerview_mechanic_reviews_ratingbar)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mAuth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()
        requestRef = mFirestore.collection("Requests")
        val review = items[position]

        requestRef.get()
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    for(document in it.result!!) {
                        val request = document.toObject(Request::class.java)
                        request.objectID = document.id
                            if(review.requestID == request.objectID) {
                                Picasso.get().load(request?.clientInfo?.basicInfo?.photoUrl).into(holder.image)
                                holder.name.text = request?.clientInfo?.basicInfo?.firstName
                                holder.description.text = review?.comment
                                holder.postedOn.text = "Posted on ${DateTimeManager.millisToDate(review.postedOn, "MMM d, y")}"
                                holder.rating.rating = review?.rating
                                Log.d(CLIENT_TAG, "[RequestListAdapter] ReviewID: ${review.requestID}")

                        }
                    }
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_item_mechanic_reviews, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }


}

