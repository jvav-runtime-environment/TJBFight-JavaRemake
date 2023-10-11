package com.mygdx.game;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

class DamageNumber {
    int lifetime;
    int ammont;
    BitmapFont font;
    Vector2 velocity = new Vector2();
    Vector2 pos = new Vector2();

    DamageNumber(float x, float y, int ammont, float fromx) {
        this.ammont = ammont;

        font = Consts.getDamageRenderFont();

        pos.x = x;
        pos.y = y + 32;

        lifetime = 80;
        velocity.y = 16;

        // 判断落点方向
        if (fromx <= x) {
            velocity.x = MathUtils.random(0.00f, 2.01f);
        } else {
            velocity.x = MathUtils.random(-2.01f, 0.00f);
        }
    }

    protected void draw(Batch batch) {
        lifetime--;

        font.draw(batch, String.format("%d", ammont), pos.x, pos.y);

        if (lifetime >= 40) {
            velocity.y -= 0.8f;
            pos.add(velocity);
        }
    }
}

public class DamageRender {
    Array<DamageNumber> numbers = new Array<>();

    public void draw(Batch batch) {
        for (DamageNumber i : numbers) {
            i.draw(batch);
            if (i.lifetime <= 0) {
                numbers.removeValue(i, false);
            }
        }
    }

    public void add(float x, float y, int ammont, float fromx) {
        numbers.add(new DamageNumber(x, y, ammont, fromx));
    }
}
