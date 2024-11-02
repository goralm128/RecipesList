package com.exercise.recipes.network.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Recipe(
    @SerializedName("name")
    val name: String,
    @SerializedName("thumb")
    val thumb: String,
    @SerializedName("servings")
    val servings: String,
    @SerializedName("time")
    val time: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("difficulty")
    val difficulty: Int,
    @SerializedName("fats")
    val fats: String,
    @SerializedName("headline")
    val headline: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("calories")
    val calories: String,
    @SerializedName("carbos")
    val carbos: String,
    @SerializedName("description")
    val description: String
) : Parcelable

