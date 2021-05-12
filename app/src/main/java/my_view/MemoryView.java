package my_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import my_class.one.Memory;

public class MemoryView extends View {

    private Memory memory;
    private int width;
    private int height;

    private Paint red_paint = null;
    private Paint green_paint = null;
    private Paint black_paint = null;
    private TextPaint textPaint = null;


    public MemoryView(Context context) {
        super(context);
        init();
    }

    public MemoryView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MemoryView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    //@SuppressLint("ResourceAsColor")
    private void init(){
        red_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        red_paint.setColor(Color.RED);
        green_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        green_paint.setColor(Color.GREEN);
        black_paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        black_paint.setColor(Color.BLACK);
        black_paint.setStrokeWidth(5);
        textPaint=new TextPaint();
        textPaint.setTextSize(50);
        textPaint.setColor(Color.BLACK);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Rect rect = new Rect();
        rect.left= 0;
        rect.top = height/3;
        rect.right = width;
        rect.bottom = height/3*2;
        canvas.drawRect(rect,green_paint);
        int lengthMax = 0;
        int start_process;
        if(memory!=null){
            lengthMax=memory.getLength();
            start_process=memory.getStart();
            canvas.drawText("0",rect.left+textPaint.measureText("0"),(int)(1.0/6*height),textPaint);
            canvas.drawText(""+lengthMax,rect.right-textPaint.measureText(""+lengthMax),(int)(1.0/6*height),textPaint);
            rect.right=start_process*width/lengthMax;
            canvas.drawRect(rect,red_paint);
            canvas.drawText(start_process+"",rect.right-textPaint.measureText(start_process+"")/2,(int)(1.0/6*height),textPaint);
            memory=memory.getNext();
        }
        int length;
        while (memory!=null){
            if(memory.getStatus()){
                start_process=memory.getStart();
                length=memory.getLength();
                rect.left = start_process*width/lengthMax;
                rect.right = (start_process+length)*width/lengthMax;
                canvas.drawRect(rect,red_paint);
                canvas.drawLine(rect.left,rect.top,rect.left,rect.bottom,black_paint);
                canvas.drawLine(rect.right,rect.top,rect.right,rect.bottom,black_paint);
            }
            memory=memory.getNext();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.width=this.getMeasuredWidth();
        this.height=this.getMeasuredHeight();
        Log.d("MyTest", "onMeasure: width--"+this.width);
        Log.d("MyTest", "onMeasure: height--"+this.height);
    }

    public void setMemory(Memory memory) {
        this.memory = memory;
        invalidate();
    }
}
