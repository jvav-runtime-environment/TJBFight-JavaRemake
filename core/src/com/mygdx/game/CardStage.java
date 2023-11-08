package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class CardStage extends Stage {
    int cardnum;

    Array<Card> handcard = new Array<>();

    Vector2 tempVec = new Vector2();

    CardStage(SpriteBatch batch) {
        super(new ScreenViewport(), batch);
    }

    @Override
    public void draw() {
        super.draw();

        for (Card i : handcard) {
            if (i.onFocus) {
                ShapeRenderer sr = Consts.sr;
                float startx, starty;

                startx = i.getCenterX();
                starty = i.getCenterY();

                // 三角形点计算
                float k, a, p1X, p1Y, p2X, p2Y, lEndx, lEndy;
                float endx = Gdx.input.getX(), endy = i.stage.getHeight() - Gdx.input.getY();
                float m = Consts.ArrowLength, n = Consts.ArrowWidth;

                k = (starty - endy) / (startx - endx);// 斜率
                a = MathUtils.atan(k);// 角度

                // p1X:n*cos(a+((π)/(2)))+m*cos(a)*((x(B))/(abs(x(B))))
                // p1y:n*sin(a+((π)/(2)))+m*sin(a)*((x(B))/(abs(x(B))))
                p1X = n * MathUtils.cos(a + MathUtils.HALF_PI)
                        + m * MathUtils.cos(a) * Math.signum(startx - endx) + endx;
                p1Y = n * MathUtils.sin(a + MathUtils.HALF_PI)
                        + m * MathUtils.sin(a) * Math.signum(startx - endx) + endy;

                p2X = -n * MathUtils.cos(a + MathUtils.HALF_PI)
                        + m * MathUtils.cos(a) * Math.signum(startx - endx) + endx;
                p2Y = -n * MathUtils.sin(a + MathUtils.HALF_PI)
                        + m * MathUtils.sin(a) * Math.signum(startx - endx) + endy;

                lEndx = m * MathUtils.cos(a) * Math.signum(startx - endx) + endx;
                lEndy = m * MathUtils.sin(a) * Math.signum(startx - endx) + endy;

                // 绘制准备
                sr.setColor(0, 1, 1, 1);
                sr.setProjectionMatrix(Consts.cardstage.getCamera().combined);
                sr.begin(ShapeType.Filled);

                sr.rectLine(startx, starty, lEndx, lEndy, 30);
                sr.triangle(endx, endy, p1X, p1Y, p2X, p2Y);

                sr.end();
                break;
            }
        }

    }

    @Override
    public void addActor(Actor actor) {
        super.addActor(actor);
        handcard.add((Card) actor);
        updateCardPos();
    }

    @Override
    public void act() {
        super.act();

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

    public void updateCardPos() {
        cardnum = handcard.size;

        int index = 1;
        for (Card i : handcard) {
            float d = getWidth() / (cardnum + 1);
            i.setAimPos(d * index - i.getWidth() / 2, 0);

            index++;
        }
    }

    public int getNum() {
        return handcard.size;
    }

    public void destroyCard(Card card) {
        card.remove();
        handcard.removeValue(card, false);
        updateCardPos();
    }

    public void freeCard(Card card) {
        // 卡牌使用
        handcard.removeValue(card, false);
        card.getActions().clear();
        card.clearListeners();

        // 动画:淡出
        AlphaAction action = new AlphaAction();
        action.setDuration(0.5f);
        action.setInterpolation(Interpolation.sineOut);

        card.addAction(action);

        card.setAimPos(card.getX(), card.getY() + 200);

        card.releaseEffect.setPosition(card.getCenterX(), card.getCenterY());
        card.releaseEffect.start();

        updateCardPos();
    }
}
