package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class Fonts {
    // 字体
    private static FreeTypeFontGenerator defaultFont_TTF = new FreeTypeFontGenerator(
            Gdx.files.internal(".\\fonts\\default.otf"));

    private static BitmapFont damageFont;
    private static BitmapFont infoFont;
    private static BitmapFont nameFont;

    private static int defaultFontSize;
    private static String chars = "";

    public static BitmapFont defaultFont;

    public static BitmapFont getInfoFont(String s) {
        boolean hasNewChar = false;

        for (char i : s.toCharArray()) {
            if (!chars.contains(String.valueOf(i))) {
                hasNewChar = true;
                chars += String.valueOf(i);
            }
        }

        if (hasNewChar) {
            return getInfoFont();
        } else {
            return infoFont;
        }
    }

    public static BitmapFont getInfoFont() {

        FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = 24;
        fontParameter.characters += chars;
        fontParameter.mono = true;

        infoFont = defaultFont_TTF.generateFont(fontParameter);

        infoFont.getData().markupEnabled = true;

        return infoFont;
    }

    public static BitmapFont getNameFont(String s) {
        boolean hasNewChar = false;

        for (char i : s.toCharArray()) {
            if (!chars.contains(String.valueOf(i))) {
                hasNewChar = true;
                chars += String.valueOf(i);
            }
        }

        if (hasNewChar) {
            return getNameFont();
        } else {
            return nameFont;
        }
    }

    public static BitmapFont getNameFont() {

        FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = 40;
        fontParameter.characters += chars;
        fontParameter.mono = true;

        nameFont = defaultFont_TTF.generateFont(fontParameter);

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
            fontParameter.mono = true;

            damageFont = defaultFont_TTF.generateFont(fontParameter);
        }

        return damageFont;
    }

    public static BitmapFont getDefaultFont(int size) {
        if (defaultFontSize != size) {
            defaultFontSize = size;

            FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            fontParameter.size = defaultFontSize;
            fontParameter.mono = true;

            defaultFont = defaultFont_TTF.generateFont(fontParameter);
        }

        return defaultFont;
    }

    public static BitmapFont getDefaultFont() {
        if (defaultFontSize != 16) {
            defaultFontSize = 16;

            FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            fontParameter.size = defaultFontSize;
            fontParameter.mono = true;

            defaultFont = defaultFont_TTF.generateFont(fontParameter);
        }

        return defaultFont;
    }
}
