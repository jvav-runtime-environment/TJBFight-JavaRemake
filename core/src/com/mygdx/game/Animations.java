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
    static TextureAtlas sweep1Atlas = new TextureAtlas(Gdx.files.internal(".\\animatoins\\sweep\\sweep.atlas"));
    static Array<TextureAtlas.AtlasRegion> sweep1 = sweep1Atlas.getRegions();

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
        super(new Animation<>(0.03f, Animations.sweep1, PlayMode.NORMAL), x, y);
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
                animations.removeValue(j, false);
            }
        }
    }

    public void addAnimation(positionedAnimation animation) {
        animations.add(animation);
    }
}
