package com.developers.healtywise.domin.models.main

data class Result(
    val userId: String,
    val spo2:Float,
    val temp:Float,
    val bpm:Float,
    val spo2Case:String,
    val tempCase:String,
    val bpmCase:String,
    val haveCovid19:Boolean,
    val note:String="",
    val date: Long
)