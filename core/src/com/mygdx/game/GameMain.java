package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameMain extends ApplicationAdapter {
	SpriteBatch batch;
	BitmapFont font;
	InputMultiplexer inputProcessor;

	MainStage mainstage;

	@Override
	public void create() {
		batch = new SpriteBatch();

		inputProcessor = new InputMultiplexer();

		mainstage = new MainStage(batch);

		// 设置舞台事件监听
		inputProcessor.addProcessor(0, Consts.cardstage);
		inputProcessor.addProcessor(1, mainstage);
		Gdx.input.setInputProcessor(inputProcessor);

	}

	@Override
	public void render() {
		ScreenUtils.clear(0, 0, 0, 1);

		mainstage.act(Gdx.graphics.getDeltaTime());
		mainstage.draw();
	}

	@Override
	public void resize(int width, int height) {
		// 更新舞台
		mainstage.updateViewport(width, height);
	}

	@Override
	public void dispose() {
	}
}
