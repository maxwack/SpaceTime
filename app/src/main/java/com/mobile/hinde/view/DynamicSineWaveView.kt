package com.mobile.hinde.view

import android.content.Context
import android.graphics.*
import android.os.SystemClock
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View
import java.lang.RuntimeException
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class DynamicSineWaveView : View {

    companion object {
        const val POINTS_FOR_CYCLE = 10
    }

    inner class Wave(val amplitude:Float, val cycle:Float, val speed:Float) {
        constructor(wave: Wave):this(wave.amplitude,wave.cycle, wave.speed)
    }

    private val waveConfigs = mutableListOf<Wave>()
    private val wavePaints = mutableListOf<Paint>()
    private val wavePathScale = Matrix()
    private var currentPaths = listOf<Path>()
    private var drawingPath = Path()

    private var viewWidth = 0
    private var viewHeight = 0

    private var baseWaveAmplitudeScale:Float=1f

    private val transferPathsQueue = LinkedBlockingQueue<List<Path>>(1)

    private var requestFutureChange = false
//    private var requestCondition : Any?=null
    private val lock = ReentrantLock()
    private val requestCondition = lock.newCondition()

    private var startAnimateTime = 0L

    private var animateTicker = object:Runnable{
        override fun run() {
            requestUpdateFrame()
            ViewCompat.postOnAnimation(this@DynamicSineWaveView, this)
        }
    }

    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet): super(context,attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context,attrs,defStyleAttr)

    init{
        if(isInEditMode){
            addWave(0.5f, 0.5f, 0f, 0, 0f)
            addWave(0.5f, 2.5f, 0f, Color.BLUE, 2f)
            addWave(0.3f, 2f, 0f, Color.RED, 2f)
            baseWaveAmplitudeScale = 1f
            tick()
        }
        createComputationThread().start()
    }

    fun addWave(amplitude:Float, cycle:Float, speed:Float, color:Int, stroke:Float):Int{
        synchronized(waveConfigs){
            waveConfigs.add(Wave(amplitude,cycle,speed))
        }

        if(waveConfigs.size > 1){
            val paint = Paint()
            paint.color = color
            paint.strokeWidth = stroke
            paint.style = Paint.Style.STROKE
            paint.isAntiAlias = true
            wavePaints.add(paint)
        }

        return wavePaints.size
    }

    fun clearWave(){
        synchronized(waveConfigs){
            waveConfigs.clear()
        }
        wavePaints.clear()
    }

    fun startAnimation(){
        startAnimateTime = SystemClock.uptimeMillis()
        removeCallbacks(animateTicker)
        ViewCompat.postOnAnimation(this@DynamicSineWaveView, animateTicker)
    }

    fun stopAnimation(){
        removeCallbacks(animateTicker)
    }

    fun requestUpdateFrame(){
        lock.withLock {
            requestFutureChange = true
            requestCondition.signal()
        }
    }

    override fun onDetachedFromWindow() {
        stopAnimation()
        super.onDetachedFromWindow()
    }

    override fun onDraw(canvas: Canvas?) {
        if(!transferPathsQueue.isEmpty()){
            currentPaths = transferPathsQueue.poll()

            if(currentPaths.size != wavePaints.size){
                throw RuntimeException("Generated paths size " + currentPaths.size +
                        " not match the paints size " + wavePaints.size)
            }
        }

        if (currentPaths.isEmpty())
            return

        wavePathScale.setScale(viewWidth.toFloat(),viewHeight * baseWaveAmplitudeScale)
        wavePathScale.postTranslate(0f,(viewHeight/2).toFloat())
        for(i in currentPaths.indices){
            drawingPath.set(currentPaths[i])
            drawingPath.transform(wavePathScale)
            canvas!!.drawPath(drawingPath,wavePaints[i])
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        viewWidth = width
        viewHeight = height
    }

    private fun createComputationThread(): Thread{
        return object:Thread(){
            override fun run() {
                while(true){
                    lock.withLock {
                        try {
                            if (!requestFutureChange)
                                requestCondition.await()
                            if (!requestFutureChange)
                                return
                            requestFutureChange = false
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                    }
                    if(tick()){
                        postInvalidate()
                    }
                }

            }
        }
    }

    private fun tick(): Boolean {
        val waveList: MutableList<Wave>
        val newPaths: MutableList<Path>

        synchronized(waveConfigs) {
            if (waveConfigs.size < 2)
                return false

            waveList = ArrayList(waveConfigs.size)
            newPaths = ArrayList(waveConfigs.size)

            for (o in waveConfigs) {
                waveList.add(Wave(o))
            }
        }

        val currentTime = SystemClock.uptimeMillis()
        val t = (currentTime - startAnimateTime) / 1000f

        var maxCycle = 0f
        var maxAmplitude = 0f
        for (i in waveList.indices) {
            val w = waveList[i]
            if (w.cycle > maxCycle)
                maxCycle = w.cycle
            if (w.amplitude > maxAmplitude && i > 0)
                maxAmplitude = w.amplitude
        }
        val pointCount = (POINTS_FOR_CYCLE * maxCycle).toInt()

        val baseWave = waveList[0]
        waveList.removeAt(0)

        val normal = baseWave.amplitude / maxAmplitude
        val baseWavePoints = generateSineWave(baseWave.amplitude, baseWave.cycle, -baseWave.speed * t, pointCount)

        for (w in waveList) {
            val space = -w.speed * t

            val wavePoints = generateSineWave(w.amplitude, w.cycle, space, pointCount)
            if (wavePoints.size != baseWavePoints.size) {
                throw RuntimeException("base wave point size " + baseWavePoints.size +
                        " not match the sub wave point size " + wavePoints.size)
            }

            // multiple up
            for (i in wavePoints.indices) {
                val p = wavePoints[i]
                val base = baseWavePoints[i]
                p.set(p.x, p.y * base.y * normal)
            }

            val path = generatePathForCurve(wavePoints)
            newPaths.add(path)
        }

        // offer the new wave paths & post invalidate to redraw.
        transferPathsQueue.offer(newPaths)
        return true
    }

    private fun generateSineWave(amplitude: Float, cycle: Float, offset: Float, pointCount: Int): List<PointF> {
        var count = pointCount
        if (count <= 0)
            count = (POINTS_FOR_CYCLE * cycle).toInt()
        val T = 1.0 / cycle
        val f = 1f / T

        val points = java.util.ArrayList<PointF>(count)
        if (count < 2)
            return points

        val dx = 1.0 / (count - 1)
        var x = 0.0
        while (x <= 1) {
            val y = amplitude * Math.sin(2.0 * Math.PI * f * (x + offset))
            points.add(PointF(x.toFloat(), y.toFloat()))
            x += dx
        }

        return points
    }

    private fun generatePathForCurve(points: List<PointF>): Path {
        val path = Path()
        if (points.isEmpty())
            return path

        path.reset()

        var prevPoint = points[0]
        for (i in points.indices) {
            val point = points[i]

            if (i == 0) {
                path.moveTo(point.x, point.y)
            } else {
                val midX = (prevPoint.x + point.x) / 2
                val midY = (prevPoint.y + point.y) / 2

                if (i == 1) {
                    path.lineTo(midX, midY)
                } else {
                    path.quadTo(prevPoint.x, prevPoint.y, midX, midY)
                }
            }
            prevPoint = point
        }
        path.lineTo(prevPoint.x, prevPoint.y)

        return path
    }



}