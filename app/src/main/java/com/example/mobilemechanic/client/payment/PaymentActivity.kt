package com.example.mobilemechanic.client.payment

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.Toast
//import com.android.volley.Request
//import com.android.volley.Response
//import com.android.volley.toolbox.StringRequest
//import com.android.volley.toolbox.Volley
import com.example.mobilemechanic.R
import com.example.mobilemechanic.mechanic.EXTRA_REQUEST
import com.stripe.android.Stripe
import com.stripe.android.TokenCallback
import com.stripe.android.model.Card
import com.stripe.android.model.Token
import kotlinx.android.synthetic.main.activity_payment.*
import kotlinx.android.synthetic.main.card_payment_container.*
import kotlinx.android.synthetic.main.card_payment_container.view.*
import com.example.mobilemechanic.model.Request
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.card_payment_summary_container.view.*


const val PAYMENT_TAG = "payment"
const val TAX_RATE = .086

class PaymentActivity : AppCompatActivity()
{
    private lateinit var request: Request

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        request = intent.getParcelableExtra(EXTRA_REQUEST)

        setUpPaymentActivity()

        id_pay_btn.setOnClickListener {
            submitPayment()
        }
    }

    private fun setUpPaymentActivity()
    {
        setUpActionBar()
        setUpSummaryContainer()
    }

    private fun setUpSummaryContainer()
    {
        val holder = id_payment_container.id_summary_container

        val vehicle = request.vehicle
        holder.id_car_title.text = "${vehicle?.year} ${vehicle?.make} ${vehicle?.model}"

        if (!vehicle?.photoUrl.isNullOrEmpty()) {
            Picasso.get().load(vehicle?.photoUrl).into(holder.id_car_image)
        }

        val service = request.service
        holder.id_service_completed.text = service?.serviceType

        val mechanicInfo = request.mechanicInfo?.basicInfo
        holder.id_mechanic_name.text = "${mechanicInfo?.firstName} ${mechanicInfo?.lastName}"

        if (mechanicInfo?.photoUrl.isNullOrEmpty()) {
            Picasso.get().load(R.drawable.ic_circle_profile).into(holder.id_profile_image)
        } else {
            Picasso.get().load(Uri.parse(mechanicInfo?.photoUrl)).into(holder.id_profile_image)
        }

        holder.id_summary_subtotal_price.text = getString(R.string.price, service?.price)
        val tax = service?.price!!* TAX_RATE
        holder.id_summary_estimated_tax_price.text = getString(R.string.price, tax)
        val total = service?.price!!+tax
        holder.id_grand_total_price.text = getString(R.string.price, total)
    }

    private fun submitPayment(){
        val holder = id_payment_container
        val holder2 = id_payment_container.id_summary_container

        val tips = holder.id_tip.text.toString()
        //get error because tips is 0
        //val total: Double = holder2.id_grand_total_price.text.toString().toDouble() + tips.toDouble()

        val cardNumber = holder.id_card_number.text.toString()
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
            Toast.makeText(this, "Invalid card number.", Toast.LENGTH_SHORT).show()
        }

        //check CVC
        if(!card.validateCVC()){
            Toast.makeText(this, "Invalid CVC", Toast.LENGTH_SHORT).show()
        }

        //check card
        if(!card.validateCard()){
            Toast.makeText(this, "Invalid Payment Information.", Toast.LENGTH_SHORT).show()
        } else{
            Toast.makeText(this, "Payment successful!", Toast.LENGTH_SHORT).show()
            convertInfoToToken(card)
        }
    }

    private fun convertInfoToToken(card: Card){
        val stripe = Stripe(this, "pk_test_wTx4vP8D0gatpbC02tmXXthM00qBhOeNO5")
        stripe.createToken(card, object : TokenCallback {
            override fun onSuccess(token: Token) {
                Toast.makeText(this@PaymentActivity,"Token Created!! ${token!!.getId()}", Toast.LENGTH_LONG).show()
                //TODO: make stripe payment model with token and other payment info
                //TODO: make cloud function to listen to payments collection and communicate with stripe to charge card
            }

            override fun onError(error: Exception?) {
                Toast.makeText(this@PaymentActivity,"Token Not Created!!", Toast.LENGTH_LONG).show()
                error!!.printStackTrace()
            }

        })
    }

//    private fun requestQueue(){
//        // Instantiate the RequestQueue.
//        val queue = Volley.newRequestQueue(this)
//        val url = "http://mobilemechanic.us/charge"
//
//        val postRequest = StringRequest(Request.Method.POST, url,
//            Response.Listener<String> {
//                override fun onResp
//                Log.d(PAYMENT_TAG, "[Volley]Response: $it")
//            },
//            Response.ErrorListener {
//                Log.d(PAYMENT_TAG, "[Volley]Error.Response: $it")
//            }
//        ) {
//
//        }
//
//        // Request a string response from the provided URL.
//        val stringRequest = StringRequest(Request.Method.GET, url,
//            Response.Listener<String> { response ->
//                // Display the first 500 characters of the response string.
//                Toast.makeText(this,
//                    "Response is: ${response.substring(0, 500)}", Toast.LENGTH_LONG).show()
//            },
//            Response.ErrorListener {
//                Toast.makeText(this,
//                    "cannot request queue!!", Toast.LENGTH_LONG).show()
//            })
//
//        // Add the request to the RequestQueue.
//        queue.add(stringRequest)
//    }

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