package com.example.mobilemechanic.mechanic.history

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.example.mobilemechanic.R
import kotlinx.android.synthetic.main.activity_mechanic_history.*

class MechanicHistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mechanic_history)

        setUpMechanicHistoryActivity()
    }

    private fun setUpMechanicHistoryActivity() {
        setUpToolBar()
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
}
