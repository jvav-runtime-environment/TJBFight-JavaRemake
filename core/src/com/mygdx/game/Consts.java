package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class Consts {
    //公用常量
    static MainStage mainstage;
    static CardStage cardStage;
    static DamageRender damageRender;

    // 窗口常数
    static final int windowWidth = 800;
    static final int windowHeight = 600;

    // 状态条常数
    static final int BarHeight = 6;
    static final int BarWidth = 75;

    // 显示常数
    static final int BlockSize = 125;

    // 伤害ID
    static final int PHYSICAL_DAMAGE_ID = 0;

    // 卡面字体
    static BitmapFont infoFont = new BitmapFont(Gdx.files.internal(".\\fonts\\Hanazono-standard.fnt"),
            Gdx.files.internal(".\\fonts\\Hanazono-standard.png"),
            false);
    static BitmapFont NameFont = new BitmapFont(Gdx.files.internal(".\\fonts\\Hanazono-standard.fnt"),
            Gdx.files.internal(".\\fonts\\Hanazono-standard.png"),
            false);

    // 横扫1动画
    static TextureAtlas sweep1Atlas = new TextureAtlas(Gdx.files.internal(".\\sweep\\sweep.atlas"));
    static Array<TextureAtlas.AtlasRegion> sweep1 = sweep1Atlas.getRegions();

    // 临时向量
    private static Vector2 tempVec = new Vector2();

    // 伤害显示字体
    private static FreeTypeFontGenerator damageFont = new FreeTypeFontGenerator(
            Gdx.files.internal("C:\\Windows\\Fonts\\simhei.ttf"));
    

    public static Vector2 getAbsPosition(float x, float y) {
        float outx = MathUtils.cosDeg(120) * y + x;
        float outy = MathUtils.sinDeg(120) * y * 0.6f;

        tempVec.set(outx * BlockSize, outy * BlockSize);

        return tempVec.cpy();
    }

    private static void absPositionforDistance(float x, float y) {
        float outx = MathUtils.cosDeg(120) * y + x;
        float outy = MathUtils.sinDeg(120) * y;

        tempVec.set(outx, outy);

    }

    public static float getDistance(int x1, int y1, int x2, int y2) {
        float X1, X2, Y1, Y2;

        absPositionforDistance(x1, y1);
        X1 = tempVec.x;
        Y1 = tempVec.y;
        absPositionforDistance(x2, y2);
        X2 = tempVec.x;
        Y2 = tempVec.y;

        return (float) (Math.sqrt(Math.pow(X1 - X2, 2) + Math.pow(Y1 - Y2, 2)) - 0.001);

    }

    private static void calcRelativePosition(float x, float y) {
        // 反向转换
        x /= BlockSize;
        y /= BlockSize;

        float outy = y / (MathUtils.sinDeg(120) * 0.6f);
        float outx = x - MathUtils.cosDeg(120) * outy;

        tempVec.set(outx, outy);
    }

    public static Vector2 getRelativePosition(float x, float y) {
        // 坐标转换
        calcRelativePosition(x, y);

        // 检查位置是否在指定范围
        if ((tempVec.x % 1 >= 0.8f || tempVec.x % 1 <= 0.2f) && (tempVec.y % 1 >= 0.75f || tempVec.y % 1 <= 0.25f)) {
            tempVec.set(Math.round(tempVec.x), Math.round(tempVec.y));
            return tempVec.cpy();
        } else {
            return null;
        }
    }

    public static BitmapFont getDamageRenderFont(){
        FreeTypeFontGenerator.FreeTypeFontParameter damageFontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        damageFontParameter.borderWidth = 1.5f;
        damageFontParameter.borderColor = Color.WHITE;
        damageFontParameter.size = 48;
        damageFontParameter.color = Color.RED;

        return damageFont.generateFont(damageFontParameter);
    }
}
