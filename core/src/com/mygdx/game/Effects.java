package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;

public class Effects {
    static ParticleEffect sparkEffect = new ParticleEffect();
    static ParticleEffect bubbleEffect = new ParticleEffect();
    static ParticleEffect releaseEffect = new ParticleEffect();
    static ParticleEffect pointerEffect = new ParticleEffect();
    static ParticleEffect flameEffect = new ParticleEffect();
    static ParticleEffect flameExplodeEffect = new ParticleEffect();

    static enum types {
        spark, bubble, release, pointer, flame
    }

    public static void load() {
        sparkEffect.load(Gdx.files.internal(".\\particles\\spark\\spark.p"), Gdx.files.internal(".\\particles\\spark"));
        bubbleEffect.load(Gdx.files.internal(".\\particles\\bubble\\bubble.p"), Gdx.files.internal(".\\particles\\bubble"));
        releaseEffect.load(Gdx.files.internal(".\\particles\\card-release\\card-release.p"), Gdx.files.internal(".\\particles\\card-release\\"));
        pointerEffect.load(Gdx.files.internal(".\\particles\\pointer\\pointer.p"), Gdx.files.internal(".\\particles\\pointer\\"));
        flameEffect.load(Gdx.files.internal(".\\particles\\flame\\flame.p"), Gdx.files.internal(".\\particles\\flame\\"));
    }

    public static ParticleEffect getEffect(types e) {
        if (sparkEffect.getEmitters().size == 0) {
            load();
        }

        switch (e) {
            case spark:
                return new ParticleEffect(sparkEffect);
            case bubble:
                return new ParticleEffect(bubbleEffect);
            case release:
                return new ParticleEffect(releaseEffect);
            case pointer:
                return new ParticleEffect(pointerEffect);
            case flame:
                return new ParticleEffect(flameEffect);

            default:
                return null;
        }
    }
}
