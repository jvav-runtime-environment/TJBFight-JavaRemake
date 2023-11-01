package com.mygdx.game;

import com.badlogic.gdx.math.MathUtils;

public class Player extends Figure {
    Player() {
        init();
    }

    @Override
    void init() {
        health = 50;
        maxhealth = 100;
        defaultTime = 3;
        time = 3;

        image = Textures.Error;
        setSize(Consts.FigureWidth, Consts.FigureHeight);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        time = MathUtils.clamp(time, 0, defaultTime);
    }

    @Override
    public boolean allFinished() {
        return !hasTime() && super.allFinished();
    }
}
