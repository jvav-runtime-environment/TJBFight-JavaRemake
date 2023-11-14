package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.utils.Array;

public class Figure extends Actor {
    int health, time, defaultTime, maxhealth, armor;
    boolean filpx = false;
    Consts.team team;
    AnimationManager aniMgr = new AnimationManager();

    Vector2 relativePosition = new Vector2();

    Array<Status> statusList = new Array<>();

    ParticleEffect hitEffect = Effects.getEffect(Effects.types.hit);
    ParticleEffect deathEffect = Effects.getEffect(Effects.types.death);

    Figure() {
        init();
    }

    Figure(float x, float y) {
        init();
        setRelativePosition(x, y);
    }

    void init() {
    }

    public void getDamage(Damage damage) {
        statusGettingDamage(damage);

        for (Status i : damage.status) {
            addStatus(i);
        }

        int ammont = 0;
        switch (damage.DamageType) {
            case PHYSICAL_DAMAGE:
                ammont = damage.ammont - armor;
                break;
            case STATUS_DAMAGE:
                ammont = damage.ammont;
                break;
        }

        health -= ammont;
        playHitEffects();

        if (damage.Starter instanceof Status) {
            Consts.damageRender.add(getCenterX(), getCenterY(), ammont, ((Status) damage.Starter).ID);
        } else {
            Consts.damageRender.add(getCenterX(), getCenterY(), ammont);
        }
    }

    private void playHitEffects() {
        if (health <= 0) {
            health = 0;

            getActions().clear();

            // 死亡动画
            AlphaAction fadeout = new AlphaAction();
            ParallelAction action = new ParallelAction();

            fadeout.setAlpha(0);
            fadeout.setDuration(0.64f);

            action.addAction(fadeout);
            action.addAction(Animations.getShakingAction(16, 15));

            addAction(action);

            // 死亡特效
            deathEffect.setPosition(getCenterX(), getCenterY());
            deathEffect.start();

        } else {
            addAction(Animations.getShakingAction(4, 5));
        }

        hitEffect.setPosition(getCenterX(), getCenterY());
        hitEffect.start();
    }

    public void MoveToRelativePosition(float x, float y) {
        relativePosition.set((int) x, (int) y);

        // 移动动画
        Vector2 vec = Map.getAbsPosition(relativePosition.x, relativePosition.y);
        MoveToAction action = new MoveToAction();

        action.setPosition(vec.x - getWidth() / 2, vec.y);
        action.setInterpolation(Interpolation.circleOut);
        action.setDuration(0.3f);

        addAction(action);

        flip(vec.x, vec.y);
    }

    public void setRelativePosition(float x, float y) {
        relativePosition.set((int) x, (int) y);

        Vector2 vec = Map.getAbsPosition(relativePosition.x, relativePosition.y);
        setPosition(vec.x - getWidth() / 2, vec.y);
        flip(vec.x, vec.y);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // 移除结束的状态
        for (int i = statusList.size - 1; i >= 0; i--) {
            Status j = statusList.get(i);

            if (j.level <= 0) {
                statusList.removeValue(j, false);
                j.remove(this);
            }
        }

        // 控制血量范围，检查是否死亡
        health = MathUtils.clamp(health, 0, maxhealth);
        if (health <= 0) {
            if (allFinished()) {
                kill();
            }
        }
    }

    public boolean consumeTime(int ammont) {
        if (ammont > time) {
            return false;
        } else {
            time -= ammont;
            statusConsumeTime();
            return true;
        }
    }

    public void attack(Figure aim, Damage damage) {
        statusAttack(aim, damage);
        aniMgr.setState(AnimationManager.State.attack);
        flip(aim.getX(), aim.getY());

        aim.getDamage(damage);

        Consts.animationRender.addAnimation(new Sweep1(getX(), getCenterY()));
    }

    public void recoverTime() {
        time = defaultTime;
    }

    public void kill() {
        statusList.clear();
        clear();
        Consts.mainstage.figures.removeValue(this, false);
        remove();
    }

