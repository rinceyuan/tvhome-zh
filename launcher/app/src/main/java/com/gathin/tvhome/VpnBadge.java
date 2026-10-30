package com.gathin.tvhome;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.style.ReplacementSpan;

import androidx.annotation.NonNull;

/** Network-pill badge adapted from Gathin23/cleanyourtv under its MIT license. */
final class VpnBadge extends ReplacementSpan {
    private static final int GREEN = 0xFF30D158;
    private static final int INK = 0xFF06290F;

    private final Drawable lock;
    private final float density;
    private final RectF rect = new RectF();

    VpnBadge(Drawable lock, float density) {
        this.lock = lock.mutate();
        this.lock.setTint(INK);
        this.density = density;
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end,
                       Paint.FontMetricsInt metrics) {
        float size = paint.getTextSize();
        paint.setTextSize(size * 0.78f);
        float width = paint.measureText(text, start, end);
        paint.setTextSize(size);
        return (int) (28 * density + size * 0.8f + width);
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end,
                     float x, int top, int y, int bottom, @NonNull Paint paint) {
        float size = paint.getTextSize();
        int color = paint.getColor();
        float lockSize = size * 0.8f;
        float height = size * 1.3f;
        float center = (top + bottom) / 2f;
        float left = x + 8 * density;
        rect.set(left, center - height / 2f,
                x + getSize(paint, text, start, end, null), center + height / 2f);
        paint.setColor(GREEN);
        canvas.drawRoundRect(rect, height / 2f, height / 2f, paint);

        int lockLeft = (int) (left + 7 * density);
        lock.setBounds(lockLeft, (int) (center - lockSize / 2f),
                (int) (lockLeft + lockSize), (int) (center + lockSize / 2f));
        lock.draw(canvas);

        paint.setColor(INK);
        paint.setTextSize(size * 0.78f);
        Paint.FontMetrics metrics = paint.getFontMetrics();
        canvas.drawText(text, start, end, lockLeft + lockSize + 4 * density,
                center - (metrics.ascent + metrics.descent) / 2f, paint);
        paint.setTextSize(size);
        paint.setColor(color);
    }
}
