package it.polimi.ingsw.model;

public class PassiveCharacterWithColor extends PassiveCharacter {

    private Color color;

    public PassiveCharacterWithColor(CardName cardName, Color color) {
        super(cardName);
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
