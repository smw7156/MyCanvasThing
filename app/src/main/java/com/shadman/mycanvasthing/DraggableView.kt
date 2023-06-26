package com.shadman.mycanvasthing

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.util.Log

class DraggableView(var x: Float, var y: Float) {

    private val TAG = "DraggableView"
    private var actionDown: Boolean = false
    private val circleRadius = 30F

    fun draw(canvas: Canvas, paint: Paint) {
//        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        canvas.drawCircle(x,y,circleRadius,paint)
    }

    fun drawCircleAt(canvas: Canvas, x: Float, y: Float, paint: Paint) {
        canvas.drawCircle(x,y,circleRadius,paint)
    }
    
    fun setActionDown(actionDown: Boolean) {
        this.actionDown = actionDown
    }

    fun getActionDown(): Boolean {
        return this.actionDown
    }

    fun setPosition(x: Float, y: Float) {
        this.x = x - circleRadius
        this.y = y - circleRadius
    }

    fun isTouched(x: Float, y: Float): Boolean {
        Log.i(TAG, "smw: checking isTouched X(${this.x}--${this.x + (circleRadius*2)}")
        Log.i(TAG, "smw: checking isTouched Y(${this.y}--${this.y + (circleRadius*2)}")
        val isXInside = x > (this.x - circleRadius) && x < (this.x + (circleRadius*3))
        val isYInside = y > (this.y - circleRadius) && y < (this.y + (circleRadius*3))
        return isXInside && isYInside
    }
}