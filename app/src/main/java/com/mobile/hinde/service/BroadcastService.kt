package com.mobile.hinde.service

import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder

class BroadcastService : Service(){

    companion object {
        const val COUNTDOWN_TICK = "tick"
        const val COUNTDOWN_FINISH = "finish"
    }

    val bi = Intent(COUNTDOWN_TICK)

    private var cdt:CountDownTimer? = null

    override fun onDestroy(){
        super.onDestroy()
        cdt?.cancel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val duration = intent?.extras?.getLong("duration")
        val target = intent?.action

        cdt = object: CountDownTimer(duration!!, 1000){
            override fun onTick(millisUntilFinished: Long) {
                bi.action = COUNTDOWN_TICK
                bi.putExtra("countdown",millisUntilFinished)
                bi.putExtra("target", target)
                sendBroadcast(bi)
            }

            override fun onFinish() {
                bi.action = COUNTDOWN_FINISH
                bi.putExtra("target",target)
                sendBroadcast(bi)
            }

        }

        cdt?.start()

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder?{ return null}

}