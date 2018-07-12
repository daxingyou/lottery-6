package com.dawoo.lotterybox.view.activity.chart.pk10;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import com.dawoo.lotterybox.R;

import java.util.List;

/**
 * Created by b on 18-4-5.
 */

public class ThreeLineRecyclerView extends RecyclerView{


    private List<LinesBean> integers;
    private Paint mPaint;
    private Context mContext;
    private float lastX = 0;
    private float lastY = 0;
    private boolean isDrawLine = true;
    private float offset;

    public ThreeLineRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public ThreeLineRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ThreeLineRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(10);
        mPaint.setStyle(Paint.Style.STROKE);
        offset = dp2px(mContext, 10);
    }

    public void setList(List<LinesBean> integers) {
        this.integers = integers;
    }

    public void isDrawLine(boolean isDraw) {
        this.isDrawLine = isDraw;
    }

    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);

    }

    private void drawLine(Canvas c) {
        if (!isDrawLine || integers == null)
            return;
        int firstPosition = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        int lastPosition = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();

//        if (lastPosition + 1 > integers.size()) {
//            return;
//        }
        mPaint.setColor(ContextCompat.getColor(mContext, R.color.line_bg));
        for (int i = firstPosition; i < lastPosition + 1; i++) {
            Log.e("LineReyccle", "" + i);
            LinearLayout childView = (LinearLayout) getLayoutManager().findViewByPosition(i);
            if (childView != null) {
                float childGroupViewX = childView.getLeft();
                float childGroupViewY = childView.getTop();
                if(i > integers.size()-1) {
                    continue;
                }
                int count = childView.getChildCount();
                Log.i("childCount",count + "");
                View view = childView.getChildAt(integers.get(i).getBallIndex());
                if (view != null) {
//                    int[] position = new int[2];
//                    view.getLocationInWindow(position);
                    float x = view.getX() + childGroupViewX + view.getWidth() / 2;
                    float y = view.getY() + childGroupViewY + view.getHeight() / 2;
                    if (lastX != 0 && y > lastY) {
                        Point point1 = getFirstPoint(lastX, lastY, x, y, offset);
                        Point point2 = getSecondPoint(lastX, lastY, x, y, offset);
                        c.drawLine(point1.getX(), point1.getY(), point2.getX(), point2.getY(), mPaint);
                    }
                    lastX = x;
                    lastY = y;
                }
            }
        }
        lastX = 0;
        lastY = 0;


        mPaint.setColor(ContextCompat.getColor(mContext, R.color.big_small_single_double));
        for (int i = firstPosition; i < lastPosition + 1; i++) {
            Log.e("LineReyccle", "" + i);
            LinearLayout childView = (LinearLayout) getLayoutManager().findViewByPosition(i);
            if (childView != null) {
                float childGroupViewX = childView.getLeft();
                float childGroupViewY = childView.getTop();
                if(i > integers.size()-1) {
                    continue;
                }
                int count = childView.getChildCount();
                Log.i("childCount",count + "");
                View view = childView.getChildAt(integers.get(i).getBigSmall());
                if (view != null) {
//                    int[] position = new int[2];
//                    view.getLocationInWindow(position);
                    float x = view.getX() + childGroupViewX + view.getWidth() / 2;
                    float y = view.getY() + childGroupViewY + view.getHeight() / 2;
                    if (lastX != 0 && y > lastY) {
                        Point point1 = getFirstPoint(lastX, lastY, x, y, offset);
                        Point point2 = getSecondPoint(lastX, lastY, x, y, offset);

                        c.drawLine(point1.getX(), point1.getY(), point2.getX(), point2.getY(), mPaint);
                    }
                    lastX = x;
                    lastY = y;
                }
            }
        }
        lastX = 0;
        lastY = 0;


        for (int i = firstPosition; i < lastPosition + 1; i++) {
            Log.e("LineReyccle", "" + i);
            LinearLayout childView = (LinearLayout) getLayoutManager().findViewByPosition(i);
            if (childView != null) {
                float childGroupViewX = childView.getLeft();
                float childGroupViewY = childView.getTop();
                if(i > integers.size()-1) {
                    return;
                }
                int count = childView.getChildCount();
                Log.i("childCount",count + "");
                View view = childView.getChildAt(integers.get(i).getSingleDouble());
                if (view != null) {
//                    int[] position = new int[2];
//                    view.getLocationInWindow(position);
                    float x = view.getX() + childGroupViewX + view.getWidth() / 2;
                    float y = view.getY() + childGroupViewY + view.getHeight() / 2;
                    if (lastX != 0 && y > lastY) {
                        Point point1 = getFirstPoint(lastX, lastY, x, y, offset);
                        Point point2 = getSecondPoint(lastX, lastY, x, y, offset);

                        c.drawLine(point1.getX(), point1.getY(), point2.getX(), point2.getY(), mPaint);
                    }
                    lastX = x;
                    lastY = y;
                }
            }
        }
        lastX = 0;
        lastY = 0;

    }

    @Override
    public void onDrawForeground(Canvas canvas) {
        super.onDrawForeground(canvas);
        drawLine(canvas);
    }

    private float getSlope(float x2, float y2, float x1, float y1) {
        float slope = (y2 - y1) / (x2 - x1);

        return slope;
    }

    private Point getFirstPoint(float x1, float y1, float x4, float y4, float r) {

        Point point = null;
        if (x4 - x1 > 0) {
            float slope = (x4 - x1) / (y4 - y1);
            float tempx = (float) (Math.sqrt(Math.pow(slope * r, 2) / (Math.pow(slope, 2) + 1)));
            float x2 = x1 + tempx;
            float tempy = (float) (Math.sqrt(r * r / (slope * slope + 1)));
            float y2 = y1 + tempy;
            point = new Point(x2, y2);
        } else if (x4 - x1 < 0) {
            float slope = (x4 - x1) / (y4 - y1);
            float tempx = (float) (Math.sqrt(Math.pow(slope * r, 2) / (Math.pow(slope, 2) + 1)));
            float x2 = x1 - tempx;
            float tempy = (float) (Math.sqrt(r * r / (slope * slope + 1)));
            float y2 = y1 + tempy;
            point = new Point(x2, y2);
        } else if (x4 == x1) {
            point = new Point(x1, y1 + r);
        }
        return point;

    }

    private Point getSecondPoint(float x1, float y1, float x4, float y4, float r) {
        Point point = null;
        if (x4 - x1 > 0) {
            float slope = (x4 - x1) / (y4 - y1);
            float tempx = (float) (Math.sqrt(Math.pow(slope * r, 2) / (Math.pow(slope, 2) + 1)));
            float x3 = x4 - tempx;
            float tempy = (float) (Math.sqrt(r * r / (slope * slope + 1)));
            float y3 = y4 - tempy;
            point = new Point(x3, y3);

        } else if (x4 - x1 < 0) {
            float slope = (x4 - x1) / (y4 - y1);
            float tempx = (float) (Math.sqrt(Math.pow(slope * r, 2) / (Math.pow(slope, 2) + 1)));
            float x3 = x4 + tempx;
            float tempy = (float) (Math.sqrt(r * r / (slope * slope + 1)));
            float y3 = y4 - tempy;
            point = new Point(x3, y3);

        } else if (x4 == x1) {
            point = new Point(x4, y4 - r);
        }

        return point;
    }


    public class Point {
        private float x;
        private float y;

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

    }


    /**
     * dp转px
     */
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }
}
