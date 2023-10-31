package com.mygdx.game;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

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

        image = Textures.Error;
        setSize(Consts.FigureWidth, Consts.FigureHeight);
    }

    @Override
    public void kill() {
        super.kill();
        Consts.mainstage.enemies.removeValue(this, false);
    }

    @Override
    public void MoveToRelativePosition(float x, float y) {
        RelativePosition.set((int) x, (int) y);

        // 移动动画
        Vector2 vec = Map.getAbsPosition(RelativePosition.x, RelativePosition.y);
        MoveToAction action = new MoveToAction();

        action.setPosition(vec.x - getWidth() / 2, vec.y);
        action.setInterpolation(Interpolation.circleOut);
        action.setDuration(0.3f);

        addAction(new SequenceAction(new DelayAction(1), action));
    }

    public void drawArrowtoAim() {
        if (pos != null) {
            Vector2 vec = Map.getAbsPosition(pos[0], pos[1]);
            drawArrowtoAim(vec.x, vec.y);
        }
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

    private void getNextPoint() {
        Array<int[]> poses = Consts.mainstage.map.getFreePointAround(RelativePosition.x, RelativePosition.y, 2);

        if (poses.size != 0) {
            pos = poses.random();
        } else {
            pos = null;
        }

    }

    @Override
    public void AI() {
        if (allFinished()) {
            if (time > 0) {
                getNextPoint();
                if (pos != null) {
                    MoveToRelativePosition(pos[0], pos[1]);
                }
                consumeTime(1);
            } else {
                AIFinished = true;
                pos = null;
            }
        }
    }
}