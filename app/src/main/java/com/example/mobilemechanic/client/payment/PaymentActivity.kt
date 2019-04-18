package com.example.mobilemechanic.client.payment

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.example.mobilemechanic.R
import com.example.mobilemechanic.client.CLIENT_TAG
import com.example.mobilemechanic.mechanic.EXTRA_REQUEST
import com.example.mobilemechanic.model.Request
import com.example.mobilemechanic.model.Status
import com.example.mobilemechanic.model.dto.Receipt
import com.example.mobilemechanic.model.stripe.Payment
import com.example.mobilemechanic.shared.Toasty
import com.example.mobilemechanic.shared.ToastyType
import com.example.mobilemechanic.shared.utility.DateTimeManager
import com.example.mobilemechanic.shared.utility.NumberManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.stripe.android.Stripe
import com.stripe.android.TokenCallback
import com.stripe.android.model.Card
import com.stripe.android.model.Token
import kotlinx.android.synthetic.main.activity_payment.*
import kotlinx.android.synthetic.main.card_payment_container.*
import kotlinx.android.synthetic.main.card_payment_container.view.*
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
    private lateinit var myReceiptRef: CollectionReference
    private var grandTotal: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        request = intent.getParcelableExtra(EXTRA_REQUEST)
        setUpPaymentActivity()
    }

    private fun setUpPaymentActivity() {
        initFirestore()
        setUpActionBar()
        setUpSummaryContainer()
        setUpOnSubmitPayment()
    }

    private fun initFirestore() {
        mAuth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()
        paymentsRef = mFirestore.collection(getString(R.string.ref_payments))
        myPaymentsRef = paymentsRef.document("${mAuth.currentUser?.uid}")
            .collection(getString(R.string.ref_charges))
        myReceiptRef = mFirestore.collection("Requests")
    }

    private fun setUpSummaryContainer() {
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
        grandTotal = service?.price!!+tax
        holder.id_grand_total_price.text = getString(R.string.price, grandTotal)

        id_tip.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                updateTotal(s.toString())
            }
        })
    }

    private fun setUpOnSubmitPayment() {
        id_pay_btn.setOnClickListener {
            submitPayment()
        }
    }

    private fun updateTotal(s: String) {
        val holder = id_payment_container
        val tips = holder.id_tip.text.toString()

        if (NumberManager.isNumeric(tips)) {
            Log.d(CLIENT_TAG, "[PaymentActivity] $tips")
            val grandTotalAfterTip = grandTotal + tips.toDouble()
            holder.id_grand_total_price.text = getString(R.string.price, grandTotalAfterTip)
        }
    }

    private fun submitPayment() {
        val holder = id_payment_container
        val holder2 = id_payment_container.id_summary_container
        val total: Double = (holder2.id_grand_total_price.text.toString().substring(1).toDouble())

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
            Toasty.makeText(this, "Invalid card number", ToastyType.WARNING)
        }

        //check CVC
        if(!card.validateCVC()){
            Toasty.makeText(this, "Invalid CVC", ToastyType.WARNING)
        }

        //check card
        if(!card.validateCard()) {
            Toasty.makeText(this, "Invalid card", ToastyType.WARNING)
        } else {
            convertInfoToToken(card, total)
        }
    }

    private fun convertInfoToToken(card: Card, amount: Double) {
        val stripe = Stripe(this, "pk_test_wTx4vP8D0gatpbC02tmXXthM00qBhOeNO5")
        stripe.createToken(card, object : TokenCallback {
            override fun onSuccess(token: Token) {
                createPayment(token.id, amount)
            }

            override fun onError(error: Exception?) {
                Toasty.makeText(this@PaymentActivity, "Fail", ToastyType.FAIL)
                error!!.printStackTrace()
                Log.d(PAYMENT_TAG, error.toString())
            }
        })
    }

    private fun createPayment(tokenId: String, amount: Double) {
        val email =
            if(!request.clientInfo?.basicInfo?.email.isNullOrEmpty()) request.clientInfo?.basicInfo!!.email
            else ""
        val payment = Payment("", amount, tokenId, email)
        payment.description += "Payment charge for ${request.service?.serviceType} service on ${request.vehicle}."


        val holder = id_payment_container
        val service = request.service

        var tips = 0.0
        if (NumberManager.isNumeric(holder.id_tip.text.toString())) {
            tips = holder.id_tip.text.toString().toDouble()
        } else {
            Toasty.makeText(this@PaymentActivity, "Invalid tips amount", ToastyType.FAIL)
        }

        val subTotal = getString(R.string.price, service?.price).substring(1).toDouble()
        val estimatedTax = holder.id_summary_estimated_tax_price.text.toString().substring(1).toDouble()
        val receipt = Receipt(tips, subTotal, estimatedTax, amount)

        myPaymentsRef.document().set(payment)
            .addOnSuccessListener {
                myReceiptRef.document("${request.objectID}")
                    .update("receipt", receipt, "status", Status.Paid.name,
                        "completedOn", DateTimeManager.currentTimeMillis())
                    .addOnSuccessListener {
                    Toasty.makeText(this@PaymentActivity, "Payment completed", ToastyType.SUCCESS)
                    finish()
                }.addOnFailureListener {
                    Toasty.makeText(this@PaymentActivity, "Fail to create receipt", ToastyType.FAIL)
                }
            }.addOnFailureListener {
                Toasty.makeText(this@PaymentActivity, "Fail to create payment", ToastyType.FAIL)
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