import java.util.ArrayList;
import java.util.Random;

public class Deck {

    private ArrayList<Card> cards;

    public Deck(){
        this.cards = new ArrayList<>();
    }

    public Card getCard(int i){
        return this.cards.get(i);
    }

    public void addCard(Card addCard){
         this.cards.add(addCard);
    }

    public void removeCard(int i){
        this.cards.remove(i);
    }

    public void createFullDeck() {
        for (Suit suit : Suit.values()) {
            for (Value value : Value.values()) {
                Card newCard = new Card(suit, value);
                this.cards.add(newCard);
            }
        }
    }
    public void shuffle() {
        ArrayList<Card> tmpDeck = new ArrayList<>();
        Random random = new Random();

        while (!this.cards.isEmpty()) {
            int randomCardIndex = random.nextInt(this.cards.size());
            tmpDeck.add(this.cards.remove(randomCardIndex));
        }

        this.cards = tmpDeck;
    }

    @Override
    public String toString() {
        StringBuilder cardListOutput = new StringBuilder();
        for (Card card : this.cards) {
            cardListOutput.append("\n").append(card.toString());
        }
        return cardListOutput.toString();
    }


    // Draw from the Jack
    public void draw(Deck comingFrom) {
        Card drawnCard = comingFrom.getCard(0);
        this.cards.add(drawnCard);
        comingFrom.removeCard(0);
    }

    public int deckSize(){
        return this.cards.size();
    }

    public void moveAllToDeck(Deck moveTo) {
        int thisDeckSize = this.cards.size();

        // Transferăm cărțile în pachetul moveTo
        for (int i = 0; i < thisDeckSize; i++) {
            Card cardToMove = this.getCard(0);
            moveTo.addCard(cardToMove);
            this.removeCard(0);
        }
    }

    // Return total value of cards in deck
    public int cardsValue(){
        int totalValue = 0;
        int aces = 0;

        for (Card aCard : this.cards){
            switch (aCard.getValue()){
                case DOI: totalValue += 2; break;
                case TREI: totalValue += 3; break;
                case PATRU: totalValue += 4 ; break;
                case CINCI: totalValue += 5; break;
                case SASE: totalValue += 6; break;
                case SAPTE: totalValue += 7; break;
                case OPT: totalValue += 8; break;
                case NOUA: totalValue += 9; break;
                case ZECE: totalValue += 10; break;
                case JUVETE: totalValue += 10; break;
                case DAMA: totalValue += 10; break;
                case POPA: totalValue += 10; break;
                case AS: aces += 1; break;
            }
        }

        for (int i = 0; i < aces; i++){
            if (totalValue > 10){
                totalValue += 1;
            }
            else{
                totalValue += 11;
            }
        }

        return totalValue;
    }
}
