package com.example.mobilemechanic.shared

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.support.v4.content.res.ResourcesCompat
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.mobilemechanic.R
import com.example.mobilemechanic.model.Vehicle


class HintVehicleSpinnerAdapter(context: Context, resource: Int, stateList: List<Vehicle>) :
    ArrayAdapter<Vehicle>(context, resource, stateList) {

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
            v.text = "Vehicle"
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
            v.text = "Vehicle"
            textView.setTextColor(Color.GRAY)
        } else {
            textView.setTextColor(Color.BLACK)
        }
        return v
    }
}