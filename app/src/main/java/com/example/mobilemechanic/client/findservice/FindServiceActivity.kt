package com.example.mobilemechanic.client.findservice

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.algolia.instantsearch.core.helpers.Searcher
import com.algolia.instantsearch.ui.helpers.InstantSearch
import com.example.mobilemechanic.shared.utility.ScreenManager
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_find_service.*

class FindServiceActivity : AppCompatActivity() {

    private lateinit var mFireStore: FirebaseStorage
    private lateinit var searcher: Searcher
    private lateinit var helper: InstantSearch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.mobilemechanic.R.layout.activity_find_service)
        mFireStore = FirebaseStorage.getInstance()
        setUpFindServiceActivity()
    }

    private fun setUpFindServiceActivity() {
        setUpAlgolia()
        setUpToolBar()
    }

    private fun setUpAlgolia() {
        searcher = Searcher.create(
            getString(com.example.mobilemechanic.R.string.algolia_app_id),
            getString(com.example.mobilemechanic.R.string.algolia_api_key),
            getString(com.example.mobilemechanic.R.string.algolia_services_index)
        )
        helper = InstantSearch(this, searcher)
        helper.search()
        val hits = findViewById<HitsCustomized>(com.example.mobilemechanic.R.id.id_hits_customized)
        hits.enableKeyboardAutoHiding()
    }

    private fun setUpToolBar() {
        setSupportActionBar(id_find_service_toolbar as Toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onResume() {
        ScreenManager.hideStatusAndBottomNavigationBar(this)
        super.onResume()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDestroy() {
        searcher.destroy()
        super.onDestroy()
    }
}
