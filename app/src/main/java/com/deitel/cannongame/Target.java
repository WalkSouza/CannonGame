
package com.deitel.cannongame;

public class Target extends GameElement {

    private final int hitReward;

    public Target(
            CannonView view,
            int color,
            int hitReward,
            int x,
            int y,
            int width,
            int length,
            float velocityY) {

        super(
                view,
                color,
                0,
                x,
                y,
                width,
                length,
                velocityY
        );

        this.hitReward = hitReward;
    }

    public int getHitReward() {
        return hitReward;
    }
    public void accelerate(float factor) {
        this.velocityY *= factor;
    }
}
