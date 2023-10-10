package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

class DamageNumber {
    int lifetime;
    int ammont;
    BitmapFont font;
    Vector2 velocity = new Vector2();
    Vector2 pos = new Vector2();

    DamageNumber(float x, float y, int ammont, float fromx) {
        font = Consts.getDamageRenderFont();
        this.ammont = ammont;
        pos.x = x;
        pos.y = y;

        lifetime = 120;
        velocity.y = 8;

        if (fromx <= x) {
            velocity.x = MathUtils.random(0, 2.0f);
        } else {
            velocity.x = MathUtils.random(-2.0f, 0);
        }

    }

    protected void draw(Batch batch) {
        lifetime--;

        font.draw(batch, String.format("%d", ammont), pos.x, pos.y);

        velocity.y -= 0.15;
        pos.add(velocity);
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
