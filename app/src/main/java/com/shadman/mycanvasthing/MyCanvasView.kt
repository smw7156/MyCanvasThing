package com.shadman.mycanvasthing

import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat

private const val STROKE_WIDTH = 12f // has to be float
private const val POINT_WIDTH = 24f // has to be float
private const val TAG = "MyCanvasView"

class MyCanvasView(context: Context) : View(context) {


    /**
     * These are your bitmap and canvas for caching what has been drawn before.
     */
    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap

    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorBackground, null)

    private val drawColor = ResourcesCompat.getColor(resources, R.color.colorPaint, null)
    private val drawTapColor = ResourcesCompat.getColor(resources, R.color.tapColorPaint, null)
    private val drawTap2Color = ResourcesCompat.getColor(resources, R.color.teal_700, null)

    /**
     * In order to draw, you need a Paint object that specifies how things are styled when drawn,
     * and a Path that specifies what is being draw.
     */
    // Set up the paint with which to draw on dragging the pointer.
    private val paint = Paint().apply {
        color = drawColor
        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true
        // Dithering affects how colors with higher-precision than the device are down-sampled.
        isDither = true
        style = Paint.Style.STROKE // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = STROKE_WIDTH // default: Hairline-width (really thin)
    }

    // Set up the paint with which to draw on Tapping
    private val tap2Paint = paint.apply {
        color = drawTap2Color
        strokeWidth = POINT_WIDTH
    }
    private val tapPaint = Paint().apply {
        color = drawTapColor
        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true
        // Dithering affects how colors with higher-precision than the device are down-sampled.
        isDither = true
        style = Paint.Style.STROKE // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.BUTT // default: BUTT
        strokeWidth = STROKE_WIDTH // default: Hairline-width (really thin)
    }

    // Path representing the drawing so far
    private val drawing = Path()

    // Path representing what's currently being drawn
    private val curPath = Path()

    private lateinit var frame: Rect


    private var path = Path()

    /**
     * variables for caching the x and y coordinates of the current touch event (the MotionEvent coordinates).
     * Initialize them to 0f.
     */
    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f

    /**
     * add variables to cache the latest x and y values. After the user stops moving and lifts their touch,
     * these are the starting point for the next path (the next segment of the line to draw).
     */
    private var currentX = 0f
    private var currentY = 0f

    /**
     * Adding touch tolerance
     * e.g.: If the finger has moved less than the touchTolerance distance, don't draw.
     */
    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    /**
     * counting points drawn by users,
     * plot line if 2 points are drawn*/
    private var pointsCount = 0
    private val MAX_POINTS_ALLOWED = 2

    /**
     * Point of the polygon to draw
     */
    private var polygonPoints = mutableListOf<Pair<Float, Float>>()

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        if (::extraBitmap.isInitialized) extraBitmap.recycle()
        Log.i(TAG, "smw: onSizeChanged: nwHeight: $height, olHeight: $oldHeight, nwWidth: $width, olWidth: $oldWidth")

        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(backgroundColor)

        // Calculate a rectangular frame around the picture.
        val inset = 40
        frame = Rect(inset, inset, width - inset, height - inset)

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(extraBitmap, 0f, 0f, null)
        canvas.drawRect(frame, paint)

        /*// Draw the drawing so far
        canvas.drawPath(drawing, paint)
        // Draw any current squiggle
        canvas.drawPath(curPath, paint)*/
        // Draw a frame around the canvas
//        canvas.drawRect(frame, paint)

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        motionTouchEventX = event.x
        motionTouchEventY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP -> touchUp()
        }
        return true
    }

    private fun touchStart() {
        path.reset()
        path.moveTo(motionTouchEventX, motionTouchEventY)
        currentX = motionTouchEventX
        currentY = motionTouchEventY
        Log.i(TAG, "smw onTouchStart: X: $currentX , Y:$currentY")

        /* if (pointsCount >= MAX_POINTS_ALLOWED) {
             polygonPoints.clear()
             pointsCount = 0
             extraCanvas.drawColor(backgroundColor)
             return
         }*/
        /**
         * Drawing points as described in @param tap2paint
         */
        extraCanvas.drawPoint(currentX, currentY, tap2Paint)
        pointsCount++
        polygonPoints.add(Pair(currentX, currentY))

        extraCanvas
        /*if (pointsCount == MAX_POINTS_ALLOWED) {
            extraCanvas.drawLine(
                polygonPoints[0].first,
                polygonPoints[0].second,
                polygonPoints[1].first,
                polygonPoints[1].second,
                tapPaint
            )
        }*/
        invalidate()
    }

    private fun touchMove() {

        // Working code
        /* val dx = Math.abs(motionTouchEventX - currentX)
         val dy = Math.abs(motionTouchEventY - currentY)
         if (dx >= touchTolerance || dy >= touchTolerance) {
            */
        /**
         * // QuadTo() adds a quadratic bezier from the last point,
         * // approaching control point (x1,y1), and ending at (x2,y2).
         * quadTo(4 agrs) in use instead of lineTo(2 args), as quad draw smoother curves
         */
    /*
            path.quadTo(currentX, currentY, (motionTouchEventX + currentX) / 2, (motionTouchEventY + currentY) / 2)
            currentX = motionTouchEventX
            currentY = motionTouchEventY
            // Draw the path in the extra bitmap to cache it.
            extraCanvas.drawPath(path, paint)
        }
        invalidate()
        */

    }

    private fun touchUp() {
        // Reset the path so it doesn't get drawn again.
//        path.reset()


        // Add the current path to the drawing so far
        drawing.addPath(curPath)
        // Rewind the current path for the next touch
        curPath.reset()

    }


}