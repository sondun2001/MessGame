package com.rebelo.messgame.objects.projectiles.equippables;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by sondu on 8/16/2016.
 */
public interface IEquippable {
    public int getNumHandsRequired(); // Does this require 1 or 2 hands to have active?
    public void drop(); // Drops equipped item on the floor
    public void equip(); // Turns on / enables equipment and reloads (called when equipping this item)
    public void unequip();  // Turns off / disables equipment and returns ammo / resources to inventory (called before equipping another item)
    public void activate(float xPos, float yPos, Vector2 direction, float force, float delta); // While trigger is being pressed
    public void deactivate(); // When trigger is released
    public void reload(); // Retrieves necessary ammmo/resources from inventory
    public void cycleOptions(); // Goes through available options of equipment
}
