package cn.iwgang.calendardemo.countdownview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

/**
 * 倒计时View
 * Created by iWgang on 15/9/16.
 */
public class CountdownView extends View {
    private Context mContext;
    private int mDay, mHour, mMinute, mSecond, mMillisecond;
    private OnCountdownEndListener mOnCountdownEndListener;
    private CountDownTimer mCountDownTimer;

    private boolean isShowDay;
    private boolean isShowHour;
    private boolean isShowMillisecond;
    private boolean isHideTimeBackground;

    private Paint mTimeTextPaint;
    private Paint mSuffixPaint;
    private Paint mTimeTextBgPaint;
    private Paint mTimeTextBgDivisionLinePaint;

    private RectF mHourBgRectF;
    private RectF mMinuteBgRectF;
    private RectF mSecondBgRectF;
    private RectF mMillisecondBgRectF;

    private float mTimeTextWidth;
    private float mTimeTextHeight;
    private float mTimeTextSize;
    private float mTimeBgSize;
    private int mTimeTextColor;
    private int mTimeBgCircularSize;

    // 后缀
    private String mSuffix;
    private String mSuffixDay;
    private String mSuffixHour;
    private String mSuffixMinute;
    private String mSuffixSecond;
    private String mSuffixMillisecond;
    private int mSuffixTextColor;
    private float mSuffixTextSize;
    private float mSuffixDayTextWidth;
    private float mSuffixHourTextWidth;
    private float mSuffixMinuteTextWidth;
    private float mSuffixSecondTextWidth;
    private float mSuffixMillisecondTextWidth;

    public CountdownView(Context context) {
        this(context, null);
    }

    public CountdownView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountdownView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.mContext = context;

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CountdownView);
        mTimeTextSize = ta.getDimension(R.styleable.CountdownView_timeTextSize, sp2px(12));
        mTimeTextColor = ta.getColor(R.styleable.CountdownView_timeTextColor, Color.WHITE);
        isHideTimeBackground = ta.getBoolean(R.styleable.CountdownView_isHideTimeBackground, false);
        isShowDay = ta.getBoolean(R.styleable.CountdownView_isShowDay, false);
        isShowHour = ta.getBoolean(R.styleable.CountdownView_isShowHour, true);
        isShowMillisecond = ta.getBoolean(R.styleable.CountdownView_isShowMillisecond, false);

        mSuffixTextSize = ta.getDimension(R.styleable.CountdownView_suffixTextSize, sp2px(12));
        mSuffixTextColor = ta.getColor(R.styleable.CountdownView_suffixTextColor, Color.BLACK);
        mSuffix = ta.getString(R.styleable.CountdownView_suffix);
        mSuffixDay = ta.getString(R.styleable.CountdownView_suffixDay);
        mSuffixHour = ta.getString(R.styleable.CountdownView_suffixHour);
        mSuffixMinute = ta.getString(R.styleable.CountdownView_suffixMinute);
        mSuffixSecond = ta.getString(R.styleable.CountdownView_suffixSecond);
        mSuffixMillisecond = ta.getString(R.styleable.CountdownView_suffixMillisecond);
        ta.recycle();

        mTimeBgCircularSize = dp2px(2);

        // 初始化画笔
        initPaint();

        // 初始化后缀
        initSuffix();

        // 测量时间文字高度
        Rect rect = new Rect();
        mTimeTextPaint.getTextBounds("00", 0, 2, rect);
        mTimeTextWidth = rect.width();
        mTimeTextHeight = rect.height();

        mTimeBgSize = mTimeTextWidth + (dp2px(2) * 4);

        // 初始化时间背景的RectF对象
        initTimeBgRect();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        // 初始化时间文字画笔
        mTimeTextPaint = new Paint();
        mTimeTextPaint.setAntiAlias(true);
        mTimeTextPaint.setColor(mTimeTextColor);
        mTimeTextPaint.setTextSize(mTimeTextSize);

        // 初始化分割文字画笔
        mSuffixPaint = new Paint();
        mSuffixPaint.setAntiAlias(true);
        mSuffixPaint.setColor(mSuffixTextColor);
