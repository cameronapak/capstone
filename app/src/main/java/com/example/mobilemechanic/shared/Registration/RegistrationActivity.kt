package com.example.mobilemechanic.shared.Registration

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import com.badoualy.stepperindicator.StepperIndicator
import com.example.mobilemechanic.R
import com.example.mobilemechanic.model.User
import com.example.mobilemechanic.model.UserType
import com.example.mobilemechanic.model.dto.Address
import com.example.mobilemechanic.model.dto.BasicInfo
import com.example.mobilemechanic.shared.USER_TAG
import com.example.mobilemechanic.shared.utility.ScreenManager
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
        setUpRegistrationActivity()
    }

    private fun setUpRegistrationActivity() {
        val registrationViewModel = ViewModelProviders.of(this).get(RegistrationViewModel::class.java)
        setUpRegistrationResults(registrationViewModel)
        setUpStepperIndicator()
        setUpToolBar()
        enableHideKeyboard()
    }


    private fun setUpStepperIndicator() {
        val pager = findViewById<ViewPager>(R.id.id_registrationPager) as NonSwipeableViewPager
        pager.adapter = SectionsPagerAdapter(supportFragmentManager)

        val indicator = findViewById<StepperIndicator>(R.id.id_registrationStepper)
        indicator.apply {
            stepCount = 4
            currentStep = 0
            setViewPager(pager, false)
        }
    }

    private fun setUpToolBar() {
        val arrow = resources.getDrawable(com.example.mobilemechanic.R.drawable.abc_ic_ab_back_material, null)
        arrow.setColorFilter(
            resources.getColor(com.example.mobilemechanic.R.color.colorPrimaryDark),
            PorterDuff.Mode.SRC_ATOP
        )
        setSupportActionBar(id_registration_toolbar as Toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(arrow)
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

        registrationViewModel.photoUrl.observe(this, Observer {
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
        val result = "${user.userType}\t" +
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

        Log.d(USER_TAG, "[RegistrationActivity] current form data $result")
    }

    private fun enableHideKeyboard() {
        id_registration_form_frame_layout.setOnClickListener {
            ScreenManager.hideKeyBoard(this)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        ScreenManager.hideStatusAndBottomNavigationBar(this)
    }
}
