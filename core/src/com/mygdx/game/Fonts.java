package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class Fonts {
    // 字体
    private static FreeTypeFontGenerator font = new FreeTypeFontGenerator(
            Gdx.files.internal("C:\\Windows\\Fonts\\simhei.ttf"));
    
    public static BitmapFont getInfoFont(CharSequence s) {
        FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = 24;
        fontParameter.characters += s;

        BitmapFont rfont = font.generateFont(fontParameter);

        rfont.getData().markupEnabled = true;

        return rfont;
    }

    public static BitmapFont getNameFont(CharSequence s) {
        FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = 40;
        fontParameter.characters += s;

        BitmapFont rfont = font.generateFont(fontParameter);

        rfont.getData().markupEnabled = true;

        return rfont;
    }

    public static BitmapFont getDamageRenderFont() {
        FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.borderWidth = 1.6f;
        fontParameter.borderColor = Color.WHITE;
        fontParameter.size = 30;
        fontParameter.color = Color.RED;

        return font.generateFont(fontParameter);
    }
}
