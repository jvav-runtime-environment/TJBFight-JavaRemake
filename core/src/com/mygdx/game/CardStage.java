package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class CardStage extends Stage {
    Card c = new DebugCard();

    Card onFocusCard;
    int cardnum;
    ShapeRenderer sr = new ShapeRenderer();

    Vector2 tempVec = new Vector2();

    CardStage(SpriteBatch batch) {
        super(new ScreenViewport(), batch);
        cardnum = 0;

        addActor(c);
    }

    @Override
    public void draw() {
        super.draw();

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
    public boolean touchDown(int x, int y, int pointer, int button) {
        boolean r = super.touchDown(x, y, pointer, button);

        // 坐标转换
        tempVec.set(x, y);
        screenToStageCoordinates(tempVec);

        // 判断是否传递事件
        if (hit(tempVec.x, tempVec.y, true) == null) {
            return r;
        } else {
            onFocusCard = (Card) hit(tempVec.x, tempVec.y, true);
            return true;
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        cardnum = getActors().size;
        int index = 1;

        for (Actor i : getActors()) {
            float d = getWidth() / (cardnum + 1);

            i.getActions().clear();

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
    }

    public int getNum() {
        return cardnum;
    }

    public Card getOnFocusCard() {
        return onFocusCard;
    }

    public void resetOnFocusCard() {
        onFocusCard = null;
    }

    public void destroyCard(Card card) {
        card.remove();
    }

    public void destroyOnFocusCard() {
        onFocusCard.remove();
        onFocusCard = null;
    }
}
