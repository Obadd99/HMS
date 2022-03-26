package com.developers.healtywise.domin.models.main

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable


@SuppressLint("ParcelCreator")
data class CheckResult(
    val spo1: Float,
    val temp: Float,
    val bpm: Float,
):Parcelable{
    fun calcDiagnosisForSpo2(): String {
        return when {
            spo1 >= 95 && spo1 <= 100 -> {
                "Normal"
            }
            spo1 >= 91 && spo1 <= 94 -> {
                "Mild Hypoxemia"
            }
            spo1 >= 86 && spo1 <= 90 -> {
                "Moderate Hypoxemia"
            }
            else -> {
                "Serve Hypoxemia"
            }
        }
    }

    fun calcDiagnosisForTem(): String {
        return when {
            temp >= 36 && temp<= 37 -> {
                "Normal"
            }
            temp >= 38 -> {
                "you may have a fever hyperpyrexia"
            }
            else -> {
                "you have hypothermia and should seek medical assistance."
            }
        }
    }

    fun calcDiagnosisForBPM(): String {
        return when {
            bpm >= 60 && bpm <= 100 -> {
                "heart rate is Normal"
            }
            bpm > 100 -> {
                "heart rate is fast Tachycardia"
            }
            else -> {
                "heart rate is slow Bradycardia"
            }
        }

    }
//    SPO2 <95% && HeartRate>100 && Temp>38 Then

    fun calcDiagnosisForCovid19():Boolean=spo1<95 && bpm>100 && temp>38



    override fun describeContents(): Int =0

    override fun writeToParcel(p0: Parcel?, p1: Int) {
    }


}