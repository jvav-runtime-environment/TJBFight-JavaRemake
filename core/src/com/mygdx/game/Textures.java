package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class Textures {

    public static Texture Error = new Texture(Gdx.files.internal("badlogic.jpg"));

    public static Texture card = new Texture(Gdx.files.internal("card.png"));
    public static Texture cardDebug = new Texture(Gdx.files.internal(".\\icons\\debug.png"));
    public static Texture cardMove = new Texture(Gdx.files.internal(".\\icons\\move.png"));
    public static Texture cardAttack = new Texture(Gdx.files.internal(".\\icons\\attack.png"));
    public static Texture cardSummon = new Texture(Gdx.files.internal(".\\icons\\summon.png"));

    public static Texture textureBleed = new Texture(Gdx.files.internal(".\\status\\bleed.png"));
    public static Texture texturePoisoned = new Texture(Gdx.files.internal(".\\status\\poisoned.png"));

    public static Texture getStatusTexture(int ID) {
        switch (ID) {
            case Consts.Status_Bleed:
                return textureBleed;
            case Consts.Status_Poisoned:
                return texturePoisoned;

            default:
                return Error;
        }
    }
}
