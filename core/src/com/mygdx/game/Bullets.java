package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;

class Bullet extends Actor {
    int direction, speed, penetrate = 1;
    Damage damage;
    Consts.team team;
    boolean collide = false, arrived = false;

    ParticleEffect effect;

    Vector2 nextpoint = new Vector2();
    Vector2 relativePosition = new Vector2();

    Vector2 tempVec = new Vector2();

    Bullet(Damage damage, float x, float y, int speed, int direction, Consts.team team) {
        this.damage = damage;
        this.speed = speed;
        this.direction = direction;
        this.team = team;

        setSize(100, 100);
        setRelativePosition(x, y);

        getNextPoint();
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
        action.setDuration(0.1f);

        addAction(action);
    }

    public void collideFigure() {
        tempVec.set(relativePosition);
        int c = penetrate;

        for (int i = speed; i > 0; i--) {
            tempVec = Consts.map.getPointByDirection(tempVec.x, tempVec.y, 1, direction);

            Figure figure = Consts.mainstage.getFigurebyPosition(tempVec.x, tempVec.y);
            if (figure != null && figure.team != team) {
                attack(figure);

                c--;
                if (c <= 0) {
                    collide = true;
                    arrived = true;
                    nextpoint.set(figure.relativePosition);
                    break;
                }
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(Textures.bulletFireBall, getX(), getY() + 25, getWidth(), getHeight());
        effect.draw(batch, Gdx.graphics.getDeltaTime());
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

        effect.setPosition(getCenterX(), getCenterY() + 25);

        if (!arrived && isFinished()) {
            arrived = true;
            getNextPoint();
        }

        Vector2 vec = Map.getRelativePosition(getCenterX(), getY());
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
        arrived = false;
        collideFigure();

        moveToRelativePosition(nextpoint.x, nextpoint.y);
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

class FireBall extends Bullet {
    FireBall(Damage damage, float x, float y, int speed, int direction, Consts.team team) {
        super(damage, x, y, speed, direction, team);
        effect = Effects.getEffect(Effects.types.flame);
    }
}