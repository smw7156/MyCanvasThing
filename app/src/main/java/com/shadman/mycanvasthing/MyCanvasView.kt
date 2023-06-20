package com.shadman.mycanvasthing

import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min

private const val STROKE_WIDTH = 12f // has to be float
private const val POINT_WIDTH = 24f // has to be float
private const val TAG = "MyCanvasView"
private const val POLYGON_SIDES = 8

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
//        extraCanvas.save()


//        // Calculate a rectangular frame around the picture.
//        val inset = 40
//        frame = Rect(inset, inset, width - inset, height - inset)

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(extraBitmap, 0f, 0f, null)
//        canvas.drawRect(frame, paint)

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
        if (polygonPoints.size >= POLYGON_SIDES) {
            return
        }
//        path.reset()
//        path.moveTo(motionTouchEventX, motionTouchEventY)
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
         * if a triangle is created (i.e. 3 sides), and then a point is added, we have to decide where to insert that point in polygon
         * for the let than 3 points simply add point and draw polygon*/
        if (polygonPoints.size >= 3) {
            updatePolygon(polygonPoints, Pair(currentX, currentY))
        }else {

            /**
             * Drawing points as described in @param tap2paint
             */
            extraCanvas.drawPoint(currentX, currentY, tap2Paint)
            invalidate()
            polygonPoints.add(Pair(currentX, currentY))
        }


        /**
         * drawing lines, connecting dots as we are drawing it
         */
        drawPolygon(polygonPoints)


