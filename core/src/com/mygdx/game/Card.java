package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
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

    int energyCost = 1;

    // 文本显示
    CharSequence name;
    Label nameLabel;
    CharSequence info;
    Label infoLabel;

    Card() {
        image = new Texture(Gdx.files.internal("card.png"));
        icon = new Texture(Gdx.files.internal("badlogic.jpg"));

        setSize(image.getWidth(), image.getHeight());

        name = "";
        info = "";

        updateLabels();
    }

    public boolean func(float aimx, float aimy) {
        // 卡牌执行的函数，需要覆盖
        player.consumeEnergy(energyCost);
        // 是否执行完成
        return false;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // 获取舞台和actor信息
        if (stage == null) {
            stage = (CardStage) getStage();
            player = stage.mainstage.player;
            map = stage.mainstage.map;
        }
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

    void updateLabels() {
        // 更新卡面文本
        LabelStyle nameStyle = new LabelStyle();
        nameStyle.font = Consts.NameFont;

        LabelStyle infoStyle = new LabelStyle();
        infoStyle.font = Consts.infoFont;

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
        batch.draw(image, getX(), getY());

        // 文本与卡牌的相对位置
        nameLabel.setPosition(getX() + 5, getHeight() - nameLabel.getHeight() + getY());
        infoLabel.setPosition(getX() + 5, getY());

        nameLabel.draw(batch, parentAlpha);
        infoLabel.draw(batch, parentAlpha);

        batch.draw(icon, getCenterX() - 75, getCenterY() - 35, 150, 150);
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
        // map.setInLines(player.RelativePosition.x, player.RelativePosition.y, 3, 2);
    }

    @Override
    public boolean func(float aimx, float aimy) {
        stage.addActor(new MoveCard());
        stage.addActor(new AttakCard());
        return false;
    }
}

class MoveCard extends Card {

    MoveCard() {
        maxRange = 2;
        minRange = 0;
        icon = new Texture(Gdx.files.internal(".\\icons\\move.png"));
        name = "[#000000ff]移动";
        info = String.format("[#000000ff]移动到指定位置, 移动范围为[#00ff00ff] %d [#000000ff]。\n消耗[#00ff00ff] %d [#000000ff]能量",
                maxRange, energyCost);
        updateLabels();
    }

    @Override
    public boolean func(float aimx, float aimy) {
        if (map.getPoint(aimx, aimy) == 2) {
            Array<Figure> figures = new Array<Figure>();

            // 检查位置是否占用
            figures = stage.mainstage.selectFigure(new FigureSelector(aimx, aimy) {
                public boolean select(Figure figure) {
                    return figure.RelativePosition.x == x && figure.RelativePosition.y == y;
                }
            });

            if (figures.size == 0 && player.consumeEnergy(energyCost)) {
                player.setRelativePosition(aimx, aimy);
                return true;
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
        name = "[#000000ff]攻击";
        info = String.format("[#000000ff]攻击目标，造成[#00ff00ff] %d [#000000ff]点伤害。\n消耗[#00ff00ff] %d [#000000ff]能量", damage,
                energyCost);
        updateLabels();
    }

    @Override
    public boolean func(float aimx, float aimy) {
        if (map.getPoint(aimx, aimy) == 2) {
            Array<Figure> figures = new Array<Figure>();

            figures = stage.mainstage.selectFigure(new FigureSelector(aimx, aimy) {
                public boolean select(Figure figure) {
                    return figure.RelativePosition.x == x && figure.RelativePosition.y == y;
                }
            });

            if (figures.size != 0 && player.consumeEnergy(energyCost)) {
                figures.first().getDamage(new Damage(player, Consts.PHYSICAL_DAMAGE_ID, damage));
                return true;
            }
        }
        return false;
    }
}
