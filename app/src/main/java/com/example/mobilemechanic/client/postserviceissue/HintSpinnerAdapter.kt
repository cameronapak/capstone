package com.example.mobilemechanic.client.postserviceissue

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.support.v4.content.res.ResourcesCompat
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.mobilemechanic.R


class HintSpinnerAdapter(context: Context, resource: Int, stateList: List<String>) :
    ArrayAdapter<String>(context, resource, stateList) {
    private val stateList = ArrayList<String>()

    override fun isEnabled(position: Int): Boolean {
       if (position == 0) {
           return false
       }
        return true
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v = super.getView(position, convertView, parent)
        var textView = v as TextView
        val qualioTypeface = Typeface.createFromAsset(context.assets, "font/qualio.otf")

        textView.apply {
            typeface = qualioTypeface
            textSize = 24f
            setPadding(0, 10, 0, 10)
        }

        if (position == 0) {
            textView.setTextColor(ResourcesCompat.getColor(parent.resources, R.color.colorGrayText, null))
        } else {
            textView.setTextColor(ResourcesCompat.getColor(parent.resources, R.color.colorHeaderDark, null))
        }

        return textView
    }


    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v = super.getDropDownView(position, convertView, parent)
        val textView = v as TextView
        if (position == 0) {
            textView.setTextColor(Color.GRAY)
        } else {
            textView.setTextColor(Color.BLACK)
        }
        return v
    }

//    private fun initView(position: Int): View {
//        val state = getItem(position)
//        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val v = inflater.inflate(R.layout.state_list, null)
//        val textView = v.findViewById(R.id.spinnerText)
//        textView.setText(state!!.getStateName())
//        return v
//    }
}