package com.rebelo.messgame.objects.projectiles.equippables;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by sondu on 8/16/2016.
 */
public interface IEquippable {
    public int getNumHandsRequired(); // Does this require 1 or 2 hands to have active?
    public void drop(); // Drops equipped item on the floor
    public void use(float xPos, float yPos, Vector2 direction, float forcePercent); // Activates equipment
    public void activate(); // Turns on / enables equipment and reloads (called when equipping this item)
    public void deactivate(); // Turns off / disables equipment and returns ammo / resources to inventory (called before equipping another item)
    public void reload(); // Retrieves necessary ammmo/resources from inventory
    public void cycleOptions(); // Goes through available options of equipment
}
