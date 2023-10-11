package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

class positionedAnimation {
    Vector2 pos;
    float lifetime = 0;
    Animation<AtlasRegion> animation;

    positionedAnimation(Animation<AtlasRegion> animation, Vector2 pos) {
        this.pos = pos;
        this.animation = animation;
    }

    public void draw(Batch batch) {
        lifetime += Gdx.graphics.getDeltaTime();
        batch.draw(animation.getKeyFrame(lifetime), pos.x, pos.y);
    }
}

class Sweep1 extends positionedAnimation {
    Sweep1(Vector2 pos) {
        super(new Animation<>(0.05f, Consts.sweep1, PlayMode.NORMAL), pos);
        pos.x -= 32;
        pos.y += 32;
    }
}

public class AnimationRender {
    Array<positionedAnimation> animations = new Array<positionedAnimation>();

    public void draw(Batch batch) {
        for (positionedAnimation j : animations) {
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
