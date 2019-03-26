package com.example.mobilemechanic.client.payment

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.widget.Toolbar
import android.widget.Toast
import com.example.mobilemechanic.R
import kotlinx.android.synthetic.main.activity_payment.*
import com.stripe.android.model.Card
import kotlinx.android.synthetic.main.payment_container.*
import kotlinx.android.synthetic.main.payment_container.view.*


class Payment : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        setUpActionBar()

        id_sumbit_payment_btn.setOnClickListener {
            submitPayment()
        }
    }

    private fun submitPayment(){
        val holder = id_payment_container_card

        val tips = holder.id_tips.text.toString()
        val cardNumber = holder.id_card_nums.text.toString()
        val cardExpMonth = holder.id_expire_date.text.substring(0,2).toInt()
        val cardExpYear = holder.id_expire_date.text.substring(2).toInt()
        val cardCVC = holder.id_cvc.text.toString()

        val card = Card(
            cardNumber,
            cardExpMonth,
            cardExpYear,
            cardCVC
        )
    }

    private fun setUpActionBar() {
        setSupportActionBar(id_payment_toolbar as Toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.apply {
            title = "Payment"
            subtitle = "Process payment"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
