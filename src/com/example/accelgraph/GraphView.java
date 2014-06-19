package com.example.accelgraph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class GraphView extends View {

    private final static String TAG = "GraphView";

    public GraphView(Context context) {
        super(context);
    }

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int width, height, base, dw, dh;
    private int ndata = 50;
    private int[] vs = new int[ndata];
    private int idx = 0;
    private final static float Ymax = 15;
    
    private void setup(int width, int height, int ndata) {
        this.width = width;
        this.height = height;
        base = height / 2;
        dw = width / ndata;
        dh = (int) (base / Ymax);
        if (dh < 1)
            dh = 1;
        this.ndata = ndata;
        if (vs == null || this.ndata > vs.length)
            vs = new int[ndata];
        idx = 0;
    }
    
    public void setVal(float val) {
        vs[idx] = (int) (base + base * val / Ymax);
        idx = (idx + 1) % ndata;
    }

    private Paint paint = new Paint();

    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        Log.i(TAG, "onSizeChanged: Ndata=" + ndata);
        setup(w, h, w / 8);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setColor(Color.argb(75, 255, 255, 255));
        paint.setStrokeWidth(1);
        for (int y = base; y < height; y += dh * 10)
            canvas.drawLine(0, y, width, y, paint);
        for (int y = base; y > 0; y -= dh * 10)
            canvas.drawLine(0, y, width, y, paint);
        for (int x = 0; x < width; x += dw * 10)
            canvas.drawLine(x, 0, x, height, paint);

        paint.setColor(Color.RED);
        canvas.drawLine(0, base, width, base, paint);

        paint.setColor(Color.YELLOW);
        paint.setStrokeWidth(2);
        for (int i = 0; i < ndata - 1; i++) {
            int j = (idx + i) % ndata;
            canvas.drawLine(i * dw, vs[j], (i + 1) * dw, vs[(j + 1) % ndata], paint);
        }
    }

}
