package pl.piotrgorny.gymondo.data.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MuscleDto(
    val id: Long,
    val name: String,
    val is_front: Boolean
) : Parcelable