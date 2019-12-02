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
    val level: String,
    val cost: Int,
    val description: String,
    val imageUrl: String
) : Parcelable