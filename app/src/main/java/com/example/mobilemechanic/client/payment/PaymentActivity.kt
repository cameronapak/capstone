package com.example.mobilemechanic.client.payment

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.mobilemechanic.client.CLIENT_TAG
import com.example.mobilemechanic.model.striped.Payment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.stripe.android.Stripe
import com.stripe.android.TokenCallback
import com.stripe.android.model.Card
import com.stripe.android.model.Token
import com.stripe.android.view.CardNumberEditText
import kotlinx.android.synthetic.main.activity_payment.*


class PaymentActivity : AppCompatActivity() {


    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var paymentRef: CollectionReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mCardNumberEditText: CardNumberEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.mobilemechanic.R.layout.activity_payment)

        mFirestore = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        val clientUid = mAuth.currentUser?.uid
        paymentRef = mFirestore.collection("Payments/$clientUid/payment")


        id_pay.setOnClickListener {
            val card = Card("4242-4242-4242-4242", 12, 2020, "123")
            if (card.validateNumber() && card.validateCVC()) {

                val stripe = Stripe(this, "pk_test_wTx4vP8D0gatpbC02tmXXthM00qBhOeNO5")
                stripe.createToken(card, object: TokenCallback {
                    override fun onSuccess(token: Token) {
                        // Store payment to firestore
                        val docId = paymentRef.document().id
                        val payment = Payment(docId, 555.0, token.id)

                        paymentRef.document().set(payment).addOnSuccessListener {
                            Log.d(CLIENT_TAG, "[PaymentActivity] added payment successfully")
                        }

                        Log.d(CLIENT_TAG, "[PaymentActivity] onSuccess() token: $token")
                    }

                    override fun onError(error: Exception) {
                        Log.d(CLIENT_TAG, "[PaymentActivity ]${error.message}")
                    }
                })

            }
        }
    }
}
