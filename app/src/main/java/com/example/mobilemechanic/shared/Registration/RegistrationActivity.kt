package com.example.mobilemechanic.shared.Registration

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.badoualy.stepperindicator.StepperIndicator
import com.example.mobilemechanic.R
import com.example.mobilemechanic.model.User
import com.example.mobilemechanic.model.UserType
import com.example.mobilemechanic.model.dto.Address
import com.example.mobilemechanic.model.dto.BasicInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.android.synthetic.main.fragment_credentials.*

const val ACCOUNT_DOC_PATH = "Accounts"

class RegistrationActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth?= null
    private var mFireStore: FirebaseFirestore? = null
    private var mStorage: FirebaseStorage? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        mAuth = FirebaseAuth.getInstance()
        mFireStore = FirebaseFirestore.getInstance()
        mStorage = FirebaseStorage.getInstance()

        val registrationViewModel = ViewModelProviders.of(this).get(RegistrationViewModel::class.java)
        setUpRegistrationResults(registrationViewModel)
        setUpStepperIndicator()
    }

    private fun setUpStepperIndicator() {
        val pager = findViewById<ViewPager>(R.id.id_registrationPager)
        pager.adapter = SectionsPagerAdapter(supportFragmentManager)

        val indicator = findViewById<StepperIndicator>(R.id.id_registrationStepper)
        indicator.apply {
            stepCount = 4
            currentStep = 0
            setViewPager(pager, false)
            setAnimIndicatorRadius(1f)
            //addOnStepClickListener { step -> pager.setCurrentItem(step, true) }
        }
    }

    private fun setUpRegistrationResults(registrationViewModel: RegistrationViewModel) {
        val info = BasicInfo()
        val user = User()
        val address = Address()

        registrationViewModel.userType.observe(this, Observer {
            it?.let {
                user.userType = getUserType(id_clientType.isChecked)

                printResults(user, info, address)
            }
        })

        registrationViewModel.emailAddress.observe(this, Observer {
            it?.let {
                info.email = "$it"

                printResults(user, info, address)
            }
        })

        registrationViewModel.password.observe(this, Observer {
            it?.let {
                user.password = "$it"

                printResults(user, info, address)
            }
        })

        registrationViewModel.firstName.observe(this, Observer {
            it?.let {
                info.firstName = "$it"

                printResults(user, info, address)
            }
        })

        registrationViewModel.lastName.observe(this, Observer {
            it?.let {
                info.lastName = "$it"

                printResults(user, info, address)
            }
        })

        registrationViewModel.phoneNumber.observe(this, Observer {
            it?.let {
                info.phoneNumber = "$it"

                printResults(user, info, address)
            }
        })

        registrationViewModel.imageUri.observe(this, Observer {
            it?.let {
                info.photoUrl = "$it"

                printResults(user, info, address)
            }
        })

        registrationViewModel.streetAddress.observe(this, Observer {
            it?.let {
                 address.street = "$it"

                printResults(user, info, address)
            }
        })

        registrationViewModel.city.observe(this, Observer {
            it?.let {
                address.city = "$it"

                printResults(user, info, address)
            }
        })

        registrationViewModel.state.observe(this, Observer {
            it?.let {
                address.state = "$it"

                printResults(user, info, address)
            }
        })

        registrationViewModel.zipcode.observe(this, Observer {
            it?.let {
                address.zipCode = "$it"

                printResults(user, info, address)
            }
        })
    }

    private fun getUserType(isClient: Boolean): UserType {
        return  if (isClient) UserType.CLIENT
        else UserType.MECHANIC
    }

    private fun printResults(user: User, info: BasicInfo, address: Address) {
        id_registrationResults.text = "${user.userType}\t" +
                "${info.email}\t" +
                "${user.password}\t" +
                "${info.firstName}\t" +
                "${info.lastName}\t" +
                "${info.phoneNumber}\n" +
                "${info.photoUrl}\n" +
                "${address.street}\t" +
                "${address.city}\t" +
                "${address.state}\t" +
                "${address.zipCode}"
    }

    /*private fun createUserAccount() {
        val userType = getUserType(id_registration_client.isChecked)
        val email = id_registration_email.text.toString().trim()
        val password = id_registration_password.text.toString().trim()
        val firstName = id_registration_firstName.text.toString().trim()
        val lastName = id_registration_lastName.text.toString().trim()
        val phoneNumber = id_registration_phoneNumber.text.toString().trim()
        val street = id_registration_address.text.toString().trim()
        val city = id_registration_city.text.toString().trim()
        val state = findViewById<Spinner>(R.id.id_registration_state).selectedItem.toString().trim()
        val zip = id_registration_zipcode.text.toString().trim()

        if(validateInformation(email, password, firstName, lastName, phoneNumber, street, city, state, zip)) {
            val address = Address(street, city, state, zip)
            val basicInfo = BasicInfo(firstName, lastName, email, phoneNumber, "")
            val user = User("", password, userType, basicInfo, address, 0f)

            mAuth?.createUserWithEmailAndPassword(email, password)
                ?.addOnCompleteListener {
                    user.uid = mAuth?.uid.toString()
                    handleAccountCreationSuccess(it, user)
            }
        }
    }

    private fun handleAccountCreationSuccess(it: Task<AuthResult>, user: User) {
        if(it.isSuccessful) {
            Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
            saveUser(user)
        } else {
            Toast.makeText(this, "Account created failed!\n${it.exception.toString()}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUser(userInfo: User) {
        mFireStore?.collection(ACCOUNT_DOC_PATH)
            ?.document(userInfo.uid)
            ?.set(userInfo)
            ?.addOnSuccessListener {
                Toast.makeText(this, "Account info added!", Toast.LENGTH_SHORT).show()

                goToUploadPictureActivity(userInfo.userType)
            }
            ?.addOnFailureListener {
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onResume() {
        super.onResume()
        ScreenManager.hideStatusAndBottomNavigationBar(this)
    }*/
}
