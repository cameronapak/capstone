package com.example.mobilemechanic.client

import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.example.mobilemechanic.R
import kotlinx.android.synthetic.main.activity_client_welcome.*


class ClientWelcomeActivity : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.mobilemechanic.R.layout.activity_client_welcome)

        setUpClientWelcomeActivity()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                mDrawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setUpClientWelcomeActivity() {
        setUpToolBar()
        setUpNavDrawerButton()
        mDrawerLayout = findViewById(R.id.client_drawer_layout)
    }

    private fun setUpToolBar() {
        setSupportActionBar(id_toolbar as Toolbar)
    }

    private fun setUpNavDrawerButton() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.apply {
            title = "Welcome"
            subtitle = "Jackie Chan"
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }
    }
}
