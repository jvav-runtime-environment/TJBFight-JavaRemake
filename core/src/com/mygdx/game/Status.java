package com.mygdx.game;

class StatusSelector {
    public static Status getStatus(int ID, int level) {
        switch (ID) {
            case Consts.Status_Bleed:
                return new Bleed(level);
            default:
                return null;
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
        aim.getDamage(new Damage(null, Consts.damagetype.STATUS_DAMAGE, 500));
        level--;
    }

}