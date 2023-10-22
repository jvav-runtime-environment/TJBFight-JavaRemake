package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

class Enemy extends Figure {
    boolean AIFinished = false;
    int steps = 3;

    Enemy() {
        health = 100;
        maxhealth = 100;

        image = new Texture(Gdx.files.internal("badlogic.jpg"));
        setSize(50, 100);

        setPosition(0, 1000);
        setRelativePosition(5, 4);
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

    public void AI() {
        if (allFinished()) {
            AIFinished = true;
        }
    }
}

class DebugEnemy extends Enemy {
    int[] pos;

    private void getNextPoint() {
        pos = Consts.mainstage.map.getInRange(RelativePosition.x, RelativePosition.y, 2).random();

        while (!testPoint(pos[0], pos[1])) {
            pos = Consts.mainstage.map.getInRange(RelativePosition.x, RelativePosition.y, 2).random();
        }
    }

    @Override
    public void AI() {
        if (allFinished()) {
            switch (steps) {
                case 3:
                    getNextPoint();
                    setRelativePosition(pos[0], pos[1]);
                    steps--;
                    break;
                case 2:
                    getNextPoint();
                    setRelativePosition(pos[0], pos[1]);
                    steps--;
                    break;
                case 1:
                    getNextPoint();
                    setRelativePosition(pos[0], pos[1]);
                    steps--;
                    break;
                case 0:
                    AIFinished = true;
                    steps = 3;
                    break;
            }
        }
    }
}