package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class CardStage extends Stage {
    int cardnum;
    Card onFocusCard;

    Array<Card> handcard = new Array<>();

    Vector2 tempVec = new Vector2();

    CardStage(SpriteBatch batch) {
        super(new ScreenViewport(), batch);
    }

    @Override
    public void draw() {
        super.draw();

        ShapeRenderer sr = Consts.sr;
        if (onFocusCard != null) {

            // 绘制选中卡实现覆盖效果
            Batch batch = getBatch();
            batch.begin();
            onFocusCard.draw(batch, 1);
            batch.end();

            // 鼠标位置
            float mousex = Gdx.input.getX();
            float mousey = Gdx.graphics.getHeight() - Gdx.input.getY();

            // 卡牌位置
            float cardx = onFocusCard.getCenterX();
            float cardy = onFocusCard.getCenterY();

            // 图形准备
            sr.setProjectionMatrix(getCamera().combined);
            sr.begin(ShapeType.Filled);
            sr.setColor(0, 1, 0.8f, 0.5f);

            // 竖线和圆角
            sr.rectLine(cardx, cardy, cardx, mousey, 21);
            sr.circle(cardx, mousey, 10);

            // 箭头左右方向
            if (mousex > cardx) {
                sr.rectLine(cardx, mousey, mousex - 30, mousey, 21);
                sr.triangle(mousex, mousey, mousex - 30, mousey + 18, mousex - 30, mousey - 18);
            } else {
                sr.rectLine(cardx, mousey, mousex + 30, mousey, 21);
                sr.triangle(mousex, mousey, mousex + 30, mousey + 18, mousex + 30, mousey - 18);
            }

            sr.end();

        }
    }

    @Override
    public void addActor(Actor actor) {
        super.addActor(actor);
        handcard.add((Card) actor);
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        boolean r = super.touchDown(x, y, pointer, button);

        // 坐标转换
        tempVec.set(x, y);
        screenToStageCoordinates(tempVec);
        Card card = (Card) hit(tempVec.x, tempVec.y, true);

        // 判断是否传递事件
        if (card == null) {
            return r;
        } else {
            onFocusCard = card;
            if (handcard.contains(onFocusCard, false) && Consts.mainstage.playerTurn) {
                return true;
            } else {
                onFocusCard = null;
                return false;
            }
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        cardnum = handcard.size;

        int index = 1;
        for (Card i : handcard) {
            float d = getWidth() / (cardnum + 1);

            // 去除所有MoveToAction
            for (Action j : i.getActions()) {
                if (j instanceof MoveToAction) {
                    i.getActions().removeValue(j, false);
                }
            }

            MoveToAction action = new MoveToAction();

            action.setPosition(d * index - i.getWidth() / 2, 0);
            action.setInterpolation(Interpolation.circleOut);
            action.setDuration(0.8f);

            if (i.equals(onFocusCard)) {
                action.setPosition(d * index - i.getWidth() / 2, 60);
            }

            i.addAction(action);

            index++;
        }

        // 检查卡牌动画是否播放完成，并删除
        for (Actor i : getActors()) {
            Card card = (Card) i;

            if (handcard.contains(card, false)) {
                continue;
            } else {
                if (card.getActions().size == 0) {
                    card.remove();
                }
            }
        }
    }

    public int getNum() {
        return handcard.size;
    }

    public Card getOnFocusCard() {
        return onFocusCard;
    }

    public void resetOnFocusCard() {
        onFocusCard = null;
    }

    public void destroyCard(Card card) {
        card.remove();
        handcard.removeValue(card, false);
    }

    public void destroyOnFocusCard() {
        onFocusCard.remove();
        handcard.removeValue(onFocusCard, false);
        onFocusCard = null;
    }

    public void freeCard(Card card) {
        // 卡牌使用
        handcard.removeValue(card, false);
        card.getActions().clear();

        // 动画:升起并淡出
        MoveByAction maction = new MoveByAction();

        maction.setAmountY(MathUtils.random(500, 800));
        maction.setDuration(1);
        maction.setInterpolation(Interpolation.fade);

        AlphaAction aaction = new AlphaAction();
        aaction.setDuration(1);
        aaction.setInterpolation(Interpolation.sineOut);

        ParallelAction action = new ParallelAction(maction, aaction);

        card.addAction(action);
        card.releaseEffect.setPosition(card.getCenterX(), card.getCenterY());
        card.releaseEffect.start();
    }

    public void freeOnFocusCard() {
        freeCard(onFocusCard);
        onFocusCard = null;
    }
}
