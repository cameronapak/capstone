package com.example.mobilemechanic.client.payment

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.mobilemechanic.R
import com.stripe.android.Stripe
import com.stripe.android.TokenCallback
import com.stripe.android.model.Card
import com.stripe.android.model.Token
import kotlinx.android.synthetic.main.activity_payment.*
import kotlinx.android.synthetic.main.payment_container.*
import kotlinx.android.synthetic.main.payment_container.view.*



class PaymentActivity : AppCompatActivity() {

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

        //Toast.makeText(this, cardExpYear.toString(), Toast.LENGTH_LONG).show()
        //test with this card
        //Card("4242-4242-4242-4242", 12, 2020, "123");

        //check number
        if(!card.validateNumber()){
            Toast.makeText(this, "You Suck, your card # sucks", Toast.LENGTH_SHORT).show()
        } else{
            Toast.makeText(this, "You Good, your card # works", Toast.LENGTH_SHORT).show()
        }

        //check CVC
        if(!card.validateCVC()){
            Toast.makeText(this, "You Suck, your card CVC sucks", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "You Good, your card CVC works", Toast.LENGTH_LONG).show()
        }

        //check card
        if(!card.validateCard()){
            Toast.makeText(this, "You Suck, your card sucks", Toast.LENGTH_SHORT).show()
        } else{
            Toast.makeText(this, "You Good, your card works", Toast.LENGTH_SHORT).show()
            convertInfoToToken(card)
        }
    }

    private fun convertInfoToToken(card: Card){
        val stripe = Stripe(this, "pk_test_wTx4vP8D0gatpbC02tmXXthM00qBhOeNO5")
        stripe.createToken(card, object : TokenCallback {
            override fun onSuccess(token: Token) {
                Toast.makeText(this@PaymentActivity,"Token Created!! ${token!!.getId()}", Toast.LENGTH_LONG).show()
                //chargeCard(token!!.getId()) // Pass that token to your Server for further processing
            }

            override fun onError(error: Exception?) {
                Toast.makeText(this@PaymentActivity,"Token Not Created!!", Toast.LENGTH_LONG).show()
                error!!.printStackTrace()
            }

        })
    }

    private fun requestQueue(){
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "https://api.stripe.com"

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(Request.Method.GET, url,
            Response.Listener<String> { response ->
                // Display the first 500 characters of the response string.
                Toast.makeText(this,
                    "Response is: ${response.substring(0, 500)}", Toast.LENGTH_LONG).show()
            },
            Response.ErrorListener {
                Toast.makeText(this,
                    "cannot request queue!!", Toast.LENGTH_LONG).show()
            })

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
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