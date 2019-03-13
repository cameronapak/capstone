package com.example.mobilemechanic.shared.Registration

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.mobilemechanic.shared.Registration.fragments.AddressInfoFragment
import com.example.mobilemechanic.shared.Registration.fragments.CredentialsFragment
import com.example.mobilemechanic.shared.Registration.fragments.InfoFragment
import com.example.mobilemechanic.shared.Registration.fragments.UploadProfilePhotoFragment

class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 4
    }

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> return CredentialsFragment()
            1 -> return InfoFragment()
            2 -> return UploadProfilePhotoFragment()
            3 -> return AddressInfoFragment()
        }
        return null
    }
}