    void flip(float x, float y) {
        if (x - getX() < 0) {
            filpx = true;
        } else {
            filpx = false;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = batch.getColor();
        color.a = getColor().a;

        batch.setColor(color);
        batch.draw(aniMgr.get(), filpx ? getWidth() + getX() : getX(), getY(), filpx ? -getWidth() : getWidth(), getHeight());
        batch.setColor(color.r, color.g, color.b, 1);

        hitEffect.draw(batch, Gdx.graphics.getDeltaTime());
        deathEffect.draw(batch, Gdx.graphics.getDeltaTime());
    }

    public void drawStatus(Batch batch) {
        float x = getCenterX() - (statusList.size - 1) * Consts.statusIconSize / 2 - Consts.statusIconSize / 2;
        float y = getY() - Consts.BarHeight - Consts.statusIconSize;
        GlyphLayout layout = new GlyphLayout();
        String l;

        for (Status i : statusList) {
            l = String.valueOf(i.level);
            layout.setText(Fonts.getDefaultFont(), l);

            batch.draw(Textures.getStatusTexture(i.ID), x, y, Consts.statusIconSize, Consts.statusIconSize);
            x += Consts.statusIconSize;

            Fonts.getDefaultFont().draw(batch, layout, x - layout.width, y + layout.height);
        }
    }

    public void drawArrowtoAim(float endx, float endy) {
        if (!isDying()) {
            ShapeRenderer sr = Consts.sr;
            float startx, starty;

            startx = getCenterX();
            starty = getY();

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
            sr.setColor(1, 0.4f, 0, 1);
            sr.setProjectionMatrix(Consts.mainstage.getCamera().combined);
            sr.begin(ShapeType.Filled);

            sr.rectLine(startx, starty, lEndx, lEndy, 10);
            sr.triangle(endx, endy, p1X, p1Y, p2X, p2Y);

            sr.end();
        }
    }

    public float getCenterX() {
        return getX() + getWidth() / 2;
    }

    public float getCenterY() {
        return getY() + getHeight() / 2;
    }

    public float getHealthPercent() {
        return (float) health / (float) maxhealth;
    }

    public String getHealthRatio() {
        return String.valueOf(health) + "/" + String.valueOf(maxhealth);
    }

    public Vector2 getAbsPosition() {
        return Map.getAbsPosition(relativePosition.x, relativePosition.y);
    }

    public boolean hasStatus(int ID) {
        for (Status i : statusList) {
            if (ID == i.ID) {
                return true;
            }
        }
        return false;
    }

    public Boolean isDying() {
        return !deathEffect.isComplete();
    }

    public boolean allFinished() {
        return getActions().size == 0 && hitEffect.isComplete() && deathEffect.isComplete();
    }

    public boolean hasTime() {
        return time > 0;
    }

    public void addStatus(Status s) {
        if (hasStatus(s.ID)) {
            for (Status i : statusList) {
                if (i.ID == s.ID) {
                    i.level += s.level;
                    break;
                }
            }
        } else {
            statusList.add(s);
        }

        s.attaching(this);
    }

    public void removeStatus(int ID, int level) {
        for (Status i : statusList) {
            if (i.ID == ID) {
                if (i.level <= level) {
                    i.remove(this);
                    statusList.removeValue(i, false);
                } else {
                    i.level -= level;
                }
                break;
            }
        }
    }

    public void statusTurnStart() {
        for (Status i : statusList) {
            i.turnStart(this);
        }
    }

    public void statusTurnEnd() {
        for (Status i : statusList) {
            i.turnEnd(this);
        }
    }

    private void statusConsumeTime() {
        for (Status i : statusList) {
            i.consumeTime(this);
        }
    }

    private void statusAttack(Figure aim, Damage damage) {
        for (Status i : statusList) {
            i.attacking(aim, damage);
        }
    }

    private void statusGettingDamage(Damage damage) {
        for (int i = statusList.size - 1; i >= 0; i--) {
            statusList.get(i).gettingDamage(this, damage);
        }
    }
}
