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

    private static FreeTypeFontGenerator defaultFont_TTF = new FreeTypeFontGenerator(
            Gdx.files.internal(".\\fonts\\default.ttf"));

    private static BitmapFont damageFont;
    private static BitmapFont infoFont;
    private static BitmapFont nameFont;

    private static int defaultFontSize;
    private static String chars;

    public static BitmapFont defaultFont;

    public static BitmapFont getInfoFont(String s) {
        chars += s;
        return getInfoFont();
    }

    public static BitmapFont getInfoFont() {

        FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = 24;
        fontParameter.characters += chars;

        infoFont = font.generateFont(fontParameter);

        infoFont.getData().markupEnabled = true;

        return infoFont;
    }

    public static BitmapFont getNameFont(String s) {
        chars += s;
        return getNameFont();
    }

    public static BitmapFont getNameFont() {

        FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = 40;
        fontParameter.characters += chars;

        nameFont = font.generateFont(fontParameter);

        nameFont.getData().markupEnabled = true;

        return nameFont;
    }

    public static BitmapFont getDamageRenderFont() {
        if (damageFont == null) {
            FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            fontParameter.borderWidth = 1.6f;
            fontParameter.borderColor = Color.WHITE;
            fontParameter.size = 30;
            fontParameter.color = Color.RED;

            damageFont = defaultFont_TTF.generateFont(fontParameter);
        }

        return damageFont;
    }

    public static BitmapFont getDefaultFont(int size) {
        if (defaultFont == null) {
            defaultFontSize = size;

            FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            fontParameter.size = defaultFontSize;

            defaultFont = defaultFont_TTF.generateFont(fontParameter);
        }

        return defaultFont;
    }

    public static BitmapFont getDefaultFont() {
        if (defaultFont == null) {
            defaultFontSize = 16;

            FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            fontParameter.size = defaultFontSize;

            defaultFont = defaultFont_TTF.generateFont(fontParameter);
        }

        return defaultFont;
    }
}
