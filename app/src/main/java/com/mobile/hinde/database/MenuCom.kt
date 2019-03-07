package com.mobile.hinde.database

data class MenuCom(val name:String, val last_execute:Long, val expected_end: Long,
                   val is_arrive: Int, val is_returned:Int)