package com.deitel.cannongame;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Particle {
    private float x;
    private float y;
    private float velocityX;
    private float velocityY;
    private float radius;
    private int alpha = 255;
    private final Paint paint = new Paint();

    public Particle(float x, float y, int color, float velocityX, float velocityY, float radius) {
        this.x = x;
        this.y = y;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.radius = radius;
        this.paint.setColor(color);
        this.paint.setAntiAlias(true);
    }

    public void update(double interval) {
        x += velocityX * interval;
        y += velocityY * interval;
        alpha -= (int) (350 * interval); //Fade out
        if (alpha < 0) {
            alpha = 0;
        }
        paint.setAlpha(alpha);
    }

    public void draw(Canvas canvas) {
        if (alpha > 0) {
            canvas.drawCircle(x, y, radius, paint);
        }
    }

    public boolean isDead() {
        return alpha <= 0;
    }
}