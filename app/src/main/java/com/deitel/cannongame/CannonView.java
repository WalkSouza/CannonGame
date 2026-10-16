
package com.deitel.cannongame;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;

public class CannonView extends SurfaceView
        implements SurfaceHolder.Callback {

    private int screenWidth;
    private int screenHeight;

    private final Paint textPaint = new Paint();
    private final Paint resultPaint = new Paint();

    private Cannon cannon;

    private final ArrayList<Cannonball> cannonballs =
            new ArrayList<>();

    private final ArrayList<Target> targets =
            new ArrayList<>();

    private final ArrayList<Particle> particles =
            new ArrayList<>();

    private Blocker blocker;

    private volatile boolean gameRunning;
    private Thread gameThread;

    private static final int TARGET_COUNT = 10;
    private static final int HIT_REWARD = 3;
    private static final int MISS_PENALTY = 2;

    private volatile double timeRemaining = 60.0;
    private volatile int shotsFired = 0;
    private volatile boolean gameOver = false;

    //Recursos extras
    private volatile int score = 0;
    private volatile int multiplier = 1;
    private static final int SCORE_INCREMENT = 100;
    private volatile int level = 1;



    private boolean resultDialogShown = false;

    private final SoundPool soundPool;
    private final int cannonFireSound;
    private final int targetHitSound;
    private final int blockerHitSound;

    public CannonView(Context context, AttributeSet attrs) {
        super(context, attrs);

        getHolder().addCallback(this);

        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(36);
        textPaint.setAntiAlias(true);

        resultPaint.setColor(Color.BLACK);
        resultPaint.setTextSize(52);
        resultPaint.setTextAlign(Paint.Align.CENTER);
        resultPaint.setAntiAlias(true);

        AudioAttributes audioAttributes =
                new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(
                                AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(3)
                .setAudioAttributes(audioAttributes)
                .build();

        cannonFireSound =
                soundPool.load(context, R.raw.cannon_fire, 1);

        targetHitSound =
                soundPool.load(context, R.raw.target_hit, 1);

        blockerHitSound =
                soundPool.load(context, R.raw.blocker_hit, 1);

        setFocusable(true);
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void playSound(int soundId) {
        if (soundId != 0) {
            soundPool.play(soundId, 1, 1, 1, 0, 1);
        }
    }

    private void newGame() {
        if (screenWidth <= 0 || screenHeight <= 0) {
            return;
        }

        synchronized (this) {
            resultDialogShown = false;
            gameOver = false;
            timeRemaining = 60.0;
            shotsFired = 0;
            score = 0;
            multiplier = 1;
            level = 1;
        }

        cannon = new Cannon(
                this,
                Math.max(20, screenHeight / 18),
                Math.max(50, screenHeight / 5),
                Math.max(8, screenHeight / 35)
        );

        synchronized (cannonballs) {
            cannonballs.clear();
        }

        synchronized (particles) {
            particles.clear();
        }

        synchronized (targets) {
            targets.clear();

            Random random = new Random();

            int targetWidth = Math.max(15, screenWidth / 40);
            int targetHeight = Math.max(35, screenHeight / 10);
            int spacing = screenWidth / (TARGET_COUNT + 2);

            for (int i = 0; i < TARGET_COUNT; i++) {
                int x = screenWidth - spacing * (i + 1);

                int y = random.nextInt(
                        Math.max(1, screenHeight - targetHeight)
                );

                float speed = 80 + random.nextInt(121);

                if (random.nextBoolean()) {
                    speed = -speed;
                }

                int color = (i % 2 == 0)
                        ? Color.rgb(0, 70, 180)
                        : Color.rgb(255, 200, 0);

                targets.add(new Target(
                        this,
                        color,
                        HIT_REWARD,
                        x,
                        y,
                        targetWidth,
                        targetHeight,
                        speed
                ));
            }
        }

        int blockerWidth = Math.max(15, screenWidth / 40);
        int blockerHeight = Math.max(45, screenHeight / 8);

        blocker = new Blocker(
                this,
                Color.BLACK,
                MISS_PENALTY,
                screenWidth / 2,
                screenHeight / 2,
                blockerWidth,
                blockerHeight,
                100.0f
        );

        postInvalidate();
    }

    private void fireCannonball() {
        if (cannon == null || gameOver) {
            return;
        }

        double angle = cannon.getBarrelAngle();

        int ballSize = Math.max(12, screenHeight / 40);

        int startX = cannon.getBarrelEnd().x - ballSize / 2;
        int startY = cannon.getBarrelEnd().y - ballSize / 2;

        float velocityX =
                Cannonball.INITIAL_SPEED * (float) Math.cos(angle);

        float velocityY =
                -Cannonball.INITIAL_SPEED * (float) Math.sin(angle);

        Cannonball ball = new Cannonball(
                this,
                Color.BLACK,
                cannonFireSound,
                startX,
                startY,
                ballSize,
                ballSize,
                velocityX,
                velocityY
        );

        synchronized (cannonballs) {
            cannonballs.add(ball);
        }

        ball.playSound();
        shotsFired++;
    }

    private void updatePositions(double interval) {
        if (gameOver) {
            return;
        }

        timeRemaining -= interval;

        synchronized (cannonballs) {
            Iterator<Cannonball> ballIterator =
                    cannonballs.iterator();

            while (ballIterator.hasNext()) {
                Cannonball ball = ballIterator.next();

                ball.update(interval);

                boolean hitTarget = false;

                synchronized (targets) {
                    Iterator<Target> targetIterator =
                            targets.iterator();

                    while (targetIterator.hasNext()) {
                        Target target = targetIterator.next();

                        if (ball.collidesWith(target)) {
                            timeRemaining += target.getHitReward();
                            score += SCORE_INCREMENT * multiplier;
                            multiplier++;
                            level++;

                            //Explosão de partículas
                            synchronized (particles) {
                                Random rand = new Random();
                                for (int p = 0; p < 15; p++) {
                                    float speed = 100 + rand.nextInt(250);
                                    double angle = rand.nextDouble() * 2 * Math.PI;
                                    float pVx = (float) (speed * Math.cos(angle));
                                    float pVy = (float) (speed * Math.sin(angle));
                                    particles.add(new Particle(
                                            target.shape.centerX(),
                                            target.shape.centerY(),
                                            target.paint.getColor(),
                                            pVx,
                                            pVy,
                                            Math.max(5, target.shape.width() / 4f)
                                    ));
                                }
                            }

                            for (Target t : targets) {
                                t.accelerate(1.15f);
                            }

                            playSound(targetHitSound);

                            targetIterator.remove();
                            ballIterator.remove();

                            hitTarget = true;
                            break;
                        }
                    }
                }

                if (hitTarget) {
                    continue;
                }

                if (blocker != null
                        && ball.collidesWith(blocker)) {

                    ball.reverseVelocityX();
                    playSound(blockerHitSound);

                    timeRemaining -= blocker.getMissPenalty();
                    multiplier = 1;
                }

                if (ball.isOffScreen()) {
                    ballIterator.remove();
                    multiplier = 1;
                }
            }
        }

        if (blocker != null) {
            blocker.update(interval);
        }

        boolean allTargetsDestroyed;

        synchronized (targets) {
            for (Target target : targets) {
                target.update(interval);
            }

            allTargetsDestroyed = targets.isEmpty();
        }

        synchronized (particles) {
            Iterator<Particle> particleIterator = particles.iterator();
            while (particleIterator.hasNext()) {
                Particle p = particleIterator.next();
                p.update(interval);
                if (p.isDead()) {
                    particleIterator.remove();
                }
            }
        }

        if (allTargetsDestroyed) {
            finishGame(true);
        } else if (timeRemaining <= 0) {
            timeRemaining = 0;
            finishGame(false);
        }
    }

    private void finishGame(boolean won) {
        synchronized (this) {
            if (gameOver) {
                return;
            }

            gameOver = true;

            if (resultDialogShown) {
                return;
            }

            resultDialogShown = true;
        }

        post(() -> showResultDialog(won));
    }

    private void showResultDialog(boolean won) {
        String title = won ? "Parabéns!" : "Fim de jogo!";

        String message = won
                ? "Você destruiu todos os alvos!"
                : "O tempo acabou! Tente novamente.";

        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(
                        message
                                + "\n\nPontuação final: " + score
                                + "\n\nDisparos realizados: " + shotsFired
                )
                .setPositiveButton(
                        "Jogar novamente",
                        (dialog, which) -> {
                            newGame();
                            drawGame(getHolder());
                        }
                )
                .setCancelable(false)
                .show();
    }

    @Override
    protected void onSizeChanged(
            int width,
            int height,
            int oldWidth,
            int oldHeight) {

        super.onSizeChanged(width, height, oldWidth, oldHeight);

        screenWidth = width;
        screenHeight = height;

        if (width > 0 && height > 0) {
            newGame();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (screenWidth > 0 && screenHeight > 0) {
            startGameThread();
        } else {
            drawGame(holder);
        }
    }

    @Override
    public void surfaceChanged(
            SurfaceHolder holder,
            int format,
            int width,
            int height) {

        screenWidth = width;
        screenHeight = height;

        if (width > 0 && height > 0 && cannon == null) {
            newGame();
        }

        drawGame(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopGameThread();
    }

    private void startGameThread() {
        if (gameRunning) {
            return;
        }

        gameRunning = true;

        gameThread = new Thread(() -> {
            long previousTime = System.nanoTime();

            while (gameRunning) {
                long currentTime = System.nanoTime();

                double interval =
                        (currentTime - previousTime)
                                / 1_000_000_000.0;

                previousTime = currentTime;

                updatePositions(interval);
                drawGame(getHolder());

                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        gameThread.start();
    }

    private void stopGameThread() {
        gameRunning = false;

        if (gameThread != null) {
            gameThread.interrupt();

            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            gameThread = null;
        }
    }

    private void drawGame(SurfaceHolder holder) {
        Canvas canvas = null;

        try {
            canvas = holder.lockCanvas();

            if (canvas != null) {
                canvas.drawColor(Color.WHITE);

                if (cannon != null) {
                    cannon.draw(canvas);
                }

                synchronized (cannonballs) {
                    for (Cannonball ball : cannonballs) {
                        ball.draw(canvas);
                    }
                }

                if (blocker != null) {
                    blocker.draw(canvas);
                }

                synchronized (targets) {
                    for (Target target : targets) {
                        target.draw(canvas);
                    }
                }

                synchronized (particles) {
                    for (Particle p : particles) {
                        p.draw(canvas);
                    }
                }

                canvas.drawText(
                        String.format(
                                Locale.getDefault(),
                                "Tempo: %.1f s",
                                Math.max(0, timeRemaining)
                        ),
                        20,
                        45,
                        textPaint
                );

                canvas.drawText(
                        String.format(
                                Locale.getDefault(),
                                "Tempo: %.1f s",
                                Math.max(0, timeRemaining)
                        ),
                        20,
                        45,
                        textPaint
                );

                canvas.drawText(
                        "Disparos: " + shotsFired,
                        20,
                        85,
                        textPaint
                );

                canvas.drawText(
                        "Pontos: " + score + " (x" + multiplier + ") | Nível: " + level,
                        20,
                        125,
                        textPaint
                );

                if (gameOver) {
                    String message;

                    synchronized (targets) {
                        message = targets.isEmpty()
                                ? "Você venceu!"
                                : "Fim de jogo!";
                    }

                    canvas.drawText(
                            message,
                            canvas.getWidth() / 2f,
                            canvas.getHeight() / 2f,
                            resultPaint
                    );
                }
            }
        } finally {
            if (canvas != null) {
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        if (gameOver) {
            return true;
        }

        if (action == MotionEvent.ACTION_DOWN
                || action == MotionEvent.ACTION_MOVE) {

            if (cannon != null) {
                double angle = Math.atan2(
                        screenHeight / 2.0 - event.getY(),
                        event.getX()
                );

                angle = Math.max(
                        -Math.PI / 4,
                        Math.min(Math.PI / 2, angle)
                );

                cannon.align(angle);
                drawGame(getHolder());
            }

            return true;
        }

        if (action == MotionEvent.ACTION_UP
                || action == MotionEvent.ACTION_CANCEL) {

            fireCannonball();
            drawGame(getHolder());

            return true;
        }

        return true;
    }
}
