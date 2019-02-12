package com.example.mobilemechanic.mechanic

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import com.example.mobilemechanic.R
import com.example.mobilemechanic.mechanic.addservice.AddService
import kotlinx.android.synthetic.main.activity_mechanic_services.*


class MechanicServices : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mechanic_services)
        setSupportActionBar(toolbar)

    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.mechanic_service, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_add -> {
                val i = Intent(this, AddService::class.java)
                startActivity(i)

return true

            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}