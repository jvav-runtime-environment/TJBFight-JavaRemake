package com.mygdx.game;

import com.badlogic.gdx.utils.Array;

class Enemy extends Figure {
    boolean AIFinished = false;
    int[] pos;

    Enemy() {
        super();
    }

    Enemy(float x, float y) {
        super(x, y);
    }

    @Override
    void init() {
        health = 100;
        maxhealth = 100;
        defaultTime = 3;
        time = 3;
        team = Consts.team.enemy;

        setSize(Consts.FigureWidth, Consts.FigureHeight);
    }

    @Override
    public void kill() {
        super.kill();
        Consts.mainstage.enemies.removeValue(this, false);
    }

    public void AI() {
        if (allFinished()) {
            AIFinished = true;
        }
    }
}

class DebugEnemy extends Enemy {

    DebugEnemy(float x, float y) {
        super(x, y);
    }

    DebugEnemy() {
        super();
    }

    @Override
    void init() {
        super.init();
        aniMgr.setAnimation(AnimationManager.State.rest, Animations.enemyCHRest);
    }

    private void getNextPoint() {
        Array<int[]> poses = Consts.mainstage.map.getFreePointAround(relativePosition.x, relativePosition.y, 2);

        pos = poses.random();
        consumeTime(1);
    }

    @Override
    public void AI() {
        if (allFinished()) {
            if (hasTime()) {
                getNextPoint();
                MoveToRelativePosition(pos[0], pos[1]);
            } else {
                AIFinished = true;
                pos = null;
            }
        }
    }
}