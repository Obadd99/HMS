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
){
    override fun toString(): String {
        return "Result(id='$id', userId='$userId', spo2=$spo2, temp=$temp, bpm=$bpm, spo2Case='$spo2Case', tempCase='$tempCase', bpmCase='$bpmCase', haveCovid19=$haveCovid19, note='$note', date=$date)"
    }
}
