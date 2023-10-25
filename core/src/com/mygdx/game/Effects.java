package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;

public class Effects {
    static ParticleEffect hitEffect = new ParticleEffect();
    static ParticleEffect deathEffect = new ParticleEffect();
    static ParticleEffect releaseEffect = new ParticleEffect();
    static ParticleEffect pointerEffect = new ParticleEffect();

    static enum types {
        hit, death, release, pointer
    }

    public static void load() {
        hitEffect.load(Gdx.files.internal(".\\particles\\spark\\spark.p"),
                Gdx.files.internal(".\\particles\\spark"));

        deathEffect.load(Gdx.files.internal(".\\particles\\bubble\\bubble.p"),
                Gdx.files.internal(".\\particles\\bubble"));

        releaseEffect.load(Gdx.files.internal(".\\particles\\card-release\\card-release.p"),
                Gdx.files.internal(".\\particles\\card-release\\"));

        pointerEffect.load(Gdx.files.internal(".\\particles\\pointer\\pointer.p"),
                Gdx.files.internal(".\\particles\\pointer\\"));
    }

    public static ParticleEffect getEffect(types e) {
        if (hitEffect.getEmitters().size == 0) {
            load();
        }

        switch (e) {
            case hit:
                return new ParticleEffect(hitEffect);
            case death:
                return new ParticleEffect(deathEffect);
            case release:
                return new ParticleEffect(releaseEffect);
            case pointer:
                return new ParticleEffect(pointerEffect);

            default:
                return null;
        }
    }
}
