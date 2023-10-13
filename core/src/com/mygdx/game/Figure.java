package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;

public class Figure extends Actor {
    Texture image;
    int health;
    int maxhealth;
    int armor;
    // boolean dying = false;
    Vector2 RelativePosition;// not the real Positon,needs to be changed

    ParticleEffect hitEffect = new ParticleEffect();

    // other things...

    Figure() {
        RelativePosition = new Vector2();
        health = 1;
        maxhealth = 1;

        hitEffect.load(Gdx.files.internal(".\\particles\\spark\\spark.p"),
                Gdx.files.internal(".\\particles\\spark"));

        // add texture loading
        // ... all the initial work

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
            AlphaAction fadeout = new AlphaAction();
            ParallelAction action = new ParallelAction();

            fadeout.setAlpha(0);
            fadeout.setDuration(1);

            action.addAction(fadeout);
            action.addAction(Consts.getShakingAction(8, 15));

            addAction(action);
        }

        Consts.damageRender.add(getX(), getY(), ammont);
        addAction(Consts.getShakingAction(2, 5));

        hitEffect.setPosition(getCenterX(), getCenterY());
        hitEffect.start();
    }

    void setRelativePosition(float x, float y) {
        RelativePosition.set((int) x, (int) y);

        // 移动动画
        Vector2 vec = Consts.getAbsPosition(RelativePosition.x, RelativePosition.y);
        MoveToAction action = new MoveToAction();

        action.setPosition(vec.x - getWidth() / 2, vec.y);
        action.setInterpolation(Interpolation.circleOut);
        action.setDuration(0.3f);

        addAction(action);
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
        return getActions().size == 0 && hitEffect.isComplete();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = batch.getColor();
        color.a = getColor().a;

        batch.setColor(color);

        batch.draw(image, getX(), getY(), getWidth(), getHeight());
        hitEffect.draw(batch, Gdx.graphics.getDeltaTime());

        batch.setColor(color.r, color.g, color.b, 1);
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
        return Consts.getAbsPosition(RelativePosition.x, RelativePosition.y);
    }
}
