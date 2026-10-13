
package com.deitel.cannongame;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class GameElement {

    protected CannonView view;
    protected Paint paint = new Paint();
    protected Rect shape;
    protected float velocityY;

    private int soundId;

    public GameElement(
            CannonView view,
            int color,
            int soundId,
            int x,
            int y,
            int width,
            int length,
            float velocityY) {

        this.view = view;
        paint.setColor(color);

        shape = new Rect(
                x,
                y,
                x + width,
                y + length
        );

        this.soundId = soundId;
        this.velocityY = velocityY;
    }

    public void update(double interval) {
        if (view.getScreenHeight() <= 0) {
            return;
        }

        int height = shape.height();

        // Se o elemento for maior que a tela, mantém-no no início.
        if (height >= view.getScreenHeight()) {
            shape.offsetTo(shape.left, 0);
            velocityY = 0;
            return;
        }

        // Atualiza a posição vertical.
        int displacement = (int) (velocityY * interval);
        shape.offset(0, displacement);

        // Corrige a posição e inverte a velocidade ao atingir as bordas.
        if (shape.top < 0) {
            shape.offset(0, -shape.top);
            velocityY = Math.abs(velocityY);
        } else if (shape.bottom > view.getScreenHeight()) {
            shape.offset(
                    0,
                    view.getScreenHeight() - shape.bottom
            );
            velocityY = -Math.abs(velocityY);
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawRect(shape, paint);
    }

    public void playSound() {
        view.playSound(soundId);
    }
}
