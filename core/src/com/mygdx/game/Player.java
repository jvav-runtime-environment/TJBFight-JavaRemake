package com.mygdx.game;

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

        aniMgr.setAnimation(AnimationManager.State.rest, Animations.playerRest);
        aniMgr.setAnimation(AnimationManager.State.attack, Animations.playerAttack);

        setSize(Consts.FigureWidth, Consts.FigureHeight);
    }

    @Override
    public boolean allFinished() {
        return !hasTime() && super.allFinished();
    }
}
