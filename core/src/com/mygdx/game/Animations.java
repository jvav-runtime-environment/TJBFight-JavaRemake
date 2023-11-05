package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

public class Animations {
    // 横扫1动画
    private static TextureAtlas textureAtlas = new TextureAtlas(Gdx.files.internal("animations.atlas"));

    public static Animation<AtlasRegion> sweep1 = new Animation<>(0.03f, textureAtlas.findRegions("sweep/page"), PlayMode.NORMAL);
    public static Animation<AtlasRegion> playerRest = new Animation<>(0.1f, textureAtlas.findRegions("figure/player/rest/page"), PlayMode.NORMAL);
    public static Animation<AtlasRegion> playerAttack = new Animation<>(0.05f, textureAtlas.findRegions("figure/player/attack/page"), PlayMode.NORMAL);

    public static SequenceAction getShakingAction(float range, int count) {
        int seg = -1;

        SequenceAction rAction = new SequenceAction();

        // 初始移动
        MoveByAction actionStart = new MoveByAction();
        actionStart.setAmountX(range);
        actionStart.setDuration(0.02f);

        rAction.addAction(actionStart);

        // 中段移动
        for (int i = count; i > 0; i--) {
            MoveByAction action = new MoveByAction();
            action.setAmountX(range * 2 * seg);
            action.setDuration(0.04f);

            rAction.addAction(action);

            seg *= -1;
        }

        // 回归原本位置
        MoveByAction actionEnd = new MoveByAction();
        actionEnd.setAmountX(range * seg);
        actionEnd.setDuration(0.02f);

        rAction.addAction(actionEnd);

        return rAction;
    }
}

class positionedAnimation {
    float x, y;
    float lifetime = 0;
    Animation<AtlasRegion> animation;

    positionedAnimation(Animation<AtlasRegion> animation, float x, float y) {
        this.x = x;
        this.y = y;
        this.animation = animation;
    }

    public void draw(Batch batch) {
        lifetime += Gdx.graphics.getDeltaTime();
        batch.draw(animation.getKeyFrame(lifetime), x, y - Consts.FigureWidth / 2, Consts.FigureWidth,
                Consts.FigureWidth);
    }
}

class Sweep1 extends positionedAnimation {
    Sweep1(float x, float y) {
        super(Animations.sweep1, x, y);
    }
}

class AnimationRender {
    Array<positionedAnimation> animations = new Array<positionedAnimation>();

    public void draw(Batch batch) {
        // 不使用for-each防止出错
        for (int i = animations.size - 1; i >= 0; i--) {
            positionedAnimation j = animations.get(i);

            j.draw(batch);
            if (j.animation.isAnimationFinished(j.lifetime)) {
                animations.removeIndex(i);
            }
        }
    }

    public void addAnimation(positionedAnimation animation) {
        animations.add(animation);
    }

}

class AnimationManager {
    public static enum State {
        rest, attack
    }

    Animation<AtlasRegion> rest, attak;
    float lifetime = 0;
    State presentState = State.rest;

    AnimationManager() {

    }

    public void flipx() {
        getWithoutUpdate().flip(true, false);
    }

    public void flipy() {
        getWithoutUpdate().flip(false, true);
    }

    public void setAnimation(State state, Animation<AtlasRegion> animation) {
        switch (state) {
            case rest:
                rest = animation;
                break;
            case attack:
                attak = animation;
                break;
        }
    }

    public void setState(State state) {
        presentState = state;
        lifetime = 0;
    }

    private AtlasRegion getRest() {
        if (rest == null) {
            return Textures.Error;
        }

        if (rest.isAnimationFinished(lifetime)) {
            lifetime = 0;
        }

        return rest.getKeyFrame(lifetime);
    }

    private AtlasRegion getAttack() {
        if (attak == null) {
            presentState = State.rest;
            return Textures.Error;
        }

        if (attak.isAnimationFinished(lifetime)) {
            lifetime = 0;
            presentState = State.rest;
        }

        return attak.getKeyFrame(lifetime);
    }

    private AtlasRegion getWithoutUpdate() {
        switch (presentState) {
            case rest:
                return getRest();
            case attack:
                return getAttack();
        }
        return null;
    }

    public AtlasRegion get() {
        lifetime += Gdx.graphics.getDeltaTime();

        switch (presentState) {
            case rest:
                return getRest();
            case attack:
                return getAttack();
        }
        return null;
    }
}
