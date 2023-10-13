package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

class Enemy extends Figure {
    Enemy(){
        health = 100;
        maxhealth = 100;

        image = new Texture(Gdx.files.internal("badlogic.jpg"));
        setSize(50, 100);
        
        setPosition(0,1000);
        setRelativePosition(5, 4);
    }

    public void AI(){

    }
}
