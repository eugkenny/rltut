package rltut;

import java.awt.Color;

public class Item {

    private char glyph;
    public char glyph() { return glyph; }

    private Color color;
    public Color color() { return color; }

    private String name;
    public String name() { return name; }

    private int foodValue;
    public int foodValue() { return foodValue; }
    public void modifyFoodValue(int amount) { foodValue += amount; }

    private int attackValue;
    public int attackValue() { return attackValue; }
    public void modifyAttackValue(int amount) { attackValue += amount; }

    private int defenseValue;
    public int defenseValue() { return defenseValue; }
    public void modifyDefenseValue(int amount) { defenseValue += amount; }

    public Item(char glyph, Color color, String name){
        this.glyph = glyph;
        this.color = color;
        this.name = name;
    }

    public String details() {
        String details = "";

        if (attackValue != 0)
            details += "     attack:" + attackValue;

        if (defenseValue != 0)
            details += "     defense:" + defenseValue;

        if (foodValue != 0)
            details += "     food:" + foodValue;

        return details;
    }

}
