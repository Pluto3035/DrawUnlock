package com.example.drawunlock

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.lang.StringBuilder

/**
 * @Description
 * 代码高手
 */
class TouchUnlockView:View {
    //圆的半径
    private var radius = 0f
    //间距
    private var padding = 0f

    //保存所有9个点的信息对象
    private val dotInfos = mutableListOf<DotInfo>()
    //保存所有被点亮的点的信息
    private val selectedItems= mutableListOf<DotInfo>()
    //保存上一个被点亮的圆点信息
    private var lastSelectedItem:DotInfo?= null

    //记录移动时触摸点的坐标 移动线条的终点
    private val endPoint = Point()

    //记录线条的路径
    private val linePath = Path()
    //线的画笔
    private val linePaint= Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    //圆圈内部的白色遮盖的Paint
    private val innerCirclePaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    //记录密码
    private var password = StringBuilder()


    //代码创建
    constructor(context: Context):super(context){}
    //xml创建
    constructor(context: Context,attrs: AttributeSet?):super(context,attrs){}
    //style
    constructor(context: Context,attrs: AttributeSet?,style:Int):super(context,attrs,style){}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //初始化
        init()
    }

    //绘制具体内容
    override fun onDraw(canvas: Canvas?) {
        //绘制线
        canvas?.drawPath(linePath,linePaint)

        //绘制移动的线
        if(!endPoint.equals(0,0)){
            canvas?.drawLine(lastSelectedItem!!.cx,lastSelectedItem!!.cy,
                endPoint.x.toFloat(), endPoint.y.toFloat(),
                linePaint
            )
        }

        //绘制9个点
      drawNineDot(canvas)
    }

    //绘制9个点
    fun drawNineDot(canvas: Canvas?){
     for(info in dotInfos){
         canvas?.drawCircle(info.cx,info.cy,info.radius,info.paint)
         canvas?.drawCircle(info.cx,info.cy,info.radius - 2,innerCirclePaint)
         if(info.isSelected){
             canvas?.drawCircle(info.cx,info.cy,info.innerCircleRadius - 2,info.paint)
         }
     }
    }

    //初始化
   private fun init(){
        //第一个点的中心点坐标
        var cx= 0f
        var cy= 0f
        //计算半径和间距
        //判断用户设置当前view的尺寸 确保在正方形区域绘制
        if(measuredWidth >= measuredHeight){
            //半径
            radius = measuredHeight/10f
            //间距
            padding= (measuredHeight-6*radius)/4
            cx = ( measuredWidth-measuredHeight)/2f + radius + padding
            cy = padding + radius
        }else{
            radius = measuredWidth/10f
            padding = (measuredHeight-6*radius)/4
            cx= padding + radius
            cy = (measuredHeight-measuredWidth)/2f + radius + padding
        }

        //设置9个点组成的Path
        for(row in 0..2){
            for (colum in 0..2){
            DotInfo(cx+ colum*(2*radius + padding),
                cy + row*(2*radius + padding),
                radius,
                row*3+colum+1
                ).also {
                dotInfos.add(it)
            }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        //获取触摸点的坐标
        val x= event?.x
        val y = event?.y
        when(event?.action){
             MotionEvent.ACTION_DOWN ->{
                  //判断这个点是否在某个矩形区域内
                 containsPoint(x!!,y!!).also {
                     if(it!= null){
                         //点亮这个点
                         selectItem(it)
                         //linePath的起点为这个点的中心点
                         linePath.moveTo(it.cx,it.cy)
                     }
                 }
             }
            MotionEvent.ACTION_MOVE ->{
                //判断这个点是否在某个矩形区域内
                containsPoint(x!!,y!!).also {
                    if(it!= null){
                        //当前触摸点已经在某个圆点内部
                        if(!it.isSelected){
                            //没有点亮
                            //是不是第一个点
                            if(lastSelectedItem==null){
                                //第一个点
                                //linePath的起点为这个点的中心点
                                linePath.moveTo(it.cx,it.cy)
                            }else{
                                 //从上一个点到当前点画线
                                linePath.lineTo(it.cx,it.cy)
                            }
                            //点亮这个点
                            selectItem(it)
                        }else{
                            //触摸点在外部
                            if(lastSelectedItem != null){
                             endPoint.set(x.toInt(),y.toInt())
                                invalidate()
                            }
                        }
                    }
                }
            }
            MotionEvent.ACTION_UP ->{
              reset()
            }
        }
        return true
    }

    //查找某个矩形区域是否包含触摸点
    private fun containsPoint(x:Float,y:Float):DotInfo?{
        for(item in dotInfos){
            if(item.rect.contains(x.toInt(),y.toInt())){
                     return item
            }
        }
        return null
    }

    //重设
       private fun reset(){
        //将颜色改回正常颜色
        for(item in selectedItems){
            item.isSelected = false
        }
        //线条重设
        linePath.reset()
        invalidate()
        //清空
        selectedItems.clear()
        //清空密码
       password.clear()
    }
    //点亮某个点
    private fun selectItem(item:DotInfo){
        //改变这个点绘制的颜色
      item.isSelected = true
        //立刻刷新 重新绘制
        invalidate()
        //保存这个点亮的点
        selectedItems.add(item)
        //记录这个点
        lastSelectedItem = item
        //设置endPoint为空
        endPoint.set(0,0)
        //记录当前的密码
        password.append(item.tag)
    }
}