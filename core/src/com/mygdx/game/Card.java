package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

class Card extends Actor {
    Texture image;
    Texture icon;
    CardStage stage;
    Player player;
    Map map;
    int maxRange;
    int minRange;

    ParticleEffect releaseEffect = Effects.getEffect(Effects.types.release);

    int timeCost = 1;

    // 文本显示
    String name;
    Label nameLabel;
    String info;
    Label infoLabel;

    Card() {
        image = new Texture(Gdx.files.internal("card.png"));
        icon = new Texture(Gdx.files.internal("badlogic.jpg"));

        setSize(image.getWidth(), image.getHeight());

        name = "\\";
        info = "/";

        updateLabels();

        stage = Consts.cardstage;
        player = Consts.mainstage.player;
        map = Consts.mainstage.map;
    }

    public boolean func(float aimx, float aimy) {
        // 卡牌执行的函数，需要覆盖
        player.consumeTime(timeCost);
        // 是否执行完成
        return false;
    }

    public void setMap() {
        // 设置地图
        map.setInRange(player.RelativePosition.x, player.RelativePosition.y, minRange, maxRange);
    }

    public float getCenterX() {
        return getX() + getWidth() / 2;
    }

    public float getCenterY() {
        return getY() + getHeight() / 2;
    }

    protected void updateLabels() {
        // 更新卡面文本
        LabelStyle nameStyle = new LabelStyle();
        nameStyle.font = Fonts.getNameFont(name);

        LabelStyle infoStyle = new LabelStyle();
        infoStyle.font = Fonts.getInfoFont(info);

        nameLabel = new Label(name, nameStyle);
        infoLabel = new Label(info, infoStyle);

        // 设置大小，自动换行，文本居中
        nameLabel.setSize(getWidth() - 10, 80);
        nameLabel.setWrap(true);
        nameLabel.setAlignment(Align.center, Align.top);

        infoLabel.setSize(getWidth() - 10, 150);
        infoLabel.setWrap(true);
        infoLabel.setAlignment(Align.top, Align.center);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = batch.getColor();
        color.a = getColor().a;

        batch.setColor(color);
        batch.draw(image, getX(), getY());

        // 文本与卡牌的相对位置
        nameLabel.setPosition(getX() + 5, getHeight() - nameLabel.getHeight() + getY());
        infoLabel.setPosition(getX() + 5, getY());

        nameLabel.draw(batch, getColor().a);
        infoLabel.draw(batch, getColor().a);

        batch.draw(icon, getCenterX() - 75, getCenterY() - 35, 150, 150);

        batch.setColor(color.r, color.g, color.b, color.a);

        releaseEffect.draw(batch, Gdx.graphics.getDeltaTime());
    }
}

class DebugCard extends Card {
    DebugCard() {
        maxRange = 999;
        minRange = -1;
        icon = new Texture(Gdx.files.internal(".\\icons\\debug.png"));
        name = "[#ff0000ff]调试";
        info = "[#ff0000ff]增加一张卡。";
        updateLabels();
    }

    @Override
    public void setMap() {
    }

    @Override
    public boolean func(float aimx, float aimy) {
        stage.addActor(new AttakCard());
        stage.addActor(new MoveCard());
        stage.addActor(new SummonCard());
        return false;
    }
}

class MoveCard extends Card {

    MoveCard() {
        maxRange = 2;
        minRange = 0;
        icon = new Texture(Gdx.files.internal(".\\icons\\move.png"));
        name = "[#000000ff]移动-[#ff0000ff]DEBUG";
        info = String.format("[#000000ff]移动到指定位置, 移动范围为[#00ff00ff] %d [#000000ff]。\n消耗[#00ff00ff] %d [#000000ff]能量",
                maxRange, timeCost);
        updateLabels();
    }

    @Override
    public boolean func(float aimx, float aimy) {
        if (map.getPoint(aimx, aimy) == 2) {

            if (!Consts.map.testPointHasFigure(aimx, aimy) && player.consumeTime(timeCost)) {
                player.MoveToRelativePosition(aimx, aimy);
                // return true;
            }
        }
        return false;
    }
}

class AttakCard extends Card {
    int damage;

    AttakCard() {
        maxRange = 2;
        minRange = 0;
        damage = 20;
        icon = new Texture(Gdx.files.internal(".\\icons\\attack.png"));
        name = "[#000000ff]攻击-[#ff0000ff]DEBUG";
        info = String.format("[#000000ff]攻击目标，造成[#00ff00ff] %d [#000000ff]点伤害。\n消耗[#00ff00ff] %d [#000000ff]能量", damage,
                timeCost);
        updateLabels();
    }

    @Override
    public boolean func(float aimx, float aimy) {
        if (map.getPoint(aimx, aimy) == 2) {
            Array<Figure> figures = new Array<Figure>();

            figures = Consts.mainstage.selectFigure(new FigureSelector(aimx, aimy) {
                public boolean select(Figure figure) {
                    return figure.RelativePosition.x == x && figure.RelativePosition.y == y && figure.allFinished();
                }
            });

            if (figures.size != 0 && player.consumeTime(timeCost)) {
                Damage d = new Damage(player, Consts.damagetype.PHYSICAL_DAMAGE, damage);
                d.addStatus(Consts.Status_Bleed, 5);

                figures.first().getDamage(d);

                Consts.animationRender.addAnimation(new Sweep1(figures.first().getAbsPosition()));
                // return true;
            }
        }
        return false;
    }
}

class SummonCard extends Card {

    SummonCard() {
        maxRange = 99;
        minRange = 0;
        icon = new Texture(Gdx.files.internal(".\\icons\\summon.png"));
        name = "[#000000ff]召唤-[#ff0000ff]DEBUG";
        info = String.format("[#000000ff]召唤一个敌人，用于DEBUG");
        updateLabels();
    }

    @Override
    public boolean func(float aimx, float aimy) {
        if (map.getPoint(aimx, aimy) == 2) {

            if (!Consts.map.testPointHasFigure(aimx, aimy)) {
                Enemy enemy = new DebugEnemy(aimx, aimy);

                Consts.mainstage.addEnemy(enemy);
                Consts.cardstage.addActor(new SummonCard());
                return true;
            }
        }
        return false;
    }
}