//        if (pointsCount > 1) {
//            if (pointsCount > 2) {
//                extraCanvas.drawARGB(0,0,0,backgroundColor)
////                invalidate()
//                path.moveTo(polygonPoints[0].first, polygonPoints[0].second)
//                for (i in 1 until polygonPoints.size) {
////                    extraCanvas.drawLine(
////                        polygonPoints[i - 1].first,
////                        polygonPoints[i - 1].second,
////                        polygonPoints[i].first,
////                        polygonPoints[i].second,
////                        tapPaint
////                    )
//                    path.lineTo(polygonPoints[i].first,polygonPoints[i].second)
//
//                }
////                extraCanvas.drawLine(
////                    polygonPoints[polygonPoints.size - 1].first,
////                    polygonPoints[polygonPoints.size - 1].second,
////                    polygonPoints[0].first,
////                    polygonPoints[0].second,
////                    tapPaint
////                )
//                path.close()
//                extraCanvas.drawPath(path,tap2Paint)
//            } else {
//                path.reset()
//                path.moveTo(polygonPoints[0].first, polygonPoints[0].second)
//                path.lineTo(
//                    polygonPoints[polygonPoints.size - 1].first,
//                    polygonPoints[polygonPoints.size - 1].second,
//                )
//            }
//        }
    }

    private fun updatePolygon(oldPolygonPoints: List<Pair<Float, Float>>, newPoints: Pair<Float, Float>) {

        // find polyPoint closest to the newPoints
        var c = extraCanvas.width.toDouble()
        var closestPointIndex = 0
        for ((i, point) in oldPolygonPoints.withIndex()) {
            val distance = hypot(
                (newPoints.first - point.first).toString().toDouble(),
                (newPoints.second - point.second).toString().toDouble()
            )
            Log.d(TAG, "smw: distance from point $i is $distance")
            if (c > distance) {
                c = distance
                closestPointIndex = i
            }
        }
        Log.d(TAG, "smw: closest point $closestPointIndex is $c")
        //--------------------------------------

        var indexBeforeClosestPoint = if (closestPointIndex > 0) (closestPointIndex - 1) else oldPolygonPoints.lastIndex
        var indexAfterClosestPoint = if (closestPointIndex == oldPolygonPoints.lastIndex) 0 else (closestPointIndex + 1)
        Log.d(TAG, "smw: indexBeforeClosestPoint is $indexBeforeClosestPoint")
        Log.d(TAG, "smw: indexAfterClosestPoint is $indexAfterClosestPoint")

        val case1Intersect = doIntersect(
            oldPolygonPoints[indexBeforeClosestPoint],
            newPoints,
            polygonPoints[indexAfterClosestPoint],
            polygonPoints[closestPointIndex]
        )
        Log.i(TAG, "smw: case1 intersect $case1Intersect")

        if (case1Intersect) {
            polygonPoints.add((closestPointIndex+1),newPoints)
        } else {
            polygonPoints.add((closestPointIndex),newPoints)
        }
        Log.i(TAG, "smw: updated polygon points are $polygonPoints")

    }

    /**
     * Just Drawing polygon from the given points, in the given orders of the list
     */
    private fun drawPolygon(polygonPoints: List<Pair<Float, Float>>) {
        if (polygonPoints.isNullOrEmpty() || polygonPoints.size < 3) {
            return
        }
        // Clearing canvas, keeping the background color
        extraCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        invalidate()

        extraCanvas.drawARGB(0, 0, 0, backgroundColor)
        path.reset()
        path.moveTo(polygonPoints[0].first, polygonPoints[0].second)
        for (i in 1 until polygonPoints.size) {
            path.lineTo(polygonPoints[i].first, polygonPoints[i].second)
        }
        path.close()
        extraCanvas.drawPath(path, tap2Paint)
        invalidate()
    }

    // @author https://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/
    private fun onSegment(p: Pair<Float, Float>, q: Pair<Float, Float>, r: Pair<Float, Float>): Boolean {
        return q.first <= max(p.first, r.first) && q.first >= Math.min(p.first, r.first) && q.second <= max(
            p.second,
            r.second
        ) && q.second >= min(p.second, r.second)
    }

    // To find orientation of ordered triplet (p, q, r).
    // The function returns following values
    // 0 --> p, q and r are collinear
    // 1 --> Clockwise
    // 2 --> Counterclockwise
    // @author https://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/
    private fun orientation(p: Pair<Float, Float>, q: Pair<Float, Float>, r: Pair<Float, Float>): Int {
        // See https://www.geeksforgeeks.org/orientation-3-ordered-points/
        // for details of below formula.
        val valu = (q.second - p.second) * (r.first - q.first) -
                (q.first - p.first) * (r.second - q.second)
        if (valu == 0F) return 0 // collinear
        return if (valu > 0) 1 else 2 // clock or counterclock wise
    }

    /**
     * Function that returns true if line segment 'p1q1' and 'p2q2' intersect.
     * @author https://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/
     */
    private fun doIntersect(
        p1: Pair<Float, Float>,
        q1: Pair<Float, Float>,
        p2: Pair<Float, Float>,
        q2: Pair<Float, Float>
    ): Boolean {

//        //correct Y coordinates - might not be required
//        var p1 = Pair(ip1.first,height - ip1.second)
//        var q1 = Pair(iq1.first,height - iq1.second)
//        var p2 = Pair(ip2.first,height - ip2.second)
//        var q2 = Pair(iq2.first,height - iq2.second)


        // Find the four orientations needed for general and
        // special cases
        val o1 = orientation(p1, q1, p2)
        val o2 = orientation(p1, q1, q2)
        val o3 = orientation(p2, q2, p1)
        val o4 = orientation(p2, q2, q1)

        // General case
        if (o1 != o2 && o3 != o4) return true

        // Special Cases
        // p1, q1 and p2 are collinear and p2 lies on segment p1q1
        if (o1 == 0 && onSegment(p1, p2, q1)) return true

        // p1, q1 and q2 are collinear and q2 lies on segment p1q1
        if (o2 == 0 && onSegment(p1, q2, q1)) return true

        // p2, q2 and p1 are collinear and p1 lies on segment p2q2
        if (o3 == 0 && onSegment(p2, p1, q2)) return true

        // p2, q2 and q1 are collinear and q1 lies on segment p2q2
        return o4 == 0 && onSegment(p2, q1, q2)
        // Doesn't fall in any of the above cases
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