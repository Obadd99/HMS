package com.developers.healtywise.domin.models.main

data class Result(
    var id:String="",
    val userId: String="",
    val spo2:Float=0f,
    val temp:Float=0f,
    val bpm:Float=0f,
    val spo2Case:String="",
    val tempCase:String="",
    val bpmCase:String="",
    val haveCovid19:Boolean=false,
    val note:String="",
    val date: Long=0L
)