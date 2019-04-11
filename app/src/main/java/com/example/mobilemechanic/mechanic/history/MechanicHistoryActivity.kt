package com.example.mobilemechanic.mechanic.history

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.LinearLayout
import com.example.mobilemechanic.R
import com.example.mobilemechanic.client.history.MechanicHistoryRecyclerAdapter
import com.example.mobilemechanic.model.Request
import com.example.mobilemechanic.model.Status
import com.example.mobilemechanic.shared.utility.ScreenManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_mechanic_history.*

class MechanicHistoryActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var requestRef: CollectionReference

    private var requestReceipt: ArrayList<Request> = ArrayList()
    private lateinit var historyAdapter: MechanicHistoryRecyclerAdapter
    private lateinit var emptyView: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mechanic_history)
        emptyView = findViewById<LinearLayout>(R.id.id_empty_state_view)
        setUpMechanicHistoryActivity()
    }

    private fun setUpMechanicHistoryActivity() {
        initFireStore()
        setUpToolBar()
        setUpAdapter()
        setUpHistoryRecyclerView()
    }

    private fun initFireStore() {
        mAuth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()
        requestRef = mFirestore.collection("Requests")
    }

    private fun setUpToolBar() {
        setSupportActionBar(id_mechanic_history_toolbar as Toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.apply {
            title = "History"
            subtitle = "Previous services completed"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setUpAdapter() {
        val viewManager = LinearLayoutManager(this)
        historyAdapter = MechanicHistoryRecyclerAdapter(this, requestReceipt)
        id_mechanic_history_recyclerview.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = historyAdapter
        }
        historyAdapter.notifyDataSetChanged()
    }

    private fun setUpHistoryRecyclerView() {
        requestRef.whereEqualTo("mechanicInfo.uid", mAuth?.currentUser?.uid.toString())
            .whereEqualTo("status", Status.Completed)
            ?.addSnapshotListener { querySnapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                requestReceipt.clear()
                for (doc in querySnapshot!!) {
                    val request = doc.toObject(Request::class.java)
                    request.objectID = doc.id
                    requestReceipt.add(request)
                }

                // toggle empty state view
                if (requestReceipt.isNullOrEmpty()) {
                    emptyView.setVisibility(View.VISIBLE)
                } else {
                    emptyView.setVisibility(View.GONE)
                }

                historyAdapter.notifyDataSetChanged()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        ScreenManager.hideStatusAndBottomNavigationBar(this)
    }
}
