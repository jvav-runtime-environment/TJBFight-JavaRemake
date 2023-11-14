package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Align;
// import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

class CardListener extends InputListener {
    Vector2 tempVec = new Vector2();
    Card card;

    CardListener(Card card) {
        this.card = card;
    }

    @Override
    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        if (!card.onFocus && !Gdx.input.isButtonPressed(0)) {
            card.setMap();
            card.setAimPosY(0);
        }
    }

    @Override
    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
        if (!card.onFocus && !Gdx.input.isButtonPressed(0)) {
            card.setAimPosY(-100);
            Consts.map.resetAll();
        }
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        if (Consts.mainstage.playerTurn) {
            card.onFocus = true;
        }
        return Consts.mainstage.playerTurn;
    }

    @Override
    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
        boolean done = false;
        card.onFocus = false;
        Figure f;
        card.stage.updateCardPos();

        // 坐标转换
        Consts.mainstage.screenToStageCoordinates(tempVec.set(Gdx.input.getX(), Gdx.input.getY()));

        Actor a = Consts.mainstage.hit(tempVec.x, tempVec.y, true);

        if (a instanceof Figure) {
            f = (Figure) a;
        } else {
            f = null;
        }

        // 检查是否有目标？如果有就以目标位置执行，如果没有是否在某个位置？如果有，以位置执行
        if (f != null) {
            done = card.func(f.relativePosition.x, f.relativePosition.y);
        } else {
            Vector2 position = Map.getRelativePosition(tempVec.x, tempVec.y);
            if (position != null && Consts.map.testPointReachable(position.x, position.y)) {
                done = card.func(position.x, position.y);
            }
        }

        // 检查执行是否成功并处理卡牌
        if (done) {
            card.stage.freeCard(card);
        }

        Consts.map.resetAll();
    }

    @Override
    public void touchDragged(InputEvent event, float x, float y, int pointer) {
        card.setAimPos(Gdx.input.getX(), card.stage.getHeight() - Gdx.input.getY());
        card.setMap();
    }
}

class Card extends Actor {
    Vector2 tempVec = new Vector2();
    AtlasRegion image, icon;
    CardStage stage;
    Player player;
    Map map;
    boolean onFocus = false;
    int maxRange, minRange;
    float aimx, aimy;

    ParticleEffect releaseEffect = Effects.getEffect(Effects.types.release);

    int timeCost = 1;

    // 文本显示
    String name;
    Label nameLabel;
    String info;
    Label infoLabel;

    Card() {
        image = Textures.card;
        icon = Textures.Error;

        setSize(image.getRegionWidth(), image.getRegionHeight() + 100);

        name = "Error";
        info = "error";

        updateLabels();

        stage = Consts.cardstage;
        player = Consts.mainstage.player;
        map = Consts.mainstage.map;

        addListener(new CardListener(this));
    }

    public boolean func(float aimx, float aimy) {
        // 卡牌执行的函数，需要覆盖
        // 是否执行完成
        return false;
    }

    public void setPointStatus() {
        // 设置地图
        map.setInRange(player.relativePosition.x, player.relativePosition.y, minRange, maxRange);
    }

    public float getCenterX() {
        return getX() + getWidth() / 2;
    }

