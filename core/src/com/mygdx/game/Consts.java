package com.mygdx.game;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Consts {
    // 注入常量
    static Map map;
    static ShapeRenderer sr;
    static MainStage mainstage;
    static CardStage cardstage;
    static DamageRender damageRender;
    static AnimationRender animationRender;

    // 窗口常数
    static final int windowWidth = 800;
    static final int windowHeight = 600;

    // 状态条常数
    static final int BarHeight = 12;
    static final int BarWidth = 150;

    static final int timerSpace = 30;
    static final int timeBallRadius = 6;

    static final int statusIconSize = 32;

    // 显示常数
    static final int BlockSize = 250;
    static final float ArrowLength = 50;
    static final float ArrowWidth = 30;

    // 图片常数
    static final int FigureWidth = 100;
    static final int FigureHeight = 200;

    // 伤害ID
    static enum damageType {
        PHYSICAL_DAMAGE, STATUS_DAMAGE
    }

    // 队列
    static enum team {
        player, enemy
    }

    // 效果ID
    static final int Status_Bleed = 0;
    static final int Status_Poisoned = 1;

    // 计时器
    static final int EnemyTimer = 80;
}
