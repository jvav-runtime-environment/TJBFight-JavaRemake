package com.mygdx.game;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Consts {
    // 注入常量
    static ShapeRenderer sr;
    static MainStage mainstage;
    static CardStage cardstage;
    static DamageRender damageRender;
    static AnimationRender animationRender;

    // 窗口常数
    static final int windowWidth = 800;
    static final int windowHeight = 600;

    // 状态条常数
    static final int BarHeight = 6;
    static final int BarWidth = 75;

    static final int timerSpace = 15;
    static final int timeBallRadius = 3;

    // 显示常数
    static final int BlockSize = 125;
    static final float ArrowLength = 25;
    static final float ArrowWidth = 12;

    // 伤害ID
    static final int PHYSICAL_DAMAGE_ID = 0;

}
