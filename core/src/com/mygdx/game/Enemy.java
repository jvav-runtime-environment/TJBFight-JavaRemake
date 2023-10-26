package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

class Enemy extends Figure {
    boolean AIFinished = false;

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

        image = new Texture(Gdx.files.internal("badlogic.jpg"));
        setSize(50, 100);
    }

    public Boolean testPoint(float aimx, float aimy) {
        Array<Figure> figures = new Array<Figure>();

        figures = Consts.mainstage.selectFigure(new FigureSelector(aimx, aimy) {
            public boolean select(Figure figure) {
                return figure.RelativePosition.x == x && figure.RelativePosition.y == y;
            }
        });

        return figures.size == 0;
    }

    @Override
    void kill() {
        remove();
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

    public void AI() {
        if (allFinished()) {
            AIFinished = true;
        }
    }
}

class DebugEnemy extends Enemy {
    int[] pos;

    DebugEnemy(float x, float y) {
        super(x, y);
    }

    DebugEnemy() {
        super();
    }

    private void getNextPoint() {
        int breakcounter = 0;
        pos = Consts.mainstage.map.getInRange(RelativePosition.x, RelativePosition.y, 2).random();

        while (!testPoint(pos[0], pos[1])) {
            pos = Consts.mainstage.map.getInRange(RelativePosition.x, RelativePosition.y, 2).random();

            breakcounter++;
            if (breakcounter >= 100) {
                break;
            }
        }
    }

    @Override
    public void AI() {
        if (allFinished()) {
            switch (time) {
                case 3:
                    getNextPoint();
                    MoveToRelativePosition(pos[0], pos[1]);
                    time--;
                    break;
                case 2:
                    getNextPoint();
                    MoveToRelativePosition(pos[0], pos[1]);
                    time--;
                    break;
                case 1:
                    getNextPoint();
                    MoveToRelativePosition(pos[0], pos[1]);
                    time--;
                    break;
                case 0:
                    AIFinished = true;
                    break;
            }
        }
    }
}