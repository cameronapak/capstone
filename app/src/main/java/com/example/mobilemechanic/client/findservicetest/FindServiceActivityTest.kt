package com.example.mobilemechanic.client.findservicetest

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.SearchView
import com.algolia.search.saas.Client
import com.algolia.search.saas.Index
import com.algolia.search.saas.Query
import com.example.mobilemechanic.R
import com.example.mobilemechanic.client.CLIENT_TAG
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_find_service_test.*
import org.json.JSONObject
import java.util.*

class FindServiceActivityTest : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var serviceIndex: Index
    private lateinit var gson: Gson
    private lateinit var hits: ArrayList<JSONObject>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_service_test)

        gson = Gson()
        hits = ArrayList()

        val client = Client(getString(R.string.algolia_app_id), getString(R.string.algolia_api_key))
        serviceIndex = client.getIndex("services")

        id_search_service.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return false
    }

    override fun onQueryTextChange(query: String?): Boolean {
        Log.d(CLIENT_TAG, "[Testing] query change: $query")
        serviceIndex.searchAsync(Query(query).setAttributesToRetrieve("mechanicInfo.basicInfo.firstName")) { content, error ->
            hits.clear()
//            Log.d(CLIENT_TAG, "$content")
//            val searchResult = gson.fromJson(content.toString(), SearchResults::class.java)

            Log.d(CLIENT_TAG, "$content")
        }
        return true
    }

}
