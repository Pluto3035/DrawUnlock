package com.example.drawunlock

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect

/**
 * @Description
 *  管理每一个圆点的具体样式
 *  中心点
 *  半径
 */
class DotInfo(val cx:Float, val cy:Float,val radius:Float,val tag:Int) {
    //这个点的画笔
    val paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL
    }

    //选中的点的半径
    var innerCircleRadius = 0f

    //记录是否被选中
    var isSelected = false

    //这个点的矩形区域
    val rect = Rect()
    //初始化代码块
    init {
        //确定矩形区域
        rect.left = (cx - radius).toInt()
        rect.right = (cx + radius).toInt()
        rect.top = (cy - radius).toInt()
        rect.bottom = (cy + radius).toInt()

        innerCircleRadius = radius/3.5f
    }

   fun setColor(color:Int){
       paint.color = color
   }


}