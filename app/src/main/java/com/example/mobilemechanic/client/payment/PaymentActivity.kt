package com.example.mobilemechanic.client.payment

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.Toast
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
import com.example.mobilemechanic.model.stripe.Payment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.card_payment_summary_container.view.*


const val PAYMENT_TAG = "payment"
const val TAX_RATE = .086

class PaymentActivity : AppCompatActivity()
{
    private lateinit var request: Request
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var paymentsRef: CollectionReference
    private lateinit var myPaymentsRef: CollectionReference


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
        initFirestore()
        setUpActionBar()
        setUpSummaryContainer()
    }

    private fun initFirestore() {
        mAuth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()
        paymentsRef = mFirestore.collection(getString(R.string.ref_payments))
        myPaymentsRef = paymentsRef.document().collection("${mAuth.currentUser?.uid}")
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

    private fun submitPayment()
    {
        val holder = id_payment_container
        val holder2 = id_payment_container.id_summary_container

        //substring to remove $ sign
        val tips = holder.id_tip.text.toString().substring(1)

        val total: Double = holder2.id_grand_total_price.text.toString().substring(1).toDouble()
            + tips.toDouble()

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
        if(!card.validateCard())
        {
            Toast.makeText(this, "Invalid Payment Information.", Toast.LENGTH_SHORT).show()
        }
        else
        {
            convertInfoToToken(card, total)
        }
    }

    private fun convertInfoToToken(card: Card, amount: Double)
    {
        val stripe = Stripe(this, "pk_test_wTx4vP8D0gatpbC02tmXXthM00qBhOeNO5")
        stripe.createToken(card, object : TokenCallback {
            override fun onSuccess(token: Token) {
                createPayment(token.id, amount)
                //TODO: make cloud function to listen to payments collection and communicate with stripe to charge card
            }

            override fun onError(error: Exception?) {
                Toast.makeText(this@PaymentActivity,"Token Not Created!!", Toast.LENGTH_LONG).show()
                error!!.printStackTrace()
                Log.d(PAYMENT_TAG, error.toString())
            }

        })
    }

    private fun createPayment(tokenId: String, amount: Double)
    {
        val payment = Payment("", amount, tokenId)
        payment.description += "${request.service?.serviceType}"

        myPaymentsRef.document().set(payment)
            .addOnSuccessListener {
                Toast.makeText(this, "Payment successful!", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Payment failed.", Toast.LENGTH_SHORT).show()
            }
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