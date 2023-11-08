package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

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

public class MainStage extends Stage {
    int round = 0;
    boolean playerTurn = true;

    Array<Enemy> enemies = new Array<Enemy>();

    Map map = new Map();
    Player player = new Player();
    FPSLogger l = new FPSLogger();
    ShapeRenderer sr = new ShapeRenderer();
    DamageRender damageRender = new DamageRender();
    AnimationRender animationRender = new AnimationRender();
    GlyphLayout layout = new GlyphLayout();

    ParticleEffect pointerEffect = Effects.getEffect(Effects.types.pointer);

    CardStage cardstage;

    boolean isDraggingMap = false;
    Vector2 tempVec = new Vector2();

    MainStage(SpriteBatch batch) {
        super(new ScreenViewport(), batch);

        cardstage = new CardStage(batch);

        // 注入常量
        Consts.mainstage = this;
        Consts.map = map;
        Consts.sr = sr;
        Consts.cardstage = cardstage;
        Consts.damageRender = damageRender;
        Consts.animationRender = animationRender;

        player.setRelativePosition(4, 4);
        addActor(player);
        addEnemy(new DebugEnemy(8, 4));
        cardstage.addActor(new DebugCard());

        pointerEffect.start();
    }

    @Override
    public void draw() {
        Batch batch = getBatch();
        getCamera().update();
        batch.setProjectionMatrix(getCamera().combined);

        map.draw(batch);

        for (Enemy i : enemies) {
            i.drawArrowtoAim();
        }

        super.draw();

        Figure figure;
        for (Actor i : getActors()) {
            // 类转换
            figure = (Figure) i;

            // 计算血条位置
            float hpBarx, hpBary;
            hpBarx = figure.getCenterX() - Consts.BarWidth / 2;
            hpBary = figure.getY() - Consts.BarHeight;

            // 计时器计算
            float timerleft, timery;
            timerleft = figure.getCenterX() - (figure.time - 1) * Consts.timerSpace / 2;
            timery = i.getY() + 6;

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

            // 计时器绘制
            sr.setColor(Color.YELLOW);
            for (int j = figure.time; j > 0; j--) {
                sr.circle(timerleft, timery, Consts.timeBallRadius);
                timerleft += Consts.timerSpace;
            }

            // 结束
            sr.end();
        }

        // 动画和伤害数字显示
        batch.begin();

        for (Actor i : getActors()) {
            figure = (Figure) i;
            figure.drawStatus(batch);

            // 血量数字显示
            layout.setText(Fonts.getDefaultFont(), "[#0099ffff]" + figure.getHealthRatio());
            Fonts.getDefaultFont().draw(batch, layout, figure.getCenterX() - layout.width / 2,
                    figure.getY() - 1);
        }

        animationRender.draw(batch);
        damageRender.draw(batch);
        pointerEffect.draw(batch, Gdx.graphics.getDeltaTime());

        batch.end();

        cardstage.draw();

    }

    @Override
    public void act(float delta) {
        super.act(delta);
        cardstage.act(delta);

        if (playerTurn) {
            pointerEffect.setPosition(player.getCenterX(), player.getTop());

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
        player.recoverTime();

        for (Enemy i : enemies) {
            i.statusTurnEnd();
        }
        player.statusTurnStart();
    }

    void enemyTurnStart() {
        System.out.println("enemy turn");
        playerTurn = false;

        for (Enemy i : enemies) {
            i.recoverTime();
        }

        for (Enemy i : enemies) {
            i.statusTurnStart();
        }
        player.statusTurnEnd();
    }

    boolean isEnemyAllFinished() {
        for (Enemy i : enemies) {
            if (!i.AIFinished) {
                pointerEffect.setPosition(i.getCenterX(), i.getTop());
                i.AI();
                return false;
            }
        }

        // 重置AI状态
        for (Enemy i : enemies) {
            i.AIFinished = false;
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
        isDraggingMap = false;

        return r;
    }

    public void updateViewport(int width, int height) {
        getViewport().update(width, height, false);
        cardstage.getViewport().update(width, height, true);
        cardstage.updateCardPos();
    }

    public Array<Figure> selectFigure(FigureSelector selector) {
        Array<Figure> figures = new Array<Figure>();

        // 选择figure
        for (Actor i : getActors()) {
            Figure figure = (Figure) i;

            if (selector.select(figure)) {
                figures.add(figure);
            }
        }

        return figures;
    }
}