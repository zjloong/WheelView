package com.loong.wheelview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Describe the function of the class
 *
 * @author zhujinlong@ichoice.com
 * @date 2016/9/12
 * @time 14:41
 * @description Describe the place where the class needs to pay attention.
 */
public class WheelView<T> extends RecyclerView {

    private int itemCount,itemHeight,lineHeight,x1,x2,y1,y2,w,h;
    private int lineColor,textColor,startColor,endColor;
    private Paint linePaint,wheelPaint;
    private LinearLayoutManager layoutManager;
    private List<T> data = new ArrayList<>();
    private T item;
    private OnItemSelectedListener<T> onItemSelectedListener;
    private AdapterImpl adapter;

    public WheelView(Context context) {
        this(context,null);
    }

    public WheelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public WheelView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if(attrs != null){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WheelView);
            itemCount = typedArray.getInteger(R.styleable.WheelView_itemCount,7);
            itemHeight = typedArray.getDimensionPixelSize(R.styleable.WheelView_itemHeight,dip2px(context,40));
            lineHeight = typedArray.getDimensionPixelSize(R.styleable.WheelView_lineHeight,dip2px(context,1));
            lineColor = typedArray.getColor(R.styleable.WheelView_lineColor,Color.BLUE);
            textColor = typedArray.getColor(R.styleable.WheelView_itemTextColor,Color.DKGRAY);
            startColor = typedArray.getColor(R.styleable.WheelView_startColor,Color.LTGRAY);
            endColor = typedArray.getColor(R.styleable.WheelView_endColor,Color.WHITE);
            typedArray.recycle();
        }
        if(itemCount % 2 == 0){
            itemCount += 1;
        }
        //线条画笔
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //颜色
        linePaint.setColor(lineColor);
        //宽度
        linePaint.setStrokeWidth(lineHeight);
        //设置填充
        linePaint.setStyle(Paint.Style.FILL);
        //背景色画笔
        wheelPaint = new Paint();
        //固定为垂直线性布局
        layoutManager = new LinearLayoutManager(context);
        setLayoutManager(layoutManager);
        //保证不会滚动半格
        new LinearSnapHelper().attachToRecyclerView(this);
        //监听
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == SCROLL_STATE_IDLE){
                    item = data.get((layoutManager.findFirstVisibleItemPosition() + layoutManager.findLastVisibleItemPosition()) / 2 - itemCount / 2);
                    if(onItemSelectedListener != null){
                        onItemSelectedListener.onItemSelected(item);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, View.MeasureSpec.makeMeasureSpec(itemCount * itemHeight, View.MeasureSpec.EXACTLY));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(x2 == 0){
            x1 = 0;
            x2 = w;
            y1 = itemHeight * (itemCount / 2);
            y2 = y1 + itemHeight;
            this.w = w;
            this.h = h;
            //画笔设置渐变色(镜像模式),用于绘制背景色,达到 wheel的视觉效果
            wheelPaint.setShader(new LinearGradient(0,0,0,h/2,startColor,endColor, Shader.TileMode.MIRROR));
        }
    }

    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
        c.drawRect(0,0,w,h,wheelPaint);
        c.drawLine(x1,y1,x2,y1,linePaint);
        c.drawLine(x1,y2,x2,y2,linePaint);
    }

    public void setAdapter(AdapterImpl adapter) {
        this.adapter = adapter;
        super.setAdapter(new WheelAdapter());
    }

    public void setData(List<T> data) {
        this.data.clear();
        if(data != null){
            this.data.addAll(data);
        }
        item = data.get(0);
        if(onItemSelectedListener != null){
            onItemSelectedListener.onItemSelected(item);
        }
        getAdapter().notifyDataSetChanged();
    }

    public void setOnItemSelectedListener(OnItemSelectedListener<T> onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private class WheelAdapter extends Adapter{

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1,parent,false)) {};
        }

        @Override
        public int getItemViewType(int position) {
            return position < itemCount / 2  || getItemCount() - position <= itemCount / 2 ? 0 : 1;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            TextView textView = (TextView) holder.itemView.findViewById(android.R.id.text1);
            ((LayoutParams) textView.getLayoutParams()).height = itemHeight;
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(textColor);
            textView.setSingleLine(true);
            textView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
            if(getItemViewType(position) == 0){
                textView.setText("");
            }else {
                if(adapter != null){
                    adapter.onBindItem(textView,data.get(position - itemCount / 2));
                }
            }
        }

        @Override
        public int getItemCount() {
            return data.isEmpty() ? 0 : data.size() + itemCount - 1;
        }
    }

    public interface OnItemSelectedListener<T>{

        void onItemSelected(T t);
    }

    public interface AdapterImpl<T>{

        void onBindItem(TextView textView, T t);
    }
}