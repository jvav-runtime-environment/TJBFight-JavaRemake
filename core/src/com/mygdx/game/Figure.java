package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;

public class Figure extends Actor {
    Texture image;
    int health;
    int maxhealth;
    int armor;
    Vector2 RelativePosition;// not the real Positon,needs to be changed

    // other things...

    Figure() {
        RelativePosition = new Vector2();
        health = 1;
        maxhealth = 1;
        // add texture loading
        // ... all the initial work

    }

    void getDamage(Damage damage) {
        switch (damage.DamageType) {
            case Consts.PHYSICAL_DAMAGE_ID:
                health -= damage.ammont - armor;
                break;
        }
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

        if (health<=0){
            kill();
        }
        
        health = MathUtils.clamp(health, 0, maxhealth);
        
    }

    void kill(){
        remove();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(image, getX(), getY(), getWidth(), getHeight());
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
}
