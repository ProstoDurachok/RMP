package com.example.rmp;

import android.text.TextPaint;
import android.text.style.StrikethroughSpan;

public class BlackStrikethroughSpan extends StrikethroughSpan {
    private int color;
    public BlackStrikethroughSpan() {
        super();
        this.color = 0xFF000000;
    }
    public BlackStrikethroughSpan(int color) {
        super();
        this.color = color;
    }
    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setStrikeThruText(true);
        ds.setColor(color);
    }


}
