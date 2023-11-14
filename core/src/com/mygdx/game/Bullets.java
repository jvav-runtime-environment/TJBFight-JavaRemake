package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;

class Bullet extends Actor {
    int direction, speed;
    Vector2 nextpoint = new Vector2();
    Vector2 relativePosition = new Vector2();
    Damage damage;
    Consts.team team;
    boolean collide = false;

    Vector2 tempVec = new Vector2();

    Bullet(Damage damage, float x, float y, int speed, int direction, Consts.team team) {
        this.damage = damage;
        this.speed = speed;
        this.direction = direction;
        this.team = team;

        setSize(50, 50);
        setRelativePosition(x, y);
        getNextPoint();
        debug();
    }

    public void getNextPoint() {
        nextpoint.set(Consts.map.getPointByDirection(relativePosition.x, relativePosition.y, speed, direction));
    }

    public void moveToRelativePosition(float x, float y) {
        relativePosition.set((int) x, (int) y);

        // 移动动画
        Vector2 vec = Map.getAbsPosition(relativePosition.x, relativePosition.y);
        MoveToAction action = new MoveToAction();

        action.setPosition(vec.x - getWidth() / 2, vec.y);
        action.setDuration(0.3f);

        addAction(action);
    }

    public void drawArrowtoAim() {
        ShapeRenderer sr = Consts.sr;
        float startx = getCenterX(), starty = getY();
        Vector2 vec = Map.getAbsPosition(nextpoint.x, nextpoint.y);
        float endx = vec.x, endy = vec.y;

        // 三角形点计算
        float k, a, p1X, p1Y, p2X, p2Y, lEndx, lEndy;
        float m = Consts.ArrowLength, n = Consts.ArrowWidth;

        k = (starty - endy) / (startx - endx);// 斜率
        a = MathUtils.atan(k);// 角度

        // p1X:n*cos(a+((π)/(2)))+m*cos(a)*((x(B))/(abs(x(B))))
        // p1y:n*sin(a+((π)/(2)))+m*sin(a)*((x(B))/(abs(x(B))))
        p1X = n * MathUtils.cos(a + MathUtils.HALF_PI) + m * MathUtils.cos(a) * Math.signum(startx - endx) + endx;
        p1Y = n * MathUtils.sin(a + MathUtils.HALF_PI) + m * MathUtils.sin(a) * Math.signum(startx - endx) + endy;

        p2X = -n * MathUtils.cos(a + MathUtils.HALF_PI) + m * MathUtils.cos(a) * Math.signum(startx - endx) + endx;
        p2Y = -n * MathUtils.sin(a + MathUtils.HALF_PI) + m * MathUtils.sin(a) * Math.signum(startx - endx) + endy;

        lEndx = m * MathUtils.cos(a) * Math.signum(startx - endx) + endx;
        lEndy = m * MathUtils.sin(a) * Math.signum(startx - endx) + endy;

        // 绘制准备
        sr.setColor(1, 1, 0, 1);
        sr.setProjectionMatrix(Consts.mainstage.getCamera().combined);
        sr.begin(ShapeType.Filled);

        sr.rectLine(startx, starty, lEndx, lEndy, 10);
        sr.triangle(endx, endy, p1X, p1Y, p2X, p2Y);

        sr.end();
    }

    public Figure testFigureCollide() {
        tempVec = Consts.map.getPointByDirection(relativePosition.x, relativePosition.y, 1, direction);
        for (int i = speed; i > 0; i--) {
            tempVec = Consts.map.getPointByDirection(tempVec.x, tempVec.y, 1, direction);
            Figure figure = Consts.mainstage.getFigurebyPosition(tempVec.x, tempVec.y);
            if (figure != null) {
                return figure;
            }
        }
        return null;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(Textures.Error, getX(), getY(), getWidth(), getHeight());
    }

    public void setRelativePosition(float x, float y) {
        relativePosition.set((int) x, (int) y);

        Vector2 vec = Map.getAbsPosition(relativePosition.x, relativePosition.y);
        setPosition(vec.x - getWidth() / 2, vec.y);
    }

    public void attack(Figure figure) {
        figure.getDamage(damage);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        Vector2 vec = Map.getRelativePosition(getX(), getY());
        if (vec != null) {
            if (!Consts.map.testPointReachable(vec.x, vec.y)) {
                kill();
                return;
            }
        }

        if (collide && isFinished()) {
            kill();
            return;
        }
    }

    public void update() {
        Figure figure = testFigureCollide();
        if (figure != null && figure.team != team) {
            moveToRelativePosition(figure.relativePosition.x, figure.relativePosition.y);
            attack(figure);
            collide = true;
        } else {
            moveToRelativePosition(nextpoint.x, nextpoint.y);
        }

        getNextPoint();
    }

    public void kill() {
        clear();
        remove();
        Consts.mainstage.bullets.removeValue(this, false);
    }

    public float getCenterX() {
        return getX() + getWidth() / 2;
    }

    public float getCenterY() {
        return getY() + getHeight() / 2;
    }

    public boolean isFinished() {
        return getActions().isEmpty();
    }
}
