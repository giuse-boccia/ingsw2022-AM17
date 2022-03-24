package it.polimi.ingsw.model;

public abstract class Character {

    private final int cost;
    private final CardName cardName;
    private boolean hasCoin;

    public Character(CardName cardName) {
        this.cardName = cardName;
        this.cost = cardName.getInitialCost();
        this.hasCoin = false;
    }

    public int getCost() {
        return hasCoin ? cost + 1 : cost;
    }

    public CardName getCardName() {
        return cardName;
    }

    private void addCoinAfterFirstUse() {
        hasCoin = true;
    }
}
