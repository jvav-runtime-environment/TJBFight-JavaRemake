package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

class StatusManager {
    public static Texture textureError = new Texture(Gdx.files.internal("badlogic.jpg"));
    public static Texture textureBleed = new Texture(Gdx.files.internal(".\\status\\bleed.png"));

    public static Status getStatus(int ID, int level) {
        switch (ID) {
            case Consts.Status_Bleed:
                return new Bleed(level);

            default:
                return new Status(0, -1);
        }
    }

    public static Texture getStatusTexture(int ID) {
        switch (ID) {
            case Consts.Status_Bleed:
                return textureBleed;

            default:
                return textureError;
        }
    }
}

class Status {
    int level;
    int ID;

    Status(int level, int ID) {
        this.level = level;
        this.ID = ID;
    }

    public void attaching(Figure aim) {
    }

    public void turnStart(Figure aim) {
    }

    public void turnEnd(Figure aim) {
    }

    public void consumeTime(Figure aim) {
    }

    public void remove(Figure aim) {
    }
}

class Bleed extends Status {
    Bleed(int level) {
        super(level, Consts.Status_Bleed);
    }

    @Override
    public void consumeTime(Figure aim) {
        aim.getDamage(new Damage(null, Consts.damagetype.STATUS_DAMAGE, 1));
        level--;
    }

}