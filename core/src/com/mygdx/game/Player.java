package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.Texture;

public class Player extends Figure {
    int energy = 3;
    int maxenergy = 3;

    Player() {
        health = 50;
        maxhealth = 100;

        image = new Texture(Gdx.files.internal("badlogic.jpg"));
        setSize(50, 100);

        setPosition(0, 1000);
        setRelativePosition(4, 4);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        energy = MathUtils.clamp(energy, 0, maxenergy);
    }

    @Override
    public boolean allFinished() {
        return !hasEnergy() && super.allFinished();
    }

    public boolean consumeEnergy(int ammont) {
        if (ammont > energy) {
            return false;
        } else {
            energy -= ammont;
            return true;
        }
    }

    public void recoverEnergy() {
        energy = maxenergy;
    }

    public boolean hasEnergy() {
        return energy > 0;
    }
}
