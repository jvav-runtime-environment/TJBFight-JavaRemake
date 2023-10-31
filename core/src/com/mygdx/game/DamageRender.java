package com.mygdx.game;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

class DamageNumber {
    int lifetime;
    float velocityX, velocityY, x, y;
    GlyphLayout layout = new GlyphLayout();

    DamageNumber(float x, float y, int ammont) {
        layout.setText(Fonts.getDamageRenderFont(), String.valueOf(ammont));

        this.x = x - layout.width / 2;
        this.y = y - layout.height / 2;

        lifetime = 80;
        velocityY = 32;

        velocityX = MathUtils.random(-4, 4);

    }

    protected void draw(Batch batch) {
        lifetime--;

        Fonts.getDamageRenderFont().draw(batch, layout, x, y);

        if (lifetime >= 40) {
            velocityY -= 1.6f;
            x += velocityX;
            y += velocityY;
        }
    }
}

public class DamageRender {
    Array<DamageNumber> numbers = new Array<>();

    public void draw(Batch batch) {
        for (int i = numbers.size - 1; i >= 0; i--) {
            DamageNumber j = numbers.get(i);

            j.draw(batch);
            if (j.lifetime <= 0) {
                numbers.removeValue(j, false);
            }
        }
    }

    public void add(float x, float y, int ammont) {
        numbers.add(new DamageNumber(x, y, ammont));
    }
}
