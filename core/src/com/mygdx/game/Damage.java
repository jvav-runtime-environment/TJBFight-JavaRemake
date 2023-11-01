package com.mygdx.game;

import com.badlogic.gdx.utils.Array;

public class Damage {
    Object Starter;
    Consts.damagetype DamageType;
    int ammont;
    Array<Status> status = new Array<>();

    Damage(Object Starter, Consts.damagetype DamageType, int ammont) {
        this.Starter = Starter;
        this.DamageType = DamageType;
        this.ammont = ammont;
    }

    public void addStatus(int ID, int level) {
        status.add(Status.getStatus(ID, level));
    }
}
