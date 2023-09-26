package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class Player extends Figure {
    Player() {
         health = 50;
        maxhealth = 100;

        image = new Texture(Gdx.files.internal("badlogic.jpg"));
        setSize(50, 100);
        
        setPosition(0,1000);
        setRelativePosition(4, 4);
    }

}
