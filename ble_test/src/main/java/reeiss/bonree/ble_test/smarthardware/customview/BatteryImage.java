package reeiss.bonree.ble_test.smarthardware.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class BatteryImage
    extends View {
    private Paint a;
    private Paint b;
    private float c = 1.0F;
    private int d;
    private int e;
    private float f = 30.0F;
    private float g = 14.0F;
    private float h = 1.5F;
    private float i = 8.0F;
    private float j = 1.0F;
    private float k = this.f - this.c - this.j * 2.0F;
    private float l = 0.0F;
    private RectF m;
    private RectF n;
    private RectF o;

    public BatteryImage(Context paramContext) {
        super(paramContext);
        a();
    }

    public void a() {
        this.a = new Paint();
        this.a.setColor(-16777216);
        this.a.setAntiAlias(true);
        this.a.setStyle(Paint.Style.STROKE);
        this.a.setStrokeWidth(this.c);
        this.b = new Paint();
        this.b.setColor(-16711936);
        this.b.setAntiAlias(true);
        this.b.setStyle(Paint.Style.FILL);
        this.b.setStrokeWidth(this.c);
        this.n = new RectF((this.g - this.i) / 2.0F, 0.0F, (this.g - this.i) / 2.0F + this.i, this.h);
        this.m = new RectF(-1.0F, this.h, this.g, this.f);
        this.o = new RectF(this.j + this.c / 2.0F + 1.0F, this.h + this.c / 2.0F + this.j + this.k * ((100.0F - this.l) / 100.0F), this.g - this.j * 2.0F - 0.5F, this.c / 2.0F + this.j + this.k - 0.5F);
    }

    public BatteryImage(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        a();
    }

    public BatteryImage(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        a();
    }

    protected void onDraw(Canvas paramCanvas) {
        super.onDraw(paramCanvas);
        paramCanvas.save();
        paramCanvas.translate(2.0F, 2.0F);
        paramCanvas.drawRoundRect(this.n, 2.0F, 2.0F, this.a);
        paramCanvas.drawRoundRect(this.m, 2.0F, 2.0F, this.a);
        paramCanvas.drawRoundRect(this.o, 2.0F, 2.0F, this.b);
        paramCanvas.restore();
    }

    protected void onMeasure(int paramInt1, int paramInt2) {
        this.e = View.MeasureSpec.getSize(paramInt1);
        this.d = View.MeasureSpec.getSize(paramInt2);
        setMeasuredDimension(this.e, this.d);
    }

    public void setPower(float paramFloat) {
        this.l = paramFloat;
        if (this.l < 0.0F) {
            this.l = 0.0F;
        }
        this.o = new RectF(this.j + this.c / 2.0F - 0.5F, this.h + this.c / 2.0F + this.j + this.k * ((100.0F - this.l) / 100.0F), this.g - this.j * 2.0F, this.c / 2.0F + this.j + this.k);
        invalidate();
    }
}
