
package com.deitel.cannongame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

public class Cannon {

    private final int baseRadius;
    private final int barrelLength;
    private final int barrelWidth;

    private final Point barrelEnd = new Point();
    private final Paint paint = new Paint();
    private final CannonView view;

    private double barrelAngle = Math.PI / 4;

    public Cannon(
            CannonView view,
            int baseRadius,
            int barrelLength,
            int barrelWidth) {

        this.view = view;
        this.baseRadius = baseRadius;
        this.barrelLength = barrelLength;
        this.barrelWidth = barrelWidth;

        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(barrelWidth);
        paint.setStrokeCap(Paint.Cap.ROUND);

        align(barrelAngle);
    }

    public void align(double angle) {
        barrelAngle = angle;

        int centerX = baseRadius;
        int centerY = view.getScreenHeight() / 2;

        barrelEnd.x = centerX
                + (int) (barrelLength * Math.cos(barrelAngle));

        barrelEnd.y = centerY
                - (int) (barrelLength * Math.sin(barrelAngle));
    }


    public void draw(Canvas canvas) {
        int centerX = baseRadius;
        int centerY = canvas.getHeight() / 2;

        barrelEnd.x = centerX
                + (int) (barrelLength * Math.cos(barrelAngle));

        barrelEnd.y = centerY
                - (int) (barrelLength * Math.sin(barrelAngle));

        canvas.drawLine(
                centerX, centerY,
                barrelEnd.x, barrelEnd.y,
                paint
        );

        canvas.drawCircle(
                centerX, centerY,
                baseRadius, paint
        );
    }

    public double getBarrelAngle() {
        return barrelAngle;
    }

    public Point getBarrelEnd() {
        return barrelEnd;
    }
}
