package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;

public class Figure extends Actor {
    Texture image;
    int health;
    int time;
    int defaultTime;
    int maxhealth;
    int armor;

    Vector2 RelativePosition = new Vector2();;// not the real Positon,needs to be changed

    ParticleEffect hitEffect = Effects.getEffect(Effects.types.hit);
    ParticleEffect deathEffect = Effects.getEffect(Effects.types.death);

    // other things...

    Figure() {
        init();
    }

    Figure(float x, float y) {
        init();
        setRelativePosition(x, y);
    }

    void init() {
    }

    void getDamage(Damage damage) {
        switch (damage.DamageType) {
            case Consts.PHYSICAL_DAMAGE_ID:
                damage(damage.ammont - armor);
                break;
        }
    }

    private void damage(int ammont) {
        health -= ammont;

        if (health <= 0) {
            // 死亡动画
            AlphaAction fadeout = new AlphaAction();
            ParallelAction action = new ParallelAction();

            fadeout.setAlpha(0);
            fadeout.setDuration(0.64f);

            action.addAction(fadeout);
            action.addAction(Animations.getShakingAction(8, 15));

            addAction(action);

            // 死亡特效
            deathEffect.setPosition(getCenterX(), getCenterY());
            deathEffect.start();
        }

        Consts.damageRender.add(getX(), getY(), ammont);
        addAction(Animations.getShakingAction(2, 5));

        hitEffect.setPosition(getCenterX(), getCenterY());
        hitEffect.start();
    }

    public void MoveToRelativePosition(float x, float y) {
        RelativePosition.set((int) x, (int) y);

        // 移动动画
        Vector2 vec = Map.getAbsPosition(RelativePosition.x, RelativePosition.y);
        MoveToAction action = new MoveToAction();

        action.setPosition(vec.x - getWidth() / 2, vec.y);
        action.setInterpolation(Interpolation.circleOut);
        action.setDuration(0.3f);

        addAction(action);
    }

    public void setRelativePosition(float x, float y) {
        RelativePosition.set((int) x, (int) y);

        Vector2 vec = Map.getAbsPosition(RelativePosition.x, RelativePosition.y);
        setPosition(vec.x - getWidth() / 2, vec.y);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        health = MathUtils.clamp(health, 0, maxhealth);

        if (health <= 0) {
            if (allFinished()) {
                kill();
            }
        }
    }

    void kill() {
        remove();
    }

    public boolean allFinished() {
        return getActions().size == 0 && hitEffect.isComplete() && deathEffect.isComplete();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = batch.getColor();
        color.a = getColor().a;

        batch.setColor(color);
        batch.draw(image, getX(), getY(), getWidth(), getHeight());
        batch.setColor(color.r, color.g, color.b, 1);

        hitEffect.draw(batch, Gdx.graphics.getDeltaTime());
        deathEffect.draw(batch, Gdx.graphics.getDeltaTime());
    }

    public void drawArrowtoAim(float endx, float endy) {
        ShapeRenderer sr = Consts.sr;
        float startx, starty;

        startx = getCenterX();
        starty = getY();

        // 三角形点计算
        float k, a, p1X, p1Y, p2X, p2Y, lEndx, lEndy;
        float m = Consts.ArrowLength, n = Consts.ArrowWidth;

        k = (starty - endy) / (startx - endx);// 斜率
        a = MathUtils.atan(k);// 角度

        // p1X:n*cos(a+((π)/(2)))+m*cos(a)*((x(B))/(abs(x(B))))
        // p1y:n*sin(a+((π)/(2)))+m*sin(a)*((x(B))/(abs(x(B))))
        p1X = n * MathUtils.cos(a + MathUtils.HALF_PI)
                + m * MathUtils.cos(a) * Math.signum(startx - endx) + endx;
        p1Y = n * MathUtils.sin(a + MathUtils.HALF_PI)
                + m * MathUtils.sin(a) * Math.signum(startx - endx) + endy;

        p2X = -n * MathUtils.cos(a + MathUtils.HALF_PI)
                + m * MathUtils.cos(a) * Math.signum(startx - endx) + endx;
        p2Y = -n * MathUtils.sin(a + MathUtils.HALF_PI)
                + m * MathUtils.sin(a) * Math.signum(startx - endx) + endy;

        lEndx = m * MathUtils.cos(a) * Math.signum(startx - endx) + endx;
        lEndy = m * MathUtils.sin(a) * Math.signum(startx - endx) + endy;

        // 绘制准备
        sr.setColor(1, 0.4f, 0, 1);
        sr.setProjectionMatrix(Consts.mainstage.getCamera().combined);
        sr.begin(ShapeType.Filled);

        sr.rectLine(startx, starty, lEndx, lEndy, 10);
        sr.triangle(endx, endy, p1X, p1Y, p2X, p2Y);

        sr.end();
    }

    public boolean consumeTime(int ammont) {
        if (ammont > time) {
            return false;
        } else {
            time -= ammont;
            return true;
        }
    }

    public void recoverTime() {
        time = defaultTime;
    }

    public boolean hasTime() {
        return time > 0;
    }

    public float getCenterX() {
        return getX() + getWidth() / 2;
    }

    public float getCenterY() {
        return getY() + getHeight() / 2;
    }

    public float getHealthPercent() {
        return (float) health / (float) maxhealth;
    }

    public Vector2 getAbsPosition() {
        return Map.getAbsPosition(RelativePosition.x, RelativePosition.y);
    }
}
