package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
//import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

class FigureSelector {
    int health, maxhealth, armor;
    float x, y;

    FigureSelector() {
    }

    FigureSelector(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public boolean select(Figure figure) {
        // 选择器，需要重写
        return false;
    }
}

class positionedAnimation {
    Vector2 pos;
    float lifetime = 0;
    Animation<AtlasRegion> animation;

    positionedAnimation(Animation<AtlasRegion> animation, Vector2 pos) {
        this.pos = pos;
        this.animation = animation;
    }

    public void draw(Batch batch) {
        lifetime += Gdx.graphics.getDeltaTime();
        batch.draw(animation.getKeyFrame(lifetime), pos.x, pos.y);
    }
}

class Sweep1 extends positionedAnimation {
    Sweep1(Vector2 pos) {
        super(new Animation<>(0.05f, Consts.sweep1, PlayMode.NORMAL), pos);
        pos.x -= 32;
        pos.y += 32;
    }
}

public class MainStage extends Stage {
    int round = 0;
    boolean playerTurn = true;

    Array<Enemy> enemies = new Array<Enemy>();
    Array<positionedAnimation> animations = new Array<positionedAnimation>();

    Map map = new Map();
    Player player = new Player();
    ShapeRenderer sr = new ShapeRenderer();
    CardStage cardstage;

    boolean isDraggingMap = false;
    Vector2 tempVec = new Vector2();

    MainStage(SpriteBatch batch) {
        super(new ScreenViewport(), batch);

        cardstage = new CardStage(batch);
        cardstage.bind(this);

        addActor(map);
        addActor(player);
        addEnemy(new Enemy());
    }

    @Override
    public void draw() {
        super.draw();

        Figure figure;

        for (Actor i : getActors()) {
            if (!(i instanceof Figure)) {
                continue;
            }

            // 类转换
            figure = (Figure) i;

            // 计算血条位置
            float hpBarx, hpBary;
            hpBarx = figure.getCenterX() - Consts.BarWidth / 2;
            hpBary = figure.getY() - Consts.BarHeight * 3;

            // 图形准备
            sr.setProjectionMatrix(getCamera().combined);
            sr.begin(ShapeType.Filled);
            sr.setColor(Color.GOLD);

            // 血条绘制
            sr.rect(hpBarx, hpBary, Consts.BarWidth, Consts.BarHeight);
            sr.setColor(0, 0, 0, 0);
            sr.rect(hpBarx + 1, hpBary + 1, Consts.BarWidth - 2, Consts.BarHeight - 2);
            sr.setColor(Color.RED);
            sr.rect(hpBarx + 1, hpBary + 1, figure.getHealthPercent() * (Consts.BarWidth - 2), Consts.BarHeight - 2);

            // 结束
            sr.end();

            Batch batch = getBatch();
            batch.setProjectionMatrix(getCamera().combined);

            batch.begin();

            for (positionedAnimation j : animations) {
                j.draw(batch);
                if (j.animation.isAnimationFinished(j.lifetime)) {
                    animations.removeValue(j, false);
                }
            }

            batch.end();

            cardstage.draw();
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        cardstage.act(delta);

        // 地图设置
        map.resetAll();

        if (cardstage.onFocusCard != null) {
            cardstage.onFocusCard.setMap();

            // 超出范围显示红色
            tempVec.set(Gdx.input.getX(), Gdx.input.getY());
            screenToStageCoordinates(tempVec);
            if (Consts.getRelativePosition(tempVec.x, tempVec.y) != null) {
                tempVec = Consts.getRelativePosition(tempVec.x, tempVec.y);
                if (map.getPoint(tempVec.x, tempVec.y) == 1) {
                    map.setPoint(tempVec, 3);
                }
            }
        }

        if (playerTurn) {
            if (player.allFinished()) {
                enemyTurnStart();
            }
        } else {
            if (isEnemyAllFinished()) {
                round++;
                playerTurnStart();
            }
        }
    }

    void playerTurnStart() {
        System.out.println("player turn");
        playerTurn = true;
        player.recoverEnergy();
    }

    void enemyTurnStart() {
        System.out.println("enemy turn");
        playerTurn = false;
    }

    boolean isEnemyAllFinished() {
        for (Enemy i : enemies) {
            if (!i.allFinished()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        boolean r = super.touchDown(x, y, pointer, button);

        // 坐标转换
        tempVec.set(x, y);
        screenToStageCoordinates(tempVec);

        // 判断是否拖动地图
        if (hit(tempVec.x, tempVec.y, true) == null) {
            isDraggingMap = true;
        }

        return r;
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer) {
        boolean r = super.touchDragged(x, y, pointer);
        OrthographicCamera cam = (OrthographicCamera) getCamera();

        // 移动地图
        if (isDraggingMap) {
            cam.translate(-Gdx.input.getDeltaX() * cam.zoom, Gdx.input.getDeltaY() * cam.zoom, 0);
        }

        return r;
    }

    /*
     * public Figure getFigureByPosition(float x, float y) {
     * for (Actor i : getActors()) {
     * if (i == map) {
     * continue;
     * }
     * 
     * Figure f = (Figure) i;
     * if (f.RelativePosition.x == x && f.RelativePosition.y == y) {
     * return f;
     * }
     * }
     * return null;
     * }
     */

    @Override
    public boolean scrolled(float amountX, float amountY) {
        boolean r = super.scrolled(amountX, amountY);
        OrthographicCamera cam = (OrthographicCamera) getCamera();

        // 滚轮调整缩放
        cam.zoom += amountY / 10;

        // 控制缩放范围
        cam.zoom = MathUtils.clamp(cam.zoom, 0.1f, 3);

        return r;
    }

    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
        addActor(enemy);
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        boolean r = super.touchUp(x, y, pointer, button);
        boolean done = false;

        isDraggingMap = false;

        if (playerTurn) {

            // 坐标转换
            tempVec.set(x, y);
            screenToStageCoordinates(tempVec);

            Figure f = (Figure) hit(tempVec.x, tempVec.y, true);

            // 检查是否有目标？如果有就以目标位置执行，如果没有是否在某个位置？如果有，以位置执行
            if (cardstage.getOnFocusCard() != null) {
                if (f != null) {
                    done = cardstage.getOnFocusCard().func(f.RelativePosition.x, f.RelativePosition.y);
                } else {
                    Vector2 position = Consts.getRelativePosition(tempVec.x, tempVec.y);

                    if (position != null && map.testPointReachable(position.x, position.y)) {
                        done = cardstage.getOnFocusCard().func(position.x, position.y);
                    }
                }
            }

            // 检查执行是否成功并处理卡牌
            if (done) {
                cardstage.destroyOnFocusCard();
            } else {
                cardstage.resetOnFocusCard();
            }
        }

        return r;
    }

    public Array<Figure> selectFigure(FigureSelector selector) {
        Array<Figure> figures = new Array<Figure>();

        // 选择figure
        for (Actor i : getActors()) {
            if (!(i instanceof Figure)) {
                continue;
            }

            Figure figure = (Figure) i;

            if (selector.select(figure)) {
                figures.add(figure);
            }
        }

        return figures;
    }

    public void addAnimation(positionedAnimation animation) {
        animations.add(animation);
    }
}