    public float getCenterY() {
        return getY() + getHeight() / 2;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        float d = (float) Math.sqrt(Math.pow(getX() - aimx, 2) + Math.pow(getY() - aimy, 2));

        if (d <= 1) {
            setPosition(aimx, aimy);
        } else {
            if (!onFocus) {
                setPosition((aimx - getX()) / 5 + getX(), (aimy - getY()) / 5 + getY());
            } else {
                setPosition((aimx - getCenterX() - 500) / 5 + getX(), (aimy - getY()) / 5 + getY());
            }
        }
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

    public void setAimPos(float x, float y) {
        aimx = x;
        aimy = y;
    }

    public void setAimPosX(float x) {
        aimx = x;
    }

    public void setAimPosY(float y) {
        aimy = y;
    }

    public void setMap() {
        // 地图设置
        map.resetAll();

        setPointStatus();

        // 超出范围显示红色
        tempVec.set(Gdx.input.getX(), Gdx.input.getY());
        Consts.mainstage.screenToStageCoordinates(tempVec);

        Vector2 rVec = Map.getRelativePosition(tempVec.x, tempVec.y);
        if (rVec != null) {
            if (map.getPoint(rVec.x, rVec.y) == 1) {
                map.setPoint(rVec, 3);
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = batch.getColor();
        color.a = getColor().a;

        batch.setColor(color);
        batch.draw(image, getX(), getY() + 100);

        // 文本与卡牌的相对位置
        nameLabel.setPosition(getX() + 5, getHeight() - nameLabel.getHeight() - 5 + getY());
        infoLabel.setPosition(getX() + 5, getY() + 100);

        nameLabel.draw(batch, getColor().a);
        infoLabel.draw(batch, getColor().a);

        batch.draw(icon, getCenterX() - 75, getCenterY() + 15, 150, 150);

        batch.setColor(color.r, color.g, color.b, color.a);

        releaseEffect.draw(batch, Gdx.graphics.getDeltaTime());
    }
}

class DebugCard extends Card {
    DebugCard() {
        maxRange = 999;
        minRange = -1;
        icon = Textures.cardDebug;
        name = "[#ff0000ff]调试";
        info = "[#ff0000ff]增加一张卡。";
        updateLabels();
    }

    @Override
    public boolean func(float aimx, float aimy) {
        stage.addActor(new AttakCard());
        stage.addActor(new MoveCard());
        stage.addActor(new SummonCard());
        stage.addActor(new RecoverCard());
        return false;
    }
}

class MoveCard extends Card {

    MoveCard() {
        maxRange = 2;
        minRange = 0;
        icon = Textures.cardMove;
        name = "[#000000ff]移动-[#ff0000ff]DEBUG";
        info = String.format("[#000000ff]移动到指定位置, 移动范围为[#00ff00ff] %d [#000000ff]。", maxRange, timeCost);
        updateLabels();
    }

    @Override
    public boolean func(float aimx, float aimy) {
        if (map.getPoint(aimx, aimy) == 2) {
            if (!Consts.map.testPointHasFigure(aimx, aimy) && player.consumeTime(timeCost)) {
                player.MoveToRelativePosition(aimx, aimy);
                Consts.cardstage.addActor(new MoveCard());
                return true;
            }
        }
        return false;
    }
}

class AttakCard extends Card {
    int damage;

    AttakCard() {
        maxRange = 1;
        minRange = 0;
        damage = 20;
        icon = Textures.cardAttack;
        name = "[#000000ff]攻击-[#ff0000ff]DEBUG";
        info = String.format("[#000000ff]攻击目标，造成[#00ff00ff] %d [#000000ff]点伤害。", damage, timeCost);
        updateLabels();
    }

    @Override
    public boolean func(float aimx, float aimy) {
        if (map.getPoint(aimx, aimy) == 2) {
            // Array<Figure> figures = new Array<Figure>();

            // figures = Consts.mainstage.selectFigure(new FigureSelector(aimx, aimy) {
            // public boolean select(Figure figure) {
            // return figure.relativePosition.x == x && figure.relativePosition.y == y &&
            // figure.allFinished();
            // }
            // });

            if (player.consumeTime(timeCost)) {
                // Figure figure = figures.first();

                Damage d = new Damage(player, Consts.damageType.PHYSICAL_DAMAGE, damage);
                d.addStatus(Consts.Status_Bleed, 5);
                d.addStatus(Consts.Status_Poisoned, 5);
                int dir = map.getDirection(player.relativePosition.x, player.relativePosition.y, aimx, aimy);
                if (dir == -1) {
                    return false;
                }
                // player.attack(figure, d);
                Bullet b = new Bullet(d, player.relativePosition.x, player.relativePosition.y, 3, dir, player.team);

                Consts.mainstage.addActor(b);
                Consts.cardstage.addActor(new AttakCard());
                return true;
            }
        }
        return false;
    }
}

class SummonCard extends Card {

    SummonCard() {
        maxRange = 99;
        minRange = 0;
        icon = Textures.cardSummon;
        name = "[#000000ff]召唤-[#ff0000ff]DEBUG";
        info = String.format("[#000000ff]召唤一个敌人，用于DEBUG");
        updateLabels();
    }

    @Override
    public boolean func(float aimx, float aimy) {
        if (map.getPoint(aimx, aimy) == 2) {

            if (!Consts.map.testPointHasFigure(aimx, aimy)) {
                Enemy enemy = new DebugEnemy(aimx, aimy);

                Consts.mainstage.addActor(enemy);
                Consts.cardstage.addActor(new SummonCard());
                return true;
            }
        }
        return false;
    }
}

class RecoverCard extends Card {
    int ammont = 1;

    RecoverCard() {
        maxRange = 0;
        minRange = -1;
        icon = Textures.cardRecoverTime;
        name = "[#000000ff]恢复时间-[#ff0000ff]DEBUG";
        info = String.format("[#000000ff]恢复[#00ff00ff] %d [#000000ff]点时间", ammont);
        updateLabels();
    }

    @Override
    public boolean func(float aimx, float aimy) {
        player.time += 1;
        Consts.cardstage.addActor(new RecoverCard());
        return true;

    }
}
