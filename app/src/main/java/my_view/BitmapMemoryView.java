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

import java.util.Set;

import androidx.annotation.Nullable;
import my_class.two.Bitmap;

public class BitmapMemoryView extends View {
    private Bitmap bitmap;
    private boolean doubleMode;//双列模式，默认单列模式
    private int width;
    private int height;

    private TextPaint textPaint = null;
    private Paint greenPaint = null;
    private Paint redPaint = null;
    private Paint blackPaint = null;
    private Paint yellowPaint = null;

    private int[] visits;

    public BitmapMemoryView(Context context) {
        super(context);
        init();
    }

    public BitmapMemoryView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BitmapMemoryView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init(){
        doubleMode=false;
        textPaint = new TextPaint();
        textPaint.setTextSize(50);
        textPaint.setColor(Color.BLACK);

        greenPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        greenPaint.setColor(Color.GREEN);

        redPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        redPaint.setColor(Color.RED);

        yellowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        yellowPaint.setColor(Color.YELLOW);


        blackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        blackPaint.setStrokeWidth(3);
        blackPaint.setColor(Color.BLACK);
        bitmap = null;
        visits = null;
    }




    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect rect = new Rect();      //控件边界
        rect.left= 0;
        rect.top = 0;
        rect.right = width;
        rect.bottom = height;
        //int
        canvas.drawLine(rect.left,rect.top,rect.right,rect.top,blackPaint);
        canvas.drawLine(rect.left,rect.bottom,rect.right,rect.bottom,blackPaint);
        canvas.drawLine(rect.left,rect.top,rect.left,rect.bottom,blackPaint);
        canvas.drawLine(rect.right,rect.top,rect.right,rect.bottom,blackPaint);
        //边界收缩
        rect.bottom-=7;
        rect.right-=7;
        int temp = (int) blackPaint.getStrokeWidth();
        rect.left += temp;
        rect.right -= temp;
        rect.top += temp;
        rect.bottom -= temp;
        if(bitmap == null){
            return;
        }
        Rect r = new Rect();
        if(doubleMode){
            //双列模式
            //h_步长、v_步长
            int h_step = (rect.right - rect.left) / 18;
            int v_step = (int) ((rect.bottom - rect.top) / (Math.ceil(bitmap.getMapLength()/2.0)+1));
            int h_gap = h_step / 7;//参数
            int v_gap = v_step / 7;//参数

            r.left = rect.left + h_gap;
            r.right = r.left + h_step - h_gap;
            r.top = rect.top + v_gap;
            r.bottom = r.top + v_step - h_gap;
            //横向数字
            String s = null;
            for(int j=15;j>=0;j--){
                r.left += h_step;
                r.right += h_step;
                s = (j%8)+"";
                canvas.drawText(s,(r.right+r.left)/2-textPaint.measureText(s)/2,r.bottom-v_gap,textPaint);
                if(j % 8 == 0){
                    r.left += h_step;
                    r.right += h_step;
                }
            }
            r.top += v_step;
            r.bottom += v_step;
            //复原位置
            r.left = rect.left + h_gap;
            r.right = r.left + h_step - h_gap;
            for(int i = 0;i<bitmap.getMapLength();i++){
                s = i+"";
                canvas.drawText(s,(r.right+r.left)/2-textPaint.measureText(s)/2,r.bottom-v_gap,textPaint);
                r.left += h_step;
                r.right += h_step;
                for(int j=7;j>=0;j--){
                    if(bitmap.getBit(i,j) == 1){
                        canvas.drawRect(r,redPaint);

                    }else {
                        canvas.drawRect(r,greenPaint);
                    }
                    r.left += h_step;
                    r.right += h_step;
                }
                if(i%2==1){
                    r.left = rect.left + h_gap;
                    r.right = r.left + h_step - h_gap;
                    r.top += v_step;
                    r.bottom += v_step;
                }
            }
            //访问2
            if(visits != null){
                for(int x :this.visits){
                    r.left = rect.left + h_gap;
                    r.right = r.left + h_step - h_gap;
                    r.top = rect.top + v_gap + v_step;
                    r.bottom = r.top + v_step - h_gap;

                    r.top += x / 16 * v_step;
                    r.bottom += x / 16* v_step;
                    int t = x % 16;
                    if(t >= 8){
                        t = (7 - x % 8) + 9;
                    }else {
                        t = (7 - x % 8);
                    }
                    r.left += (t + 1) * h_step;//+1
                    r.right += (t + 1) * h_step;//+1
                    canvas.drawRect(r,yellowPaint);
                }
                visits = null;
            }
        }else {
            //单列模式
            //h_步长、v_步长
            int h_step = (rect.right - rect.left) / 9;
            int v_step = (rect.bottom - rect.top) / (bitmap.getMapLength()+1);
            int h_gap = h_step / 7;//参数
            int v_gap = v_step / 7;//参数
            r.left = rect.left + h_gap;
            r.right = r.left + h_step - h_gap;
            r.top = rect.top + v_gap;
            r.bottom = r.top + v_step - h_gap;
            //横向数字
            String s = null;
            for(int j=7;j>=0;j--){
                r.left += h_step;
                r.right += h_step;
                s = j+"";
                canvas.drawText(s,(r.right+r.left)/2-textPaint.measureText(s)/2,r.bottom-v_gap,textPaint);
            }
            r.top += v_step;
            r.bottom += v_step;
            //复原位置
            r.left = rect.left + h_gap;
            r.right = r.left + h_step - h_gap;
            for(int i = 0;i<bitmap.getMapLength();i++){
                s = i+"";
                canvas.drawText(s,(r.right+r.left)/2-textPaint.measureText(s)/5,r.bottom-v_gap,textPaint);
                r.left += h_step;
                r.right += h_step;
                for(int j=7;j>=0;j--){
                    if(bitmap.getBit(i,j) == 1){
                        canvas.drawRect(r,redPaint);

                    }else {
                        canvas.drawRect(r,greenPaint);
                    }
                    r.left += h_step;
                    r.right += h_step;
                }
                r.left = rect.left + h_gap;
                r.right = r.left + h_step - h_gap;
                r.top += v_step;
                r.bottom += v_step;
            }
            if(visits != null){
                for(int x :this.visits){
                    r.top = rect.top + v_gap + v_step;
                    r.bottom = r.top + v_step - h_gap;
                    r.left = rect.left + h_gap;
                    r.right = r.left + h_step - h_gap;

                    r.top += x / 8 * v_step;
                    r.bottom += x / 8 * v_step;
                    r.left += (8 - x%8) * h_step;
                    r.right = (9 - x%8) * h_step;
                    canvas.drawRect(r,yellowPaint);
                }
                visits = null;
            }
        }//单列模式
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


    public void setDoubleMode(boolean doubleMode) {
        this.doubleMode = doubleMode;
        invalidate();
    }

    public boolean isDoubleMode() {
        return doubleMode;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        invalidate();
    }

    public void update(int[] visits){
        this.visits=visits;
        invalidate();
    }
}