//        mSuffixPaint.setTextAlign(Paint.Align.CENTER);
        mSuffixPaint.setTextSize(mSuffixTextSize);

        // 初始化时间背景画笔
        mTimeTextBgPaint = new Paint();
        mTimeTextBgPaint.setStyle(Paint.Style.FILL);
        mTimeTextBgPaint.setAntiAlias(true);
        mTimeTextBgPaint.setColor(0xFF444444);

        // 初始化时间背景中间的分割线画笔
        mTimeTextBgDivisionLinePaint = new Paint();
        mTimeTextBgDivisionLinePaint.setAntiAlias(true);
        mTimeTextBgDivisionLinePaint.setColor(Color.parseColor("#30FFFFFF"));
        mTimeTextBgDivisionLinePaint.setStrokeWidth(dp2px(0.5f));
    }

    private void initSuffix() {
        boolean isSuffixNull = true;
        float mSuffixTextWidth = 0;
        if (!TextUtils.isEmpty(mSuffix)) {
            isSuffixNull = false;
            mSuffixTextWidth = mSuffixPaint.measureText(mSuffix);
        }

        if (isShowDay) {
            if (!TextUtils.isEmpty(mSuffixDay)) {
                mSuffixDayTextWidth = mSuffixPaint.measureText(mSuffixDay);
            } else {
                if (!isSuffixNull) {
                    mSuffixDay = mSuffix;
                    mSuffixDayTextWidth = mSuffixTextWidth;
                }
            }
        }

        if (isShowHour) {
            if (!TextUtils.isEmpty(mSuffixHour)) {
                mSuffixHourTextWidth = mSuffixPaint.measureText(mSuffixHour);
            } else {
                if (!isSuffixNull) {
                    mSuffixHour = mSuffix;
                    mSuffixHourTextWidth = mSuffixTextWidth;
                }
            }
        }

        if (!TextUtils.isEmpty(mSuffixMinute)) {
            mSuffixMinuteTextWidth = mSuffixPaint.measureText(mSuffixMinute);
        } else {
            if (!isSuffixNull) {
                mSuffixMinute = mSuffix;
                mSuffixMinuteTextWidth = mSuffixTextWidth;
            }
        }

        if (!TextUtils.isEmpty(mSuffixSecond)) {
            mSuffixSecondTextWidth = mSuffixPaint.measureText(mSuffixSecond);
        } else {
            if (!isSuffixNull) {
                mSuffixSecond = mSuffix;
                mSuffixSecondTextWidth = mSuffixTextWidth;
            }
        }

        if (!TextUtils.isEmpty(mSuffixMillisecond)) {
            mSuffixMillisecondTextWidth = mSuffixPaint.measureText(mSuffixMillisecond);
        }
    }

    /**
     * 初始化时间背景的RectF对象
     */
    private void initTimeBgRect() {
        if (!isHideTimeBackground) {
            float mSecondLeft;

            if (isShowHour) {
                // 显示小时

                // 计算分钟x轴
                float mMinuteLeft = mTimeBgSize + mSuffixHourTextWidth;
                // 计算秒钟x轴
                mSecondLeft = mMinuteLeft + mTimeBgSize + mSuffixMinuteTextWidth;

                // 初始化小时背景RectF
                mHourBgRectF = new RectF(0, 0, mTimeBgSize, mTimeBgSize);
                // 初始化分钟背景RectF
                mMinuteBgRectF = new RectF(mMinuteLeft, 0, mTimeBgSize + mMinuteLeft, mTimeBgSize);
                // 初始化秒钟背景RectF
                mSecondBgRectF = new RectF(mSecondLeft, 0, mTimeBgSize + mSecondLeft, mTimeBgSize);
            } else {
                // 不显示小时

                // 计算秒钟x轴
                mSecondLeft = mTimeBgSize + mSuffixMinuteTextWidth;

                // 初始化分钟背景RectF
                mMinuteBgRectF = new RectF(0, 0, mTimeBgSize, mTimeBgSize);
                // 初始化秒钟背景RectF
                mSecondBgRectF = new RectF(mSecondLeft, 0, mTimeBgSize + mSecondLeft, mTimeBgSize);
            }

            if (isShowMillisecond) {
                // 计算毫秒x轴
                float mMillisecondLeft = mTimeBgSize + mSuffixSecondTextWidth + mSecondLeft;

                // 初始化毫秒背景RectF
                mMillisecondBgRectF = new RectF(mMillisecondLeft, 0, mTimeBgSize + mMillisecondLeft, mTimeBgSize);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        float timeWidth = isHideTimeBackground ? mTimeTextWidth : mTimeBgSize;
        float width = timeWidth * 2;
        width += mSuffixMinuteTextWidth;

        if (isShowDay) {
            width += timeWidth;
            width += mSuffixDayTextWidth;
        }

        if (isShowHour) {
            width += timeWidth;
            width += mSuffixHourTextWidth;
        }

        if (isShowMillisecond) {
            width += timeWidth;
            width += mSuffixSecondTextWidth;
            width += mSuffixMillisecondTextWidth;
        }

        setMeasuredDimension((int)width, isHideTimeBackground ? (int)mTimeTextHeight : (int)mTimeBgSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float mSecondLeft;
        float mTextYPos;

        if (isHideTimeBackground) {
            // 无背景
            mTextYPos = mTimeTextHeight;

            if (isShowHour) {
                // 显示小时
                // 画小时文字
                canvas.drawText(formatNum(mHour), 0, mTextYPos, mTimeTextPaint);
                if (mSuffixHourTextWidth > 0) {
                    // 画小时后缀
                    canvas.drawText(mSuffixHour, mTimeTextWidth, mTextYPos, mSuffixPaint);
                }

                // 计算分钟x轴
                float mMinuteLeft = mTimeTextWidth + mSuffixHourTextWidth;
                // 画分钟文字
                canvas.drawText(formatNum(mMinute), mMinuteLeft, mTextYPos, mTimeTextPaint);
                if (mSuffixMinuteTextWidth > 0) {
                    // 画分钟后缀
                    canvas.drawText(mSuffixMinute, mMinuteLeft + mTimeTextWidth, mTextYPos, mSuffixPaint);
                }

                // 计算秒钟x轴
                mSecondLeft = mTimeTextWidth + mSuffixMinuteTextWidth + mMinuteLeft;
                // 画秒钟文字
                canvas.drawText(formatNum(mSecond), mSecondLeft + (mTimeTextWidth / 2) - (mTimeTextWidth / 2), mTextYPos, mTimeTextPaint);
            } else {
                // 不显示小时
                // 画分钟文字
                canvas.drawText(formatNum(mMinute), 0, mTextYPos, mTimeTextPaint);
                if (mSuffixMinuteTextWidth > 0) {
                    // 画分钟后缀
                    canvas.drawText(mSuffixMinute, mTimeTextWidth, mTextYPos, mSuffixPaint);
                }

                // 计算秒钟x轴
                mSecondLeft = mTimeTextWidth + mSuffixMinuteTextWidth;
                // 画秒钟文字
                canvas.drawText(formatNum(mSecond), mSecondLeft, mTextYPos, mTimeTextPaint);
            }
        } else {
            // 有背景
            mTextYPos = mTimeBgSize / 2 + mTimeTextHeight / 2;

            if (isShowHour) {
                // 显示小时
                // 画小时背景
                canvas.drawRoundRect(mHourBgRectF, mTimeBgCircularSize, mTimeBgCircularSize, mTimeTextBgPaint);
                // 画小时背景中间的横线
                canvas.drawLine(0, mTimeBgSize / 2, mTimeBgSize, mTimeBgSize / 2, mTimeTextBgDivisionLinePaint);
                // 画小时文字
                canvas.drawText(formatNum(mHour), mTimeBgSize / 2 - mTimeTextWidth / 2, mTextYPos, mTimeTextPaint);
                if (mSuffixHourTextWidth > 0) {
                    // 画小时后缀
                    canvas.drawText(mSuffixHour, mTimeBgSize, mTextYPos, mSuffixPaint);
                }

                // 计算分钟x轴
                float mMinuteLeft = mTimeBgSize + mSuffixHourTextWidth;
                // 画分钟背景
                canvas.drawRoundRect(mMinuteBgRectF, mTimeBgCircularSize, mTimeBgCircularSize, mTimeTextBgPaint);
                // 画分钟背景中间的横线
                canvas.drawLine(mMinuteLeft, mTimeBgSize / 2, mTimeBgSize + mMinuteLeft, mTimeBgSize / 2, mTimeTextBgDivisionLinePaint);
                // 画分钟文字
                canvas.drawText(formatNum(mMinute), mMinuteLeft + (mTimeBgSize / 2) - (mTimeTextWidth / 2), mTextYPos, mTimeTextPaint);
                if (mSuffixMinuteTextWidth > 0) {
                    // 画分钟后缀
                    canvas.drawText(mSuffixMinute, mMinuteLeft + mTimeBgSize, mTextYPos, mSuffixPaint);
                }

                // 计算秒钟x轴
                mSecondLeft = mMinuteLeft + mTimeBgSize + mSuffixMinuteTextWidth;
                // 画秒钟背景
                canvas.drawRoundRect(mSecondBgRectF, mTimeBgCircularSize, mTimeBgCircularSize, mTimeTextBgPaint);
                // 画秒钟背景中间的横线
                canvas.drawLine(mSecondLeft, mTimeBgSize / 2, mTimeBgSize + mSecondLeft, mTimeBgSize / 2, mTimeTextBgDivisionLinePaint);
                // 画秒钟文字
                canvas.drawText(formatNum(mSecond), mSecondLeft + (mTimeBgSize / 2) - (mTimeTextWidth / 2), mTextYPos, mTimeTextPaint);
            } else {
                // 不显示小时
                // 画分钟背景
                canvas.drawRoundRect(mMinuteBgRectF, mTimeBgCircularSize, mTimeBgCircularSize, mTimeTextBgPaint);
                // 画分钟背景中间的横线
                canvas.drawLine(0, mTimeBgSize / 2, mTimeBgSize, mTimeBgSize / 2, mTimeTextBgDivisionLinePaint);
                // 画分钟文字
                canvas.drawText(formatNum(mMinute), mTimeBgSize / 2 - mTimeTextWidth / 2, mTextYPos, mTimeTextPaint);
                if (mSuffixMinuteTextWidth > 0) {
                    // 画分钟后缀
                    canvas.drawText(mSuffixMinute, mTimeBgSize, mTextYPos, mSuffixPaint);
                }

                // 计算秒钟x轴
                mSecondLeft = mTimeBgSize + mSuffixMinuteTextWidth;
                // 画秒钟背景
                canvas.drawRoundRect(mSecondBgRectF, mTimeBgCircularSize, mTimeBgCircularSize, mTimeTextBgPaint);
                // 画秒钟背景中间的横线
                canvas.drawLine(mSecondLeft, mTimeBgSize / 2, mTimeBgSize + mSecondLeft, mTimeBgSize / 2, mTimeTextBgDivisionLinePaint);
                // 画秒钟文字
                canvas.drawText(formatNum(mSecond), mSecondLeft + (mTimeBgSize / 2) - (mTimeTextWidth / 2), mTextYPos, mTimeTextPaint);
            }
        }

        // 判断显示毫秒
        if (isShowMillisecond) {
            // 计算毫秒x轴
            if (isHideTimeBackground) {
                // 无背景
                if (mSuffixSecondTextWidth > 0) {
                    // 画秒钟后缀
                    canvas.drawText(mSuffixSecond, mSecondLeft + mTimeTextWidth, mTextYPos, mSuffixPaint);
                }

                float mMillisecondLeft = mTimeTextWidth + mSuffixSecondTextWidth + mSecondLeft;
                // 画毫秒文字
                canvas.drawText(formatMillisecond(), mMillisecondLeft, mTextYPos, mTimeTextPaint);
                if (mSuffixMillisecondTextWidth > 0) {
                    // 画毫秒后缀
                    canvas.drawText(mSuffixMillisecond, mMillisecondLeft + mTimeTextWidth, mTextYPos, mSuffixPaint);
                }
            } else {
                // 有背景
                if (mSuffixSecondTextWidth > 0) {
                    // 画秒钟后缀
                    canvas.drawText(mSuffixSecond, mSecondLeft + mTimeBgSize, mTextYPos, mSuffixPaint);
                }

                float mMillisecondLeft = mTimeBgSize + mSuffixSecondTextWidth + mSecondLeft;
                // 画毫秒背景
                canvas.drawRoundRect(mMillisecondBgRectF, mTimeBgCircularSize, mTimeBgCircularSize, mTimeTextBgPaint);
                // 画毫秒背景中间的横线
                canvas.drawLine(mMillisecondLeft, mTimeBgSize / 2, mTimeBgSize + mMillisecondLeft, mTimeBgSize / 2, mTimeTextBgDivisionLinePaint);
                // 画毫秒文字
                canvas.drawText(formatMillisecond(), mMillisecondLeft + (mTimeBgSize / 2) - (mTimeTextWidth / 2), mTextYPos, mTimeTextPaint);
                if (mSuffixMillisecondTextWidth > 0) {
                    // 画毫秒后缀
                    canvas.drawText(mSuffixMillisecond, mMillisecondLeft + mTimeBgSize, mTextYPos, mSuffixPaint);
                }
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }

    /**
     * 启动倒计时
     * @param millisecond 毫秒数
     */
    public void start(long millisecond) {
        if (millisecond <= 0) {
            return ;
        }

        updateShow(millisecond);

        if (null != mCountDownTimer) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }

        mCountDownTimer = new CountDownTimer(millisecond, 10) {
            @Override
            public void onFinish() {
                // 倒计时结束
                // 回调
                if (null != mOnCountdownEndListener) {
                    mOnCountdownEndListener.onEnd();
                }
            }

            @Override
            public void onTick(long millisUntilFinished) {
                updateShow(millisUntilFinished);
            }
        };
        mCountDownTimer.start();
    }

    public void stop() {
        if (null != mCountDownTimer) mCountDownTimer.cancel();
    }

    public void setShowHourView(boolean isShowHour) {
        this.isShowHour = isShowHour;
        invalidate();
    }

    public void setShowMillisecondView(boolean isShowMillisecond) {
        this.isShowMillisecond = isShowMillisecond;
        invalidate();
    }

    public void allShowZero() {
        mHour = 0;
        mMinute = 0;
        mSecond = 0;
        mMillisecond = 0;

        invalidate();
    }

    public void setOnCountdownEndListener(OnCountdownEndListener onCountdownEndListener) {
        this.mOnCountdownEndListener = onCountdownEndListener;
    }

    private void updateShow(long ms) {
        mDay = (int)(ms / (1000 * 60 * 60 * 24));
        mHour = (int)((ms % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        mMinute = (int)((ms % (1000 * 60 * 60)) / (1000 * 60));
        mSecond = (int)((ms % (1000 * 60)) / 1000);
        mMillisecond = (int)(ms % 1000);

        invalidate();
    }

    private String formatNum(int time) {
        return time < 10 ? "0"+time : String.valueOf(time);
    }

    private String formatMillisecond() {
        String retMillisecondStr;

        if (mMillisecond > 99) {
            retMillisecondStr = String.valueOf(mMillisecond).substring(0, 2);
        } else if (mMillisecond <= 9) {
            retMillisecondStr = "0" + mMillisecond;
        } else {
            retMillisecondStr = String.valueOf(mMillisecond);
        }

        return retMillisecondStr;
    }

    public interface OnCountdownEndListener {
        void onEnd();
    }

    public int dp2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public float sp2px(float spValue) {
        final float scale = mContext.getResources().getDisplayMetrics().scaledDensity;
        return spValue * scale;
    }

}
