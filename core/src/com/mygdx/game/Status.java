package com.mygdx.game;

class Status {
    int level;
    int ID;

    public static Status getStatus(int ID, int level) {
        switch (ID) {
            case Consts.Status_Bleed:
                return new Bleed(level);
            case Consts.Status_Poisoned:
                return new Poisoned(level);

            default:
                return new Status(0, -1);
        }
    }

    Status(int level, int ID) {
        this.level = level;
        this.ID = ID;
    }

    public void attaching(Figure self) {
    }

    public void turnStart(Figure self) {
    }

    public void turnEnd(Figure self) {
    }

    public void consumeTime(Figure self) {
    }

    public void remove(Figure self) {
    }

    public void attacking(Figure aim, Damage damage) {
    }

    public void gettingDamage(Figure self, Damage damage) {
    }
}

class Bleed extends Status {
    Bleed(int level) {
        super(level, Consts.Status_Bleed);
    }

    @Override
    public void consumeTime(Figure self) {
        self.getDamage(new Damage(this, Consts.damagetype.STATUS_DAMAGE, 1));
        level--;
    }
}

class Poisoned extends Status {
    Poisoned(int level) {
        super(level, Consts.Status_Poisoned);
    }

    @Override
    public void turnStart(Figure self) {
        self.getDamage(new Damage(this, Consts.damagetype.STATUS_DAMAGE, level));
        level--;
    }
}