package com.rebelo.messgame.map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by sondu on 8/24/2016.
 */
public class TileType {

    public final TextureRegion[] tileVariations;

    public TileType(String name, Texture texture, int regionSize){

        tileVariations = new TextureRegion[16];

        TextureRegion[][] tmp = TextureRegion.split(texture, regionSize, regionSize);

        tileVariations[0] = tmp[3][0];
        tileVariations[1] = tmp[2][0];
        tileVariations[2] = tmp[3][1];
        tileVariations[3] = tmp[2][1];
        tileVariations[4] = tmp[0][0];
        tileVariations[5] = tmp[1][0];
        tileVariations[6] = tmp[0][1];
        tileVariations[7] = tmp[1][1];
        tileVariations[8] = tmp[3][3];
        tileVariations[9] = tmp[2][3];
        tileVariations[10] = tmp[3][2];
        tileVariations[11] = tmp[2][2];
        tileVariations[12] = tmp[0][3];
        tileVariations[13] = tmp[1][3];
        tileVariations[14] = tmp[0][2];
        tileVariations[15] = tmp[1][2];

    }

}