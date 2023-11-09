package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

public class Textures {
    private static TextureAtlas textureAtlas = new TextureAtlas("images.atlas");

    public static AtlasRegion Error = textureAtlas.findRegion("badlogic");

    public static AtlasRegion card = textureAtlas.findRegion("card");
    public static AtlasRegion cardDebug = textureAtlas.findRegion("icons/debug");
    public static AtlasRegion cardMove = textureAtlas.findRegion("icons/move");
    public static AtlasRegion cardAttack = textureAtlas.findRegion("icons/attack");
    public static AtlasRegion cardSummon = textureAtlas.findRegion("icons/summon");
    public static AtlasRegion cardRecoverTime = textureAtlas.findRegion("icons/recovertime");

    public static AtlasRegion textureBleed = textureAtlas.findRegion("status/bleed");
    public static AtlasRegion texturePoisoned = textureAtlas.findRegion("status/poisoned");

    public static AtlasRegion getStatusTexture(int ID) {
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
