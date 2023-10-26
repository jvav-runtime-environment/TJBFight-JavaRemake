package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.Texture;

public class Player extends Figure {
    int maxtime = 3;

    Player() {
        health = 50;
        maxhealth = 100;
        time = 3;

        image = new Texture(Gdx.files.internal("badlogic.jpg"));
        setSize(50, 100);

        setPosition(0, 1000);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        time = MathUtils.clamp(time, 0, maxtime);
    }

    @Override
    public boolean allFinished() {
        return !hasTime() && super.allFinished();
    }

    public boolean consumetime(int ammont) {
        if (ammont > time) {
            return false;
        } else {
            time -= ammont;
            return true;
        }
    }

    public void recoverTime() {
        time = maxtime;
    }

    public boolean hasTime() {
        return time > 0;
    }
}
