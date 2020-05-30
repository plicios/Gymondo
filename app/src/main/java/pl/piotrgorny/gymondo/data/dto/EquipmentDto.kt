package pl.piotrgorny.gymondo.data.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EquipmentDto(
    val id: Long,
    val name: String
) : Parcelable