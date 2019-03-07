package com.mobile.hinde.utils

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.mobile.hinde.connection.AsyncResponse
import com.mobile.hinde.connection.DurationSite
import com.mobile.hinde.database.DBHandler
import com.mobile.hinde.service.BroadcastService
import com.mobile.hinde.spacetime.FragSendAccept
import com.mobile.hinde.spacetime.R
import com.mobile.hinde.view.DynamicSineWaveView
import org.json.JSONObject

class CommModel(private val controller : FragSendAccept, private val targetName: String) {

    var v: View?=null
    private var c: Context? = null
    private var dbHandler: DBHandler? = null

    init{
        c = controller.context
        dbHandler = controller.mDBHandler
    }

    fun startSending(){
        DurationSite(object : AsyncResponse {
            override fun processFinish(output: Any) {
                val result = output as JSONObject
                val duration = result.getLong(targetName)

                val intent = Intent(c, BroadcastService::class.java)
                intent.putExtra("duration", duration)
                c?.startService(intent)

                val butSend = v?.findViewById<Button>(R.id.but_Send)
                butSend?.visibility = View.INVISIBLE

                val waveViewkot = v?.findViewById<DynamicSineWaveView>(R.id.SineWave)
                waveViewkot?.visibility = View.VISIBLE
                waveViewkot?.startAnimation()

                val txtView = v?.findViewById<TextView>(R.id.Timer)
                txtView?.visibility = View.VISIBLE
                dbHandler?.updateData(targetName, System.currentTimeMillis(), System.currentTimeMillis() + 2 * duration)
            }
        }).execute(targetName)
    }

    fun checkRemainingTime(){
        val currTime = System.currentTimeMillis()
        val targetMap = dbHandler!!.searchStartedData(targetName)

        if(targetMap.containsKey(targetName)){
            val value = targetMap[targetName] as Long

            if(currTime < value){
                val butSend = v?.findViewById<Button>(R.id.but_Send)
                butSend?.visibility = View.INVISIBLE

                val wavesView = v?.findViewById<DynamicSineWaveView>(R.id.SineWave)
                wavesView?.visibility = View.VISIBLE
                wavesView?.startAnimation()

                val duration = value - currTime
                val intent = Intent(c, BroadcastService::class.java)
                intent.action = targetName
                intent.putExtra("duration", duration)
                c?.startService(intent)
            }
        } else{
            val intent = Intent(BroadcastService.COUNTDOWN_FINISH)
            intent.putExtra("target", targetName)
            c?.sendBroadcast(intent)
        }
    }


    fun resetMainView() {
        val butAccept = v?.findViewById<Button>(R.id.but_Accept)
        butAccept?.visibility = View.INVISIBLE

        val butSend = v?.findViewById<Button>(R.id.but_Send)
        butSend?.visibility = View.VISIBLE

        dbHandler?.resetExpectedEnd(targetName)

        val txt = v?.findViewById<TextView>(R.id.Timer)
        txt?.visibility = View.INVISIBLE
    }

    fun updateTickingView(action: String, timer: Long) {
        if (action == "finish") {
            val wavesView = v?.findViewById<DynamicSineWaveView>(R.id.SineWave)
            wavesView?.stopAnimation()
            wavesView?.visibility = View.INVISIBLE

            val remain: Long = 0
            val txt = v?.findViewById<TextView>(R.id.Timer)
            txt?.text = Tools.formatTimeToString(remain)

            val butAccept = v!!.findViewById<Button>(R.id.but_Accept)
            butAccept.visibility = View.VISIBLE

            dbHandler?.resetExpectedEnd(targetName)
        } else {
            val txt = v?.findViewById<TextView>(R.id.Timer)
            txt?.text = Tools.formatTimeToString(timer)
        }
    }

    fun createSineWave(wave: DynamicSineWaveView) {
        wave.visibility = View.INVISIBLE
        wave.addWave(0.5f, 0.5f, 0f, 0, 0f) // Fist wave is for the shape of other waves.
        wave.addWave(0.5f, 2f, 0.5f, ContextCompat.getColor(c!!, android.R.color.holo_red_dark), 4f)
        wave.addWave(0.1f, 2f, 0.7f, ContextCompat.getColor(c!!, android.R.color.holo_blue_dark), 4f)
    }

}