package piechar.test.com.testpiechar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Created by zhangjian on 2019/4/10 10:01
 * E-Mail：zhangjian1@keking.cn
 */
public class PieChartView extends View {
    private float[] percents = new float[]{(float) 0.25, (float) 0.25, (float) 0.25, (float) 0.25};
    private int[] colors = new int[]{Color.BLACK, Color.YELLOW, Color.MAGENTA, Color.CYAN};
    private HashMap[] areas = new HashMap[]{new HashMap(), new HashMap(), new HashMap(), new HashMap()};
    private Paint bgPaint = new Paint();

    public PieChartView(Context context) {
        super(context);
    }

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.chartPieView);
        int bgColor = typedArray.getColor(R.styleable.chartPieView_backgroundColor, Color.GRAY);
        bgPaint.setColor(bgColor);
        bgPaint.setAntiAlias(true);
        bgPaint.setStyle(Paint.Style.FILL);
    }

    private float lastPercent = 0;
    private int radius;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        lastPercent = 0;
        radius = getMeasuredWidth() / 2;
        canvas.drawCircle(radius, radius, radius, bgPaint);
        if (percents == null) return;
        for (int i = 0; i < percents.length; i++) {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(colors[i]);
            paint.setStyle(Paint.Style.FILL);
            Log.e("PieChartView", lastPercent + "percent " + percents[i]);
            @SuppressLint("DrawAllocation") RectF rectF = new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight());
            float sweepAngle = percents[i] * 360;
            float starAngle = lastPercent * 360;
            Log.e("PieChartView", starAngle + "angle " + sweepAngle);
            canvas.drawArc(rectF, starAngle, sweepAngle, true, paint);
            lastPercent += percents[i];
            areas[i].put("start", starAngle);
            areas[i].put("end", sweepAngle + starAngle);
            areas[i].put("area", "第" + (i + 1) + "区域");
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float height = event.getY();
            float width = event.getX();
            double angle = getClickPoint(height, width);
            for (HashMap map : areas) {
                float startAngle = (float) map.get("start");
                float endAngle = (float) map.get("end");
                String area = (String) map.get("area");
                if (angle >= startAngle && angle <= endAngle) {
                    Toast.makeText(getContext(), area + "被点击了，角度为：" + angle, Toast.LENGTH_SHORT).show();
                    break;
                }
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    private double getClickPoint(float height, float width) {
        if (width >= radius && height < radius) {//第一象限
            return Math.toDegrees(Math.atan(((radius - height) / (width - radius)))) + 270;
        } else if (width > radius && height >= radius) {//第二象限
            return Math.toDegrees(Math.atan(((width - radius) / (height - radius))));
        } else if (width <= radius && height > radius) {//第三象限
            return Math.toDegrees(Math.atan(((height - radius) / (radius - width)))) + 90;
        } else if (width < radius && height <= radius) {//第四象限
            return Math.toDegrees(Math.atan(((radius - width) / (radius - height)))) + 180;
        }
        return 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        Log.e("PieChartView", "width = " + width + ",height = " + height);
        Log.e("PieChartView", Math.min(width, height) + "，" + Math.min(height, width));
        setMeasuredDimension(Math.min(width, height), Math.min(height, width));
    }
}
