package com.mobile.hinde.utils

class Tools{
    companion object {
       fun formatTimeToString(time:Long):String{
           val milliTime = time/1000
           return "%02d:%02d:%02d".format(milliTime / 3600, (milliTime % 3600) / 60, (milliTime % 60))
       }

       fun formatMoneyCount(amount:Long):String{
           return "%04d".format(amount)
       }

    }
}