package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class Textures {
    static Texture card = new Texture(Gdx.files.internal("card.png"));
    static Texture cardError = new Texture(Gdx.files.internal("badlogic.jpg"));
    static Texture cardDebug = new Texture(Gdx.files.internal(".\\icons\\debug.png"));
    static Texture cardMove = new Texture(Gdx.files.internal(".\\icons\\move.png"));
    static Texture cardAttack = new Texture(Gdx.files.internal(".\\icons\\attack.png"));
    static Texture cardSummon = new Texture(Gdx.files.internal(".\\icons\\summon.png"));
}
