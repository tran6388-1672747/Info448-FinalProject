package edu.uw.tran6388.ninkawalk.ui

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * A class representing a single pokemon item. Can be parsed from
 * the News API aggregator
 */
@Parcelize
data class Pokemon(
    val name: String,
    val description: String,
    val id: Int,
    val hp: String,
    val type:String,
    val weakness: String,
    val imageURL: String
) : Parcelable