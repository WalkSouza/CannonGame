
package com.deitel.cannongame;

import android.graphics.Rect;

public class Cannonball extends GameElement {

    public static final float INITIAL_SPEED = 1000.0f;

    private float velocityX;

    public Cannonball(
            CannonView view,
            int color,
            int soundId,
            int x,
            int y,
            int width,
            int length,
            float velocityX,
            float velocityY) {

        super(
                view,
                color,
                soundId,
                x,
                y,
                width,
                length,
                velocityY
        );

        this.velocityX = velocityX;
    }

    @Override
    public void update(double interval) {
        shape.offset(
                (int) (velocityX * interval),
                (int) (velocityY * interval)
        );
    }

    public boolean collidesWith(GameElement element) {
        return Rect.intersects(shape, element.shape);
    }

    public boolean isOffScreen() {
        return shape.left > view.getScreenWidth()
                || shape.right < 0
                || shape.bottom < 0
                || shape.top > view.getScreenHeight();
    }

    public void reverseVelocityX() {
        velocityX *= -1;
    }
}
