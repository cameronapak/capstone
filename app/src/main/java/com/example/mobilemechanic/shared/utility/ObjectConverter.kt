package com.example.mobilemechanic.shared.utility

import com.example.mobilemechanic.model.dto.ClientInfo
import com.example.mobilemechanic.model.dto.MechanicInfo
import com.example.mobilemechanic.model.messaging.Member

object ObjectConverter {
    fun convertToMember(clientInfo: ClientInfo): Member {
        return Member(
            clientInfo.uid,
            clientInfo.basicInfo.firstName,
            clientInfo.basicInfo.lastName,
            clientInfo.basicInfo.photoUrl
        )
    }

    fun convertToMember(mechanicInfo: MechanicInfo): Member {
        return Member(
            mechanicInfo.uid,
            mechanicInfo.basicInfo.firstName,
            mechanicInfo.basicInfo.lastName,
            mechanicInfo.basicInfo.photoUrl
        )
    }
}