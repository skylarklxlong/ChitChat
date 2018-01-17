package online.himakeit.baseframe.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.AbsListView;
import android.widget.ScrollView;

import com.socks.library.KLog;

/**
 * @author：LiXueLong
 * @date：2018/1/10
 * @mail1：skylarklxlong@outlook.com
 * @mail2：li_xuelong@126.com
 * @des:
 */
public class DragPointView extends AppCompatTextView {

    private boolean initBgFlag;
    private OnDragListencer dragListencer;
    private int backgroundColor = Color.parseColor("#ff0000");
    private PointView pointView;
    private int x, y, r;
    private ViewGroup scrollParent;
    private int[] p = new int[2];

    public DragPointView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initbg();
    }

    public OnDragListencer getDragListencer() {
        return dragListencer;
    }

    public void setDragListencer(OnDragListencer dragListencer) {
        this.dragListencer = dragListencer;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        /**
         * 保持view的宽高一样
         */
        if (w != h) {
            int x = Math.max(w, h);
            setMeasuredDimension(x, x);
        }
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        /**
         * DragPointView背景设置为绘制出来的圆
         */
        DragPointView.this.setBackgroundDrawable(createStateListDrawable((getHeight() > getWidth() ? getHeight()
                : getWidth()) / 2, backgroundColor));
    }

    /**
     * 设置居中，DragPointView背景设置为绘制出来的圆
     */
    private void initbg() {
        setGravity(Gravity.CENTER);
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @SuppressWarnings("deprecation")
            @Override
            public boolean onPreDraw() {
                if (!initBgFlag) {
                    DragPointView.this.setBackgroundDrawable(createStateListDrawable(
                            (getHeight() > getWidth() ? getHeight() : getWidth()) / 2, backgroundColor));
                    initBgFlag = true;
                    return false;
                }
                return true;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        View root = getRootView();
        if (root == null || !(root instanceof ViewGroup)) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                root.getLocationOnScreen(p);
                scrollParent = getScrollParent();
                if (scrollParent != null) {
                    scrollParent.requestDisallowInterceptTouchEvent(true);
                }
                int[] location = new int[2];
                getLocationOnScreen(location);
                x = location[0] + (getWidth() / 2) - p[0];
                y = location[1] + (getHeight() / 2) - p[1];
                r = (getWidth() + getHeight()) / 4;
                pointView = new PointView(getContext());
                pointView.setLayoutParams(new ViewGroup.LayoutParams(root.getWidth(), root.getHeight()));
                setDrawingCacheEnabled(true);
                pointView.catchBitmap = getDrawingCache();
                pointView.setLocation(x, y, r, event.getRawX() - p[0], event.getRawY() - p[1]);
                ((ViewGroup) root).addView(pointView);
                setVisibility(View.INVISIBLE);
                break;
            case MotionEvent.ACTION_MOVE:
                pointView.refrashXY(event.getRawX() - p[0], event.getRawY() - p[1]);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (scrollParent != null) {
                    scrollParent.requestDisallowInterceptTouchEvent(false);
                }
                if (!pointView.broken) {
                    /**
                     * 没有拉断
                     */
                    pointView.cancel();
                } else if (pointView.nearby) {
                    /**
                     * 拉断了,但是又回去了
                     */
                    pointView.cancel();
                } else {
                    /**
                     * 彻底拉断了
                     */
                    pointView.broken();
                }
                break;
            default:
                break;
        }
        return true;
    }

    private ViewGroup getScrollParent() {
        View p = this;
        while (true) {
            View v;
            try {
                v = (View) p.getParent();
            } catch (ClassCastException e) {
                return null;
            }
            if (v == null) {
                return null;
            }
            if (v instanceof AbsListView || v instanceof ScrollView || v instanceof ViewPager) {
                return (ViewGroup) v;
            }
            p = v;
        }
    }

    public interface OnDragListencer {
        void onDragOut();
    }

    class PointView extends View {
        private Bitmap catchBitmap;
        private Circle c1;
        private Circle c2;
        private Paint paint;
        private Path path = new Path();
        /**
         * 10倍半径距离视为拉断
         */
        private int maxDistance = 8;
        /**
         * 是否拉断过
         */
        private boolean broken;
        /**
         * 放手的时候是否拉断
         */
        private boolean out;
        private boolean nearby;
        private int brokenProgress;

        public PointView(Context context) {
            super(context);
            init();
        }

        public void init() {
            paint = new Paint();
            paint.setColor(backgroundColor);
            /**
             * 抗锯齿，平滑
             */
            paint.setAntiAlias(true);
        }

        public void setLocation(float c1X, float c1Y, float r, float endX, float endY) {
            broken = false;
            c1 = new Circle(c1X, c1Y, r);
            c2 = new Circle(endX, endY, r);
        }

        /**
         * 刷新拖动圆的坐标
         *
         * @param x
         * @param y
         */
        public void refrashXY(float x, float y) {
            c2.x = x;
            c2.y = y;
            /**
             * 以前的半径应该根据距离缩小点了
             * 计算出距离
             */
            double distance = c1.getDistance(c2);
            int rate = 10;
            c1.r = (float) ((c2.r * c2.r * rate) / (distance + (c2.r * rate)));
            KLog.e("info", "c1: " + c1.r);
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawColor(Color.TRANSPARENT);
            if (out) {
                /**
                 * 放手的时候拉断了
                 */
                float dr = c2.r / 2 + c2.r * 4 * (brokenProgress / 100f);
                KLog.e("info", "dr" + dr);
                /**
                 * 拉断时的爆炸效果，中间一个点，四个角分别四个点
                 * 中、左上、左下、右上、右下
                 */
                canvas.drawCircle(c2.x, c2.y, c2.r / (brokenProgress + 1), paint);
                canvas.drawCircle(c2.x - dr, c2.y - dr, c2.r / (brokenProgress + 2), paint);
                canvas.drawCircle(c2.x + dr, c2.y - dr, c2.r / (brokenProgress + 2), paint);
                canvas.drawCircle(c2.x - dr, c2.y + dr, c2.r / (brokenProgress + 2), paint);
                canvas.drawCircle(c2.x + dr, c2.y + dr, c2.r / (brokenProgress + 2), paint);
            } else {
                /**
                 * 放手的时候没有拉断
                 */
                if (catchBitmap == null || (catchBitmap != null && catchBitmap.isRecycled())) {
                    return;
                }
                canvas.drawBitmap(catchBitmap, c2.x - c2.r, c2.y - c2.r, paint);
                path.reset();
                float deltaX = c2.x - c1.x;
                float deltaY = -(c2.y - c1.y);
                double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                double sin = deltaY / distance;
                double cos = deltaX / distance;
                nearby = distance < c2.r * maxDistance;
                if (nearby && !broken) {
                    canvas.drawCircle(c1.x, c1.y, c1.r, paint);
                    path.moveTo((float) (c1.x - c1.r * sin), (float) (c1.y - c1.r * cos));
                    path.lineTo((float) (c1.x + c1.r * sin), (float) (c1.y + c1.r * cos));
                    path.quadTo((c1.x + c2.x) / 2, (c1.y + c2.y) / 2, (float) (c2.x + c2.r * sin), (float) (c2.y + c2.r
                            * cos));
                    path.lineTo((float) (c2.x - c2.r * sin), (float) (c2.y - c2.r * cos));
                    path.quadTo((c1.x + c2.x) / 2, (c1.y + c2.y) / 2, (float) (c1.x - c1.r * sin), (float) (c1.y - c1.r
                            * cos));
                    canvas.drawPath(path, paint);
                } else {
                    /**
                     * 已经拉断了
                     */
                    broken = true;
                }
            }

        }

        public void cancel() {
            int duration = 150;
            AnimatorSet set = new AnimatorSet();
            ValueAnimator animx = ValueAnimator.ofFloat(c2.x, c1.x);
            animx.setDuration(duration);
            animx.setInterpolator(new OvershootInterpolator(2));
            animx.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    c2.x = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            ValueAnimator animy = ValueAnimator.ofFloat(c2.y, c1.y);
            animy.setDuration(duration);
            animy.setInterpolator(new OvershootInterpolator(2));
            animy.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    c2.y = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            set.playTogether(animx, animy);
            set.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    ViewGroup vg = (ViewGroup) PointView.this.getParent();
                    vg.removeView(PointView.this);
                    DragPointView.this.setVisibility(View.VISIBLE);
                }
            });
            set.start();

        }

        public void broken() {
            out = true;
            int duration = 500;
            ValueAnimator a = ValueAnimator.ofInt(0, 100);
            a.setDuration(duration);
            a.setInterpolator(new LinearInterpolator());
            a.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    brokenProgress = (int) animation.getAnimatedValue();
                    invalidate();
                }
            });
            a.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    ViewGroup vg = (ViewGroup) PointView.this.getParent();
                    vg.removeView(PointView.this);
                }
            });
            a.start();
            if (dragListencer != null) {
                dragListencer.onDragOut();
            }
        }

        /**
         * 圆实体类
         * x：圆心坐标x
         * y：圆心坐标y
         * r：圆半径
         */
        class Circle {
            float x;
            float y;
            float r;

            public Circle(float x, float y, float r) {
                this.x = x;
                this.y = y;
                this.r = r;
            }

            /**
             * 获取新圆对象与初始圆对象圆心间距离
             *
             * @param c 新圆对象
             * @return
             */
            public double getDistance(Circle c) {
                float deltaX = x - c.x;
                float deltaY = y - c.y;
                double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                return distance;
            }
        }

    }

    /**
     * StateListDrawable
     * 让您将许多图形图像分配给一个可绘制的图形，并通过一个字符串ID值来交换可见项
     * 解析xml文件中的selector标签
     * <p>
     * 这里的操作相当于就是新建了一个shape类型的xml,圆
     *
     * @param radius 圆角角度
     * @param color  填充颜色
     * @return StateListDrawable 对象
     */
    public static StateListDrawable createStateListDrawable(int radius, int color) {
        StateListDrawable bg = new StateListDrawable();
        /**
         * 倾斜度
         */
        GradientDrawable gradientStateNormal = new GradientDrawable();
        gradientStateNormal.setColor(color);
        gradientStateNormal.setShape(GradientDrawable.RECTANGLE);
        gradientStateNormal.setCornerRadius(50);
        gradientStateNormal.setStroke(0, 0);
        bg.addState(View.EMPTY_STATE_SET, gradientStateNormal);
        return bg;
    }
}
