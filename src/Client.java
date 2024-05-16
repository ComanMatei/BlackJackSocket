import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Client {

    private static String serializeCards(ArrayList<String> cards) {
        StringBuilder serializedCards = new StringBuilder();
        for (String card : cards) {
            serializedCards.append(card).append(",");
        }
        if (serializedCards.length() > 0) {
            serializedCards.deleteCharAt(serializedCards.length() - 1);
        }
        return serializedCards.toString();
    }


    public static void main(String[] args) {

        try{

            System.out.println("Bine ai venit la jocul de BlackJack!");

            // Create playing deck
            Deck playingDeck = new Deck();
            playingDeck.createFullDeck();
            playingDeck.shuffle();

            // Create a deck for the player
            Deck playerDeck = new Deck();
            Deck dealerDeck = new Deck();

            double playerMoney = 100.00;


            Socket s = new Socket("localhost", 9999);
            BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            OutputStreamWriter os = new OutputStreamWriter(s.getOutputStream());

            while(playerMoney > 0) {
                // Play On!
                //Take the player bet
                System.out.println("Ai " + playerMoney + "RON, cat de mult vrei sa pariezi?");
                double playerBet = Double.parseDouble(userIn.readLine());

                out.println(playerBet);

                if (playerBet > playerMoney) {
                    System.out.println("Nu poti paria mai mult decat ai!");
                    break;
                }

                boolean endRound = false;

                // Start Dealing
                // Player gets two cards
                ArrayList<String> cardsPlayer = new ArrayList<>();
                ArrayList<String> cardsDealer = new ArrayList<>();

                playerDeck.draw(playingDeck);
                playerDeck.draw(playingDeck);
                cardsPlayer.add(String.valueOf(playerDeck.getCard(0)));
                cardsPlayer.add(String.valueOf(playerDeck.getCard(1)));

                String serializedCardsPlayer = serializeCards(cardsPlayer);
                out.println(serializedCardsPlayer);

                // Deealer gest two cards
                dealerDeck.draw(playingDeck);
                dealerDeck.draw(playingDeck);
                cardsDealer.add(String.valueOf(dealerDeck.getCard(0)));
                cardsDealer.add(String.valueOf(dealerDeck.getCard(1)));

                String serializedCardsDealer = serializeCards(cardsDealer);
                out.println(serializedCardsDealer);

                int dealerCardsValue = dealerDeck.cardsValue();
                out.println(dealerCardsValue);
                os.flush();

                while (true){
                    System.out.println("Mana ta :" + cardsPlayer);
                    System.out.println("Valoarea cartilor este : " + playerDeck.cardsValue());

                    int playerCardsValue = playerDeck.cardsValue();
                    out.println(playerCardsValue);
                    os.flush();

                    // Display dealer hand
                    System.out.println("Mana dealerului : [" + dealerDeck.getCard(0).toString() + "], [Ascunsa]");

                    // What does the player want to do
                    System.out.println("Ce vrei sa faci (1)Trage sau (2)Stai?");
                    int response = Integer.parseInt(userIn.readLine());
                    out.println(response);
                    os.flush();

                    // They hit
                    if(response == 1){
                        playerDeck.draw(playingDeck);
                        System.out.println("Ai tras : [" + playerDeck.getCard(playerDeck.deckSize() - 1).toString() + "]");

                        cardsPlayer.add(String.valueOf(playerDeck.getCard(playerDeck.deckSize() - 1)));
                        serializedCardsPlayer = serializeCards(cardsPlayer);
                        out.println(serializedCardsPlayer);

                        // Bust if > 21
                        if(playerDeck.cardsValue() > 21){
                            System.out.println("Ai pierdut! Valoarea cartilor este : " + playerDeck.cardsValue());
                            int endPlayerCardsValue = playerDeck.cardsValue();
                            out.println(endPlayerCardsValue);
                            os.flush();

                            playerMoney -= playerBet;
                            endRound = true;
                            break;
                        }
                    }

                    if (response == 2)
                        break;
                }

                // Reveal Dealer Cards
                // Dealer draws at 16, stand at 17
                System.out.println("Cartea ascunsa a dealerului : [" + dealerDeck.getCard(1) + "]");
                while (dealerDeck.cardsValue() < 17 && endRound == false){
                    dealerDeck.draw(playingDeck);
                    System.out.println("Dealerul trage : [" + dealerDeck.getCard(dealerDeck.deckSize() - 1).toString() + "]");

                    cardsDealer.add(String.valueOf(dealerDeck.getCard(dealerDeck.deckSize() - 1)));
                    serializedCardsDealer = serializeCards(cardsDealer);
                    out.println(serializedCardsDealer);

                    int enterDealerCardsValue = dealerDeck.cardsValue();
                    out.println(enterDealerCardsValue);
                    os.flush();
                }
                System.out.println("Cartile dealerului : " + cardsDealer);
                // Display total value for Dealer
                System.out.println("Valoarea cartilor dealerului: " + dealerDeck.cardsValue());

                // Determine if dealer busted
                if ((dealerDeck.cardsValue() > 21) && endRound == false){
                    System.out.println("AI CASTIGAT!");
                    playerMoney += playerBet;
                    endRound = true;
                }

                if ((dealerDeck.cardsValue() > playerDeck.cardsValue()) && endRound == false){
                    System.out.println("AI PIERDUT!");
                    playerMoney -= playerBet;
                    endRound = true;
                }

                // Determine if push
                if ((playerDeck.cardsValue() == dealerDeck.cardsValue()) && endRound == false){
                    System.out.println("REMIZA!");
                    endRound = true;
                }

                if(playerDeck.cardsValue() > dealerDeck.cardsValue() && endRound == false){
                    System.out.println("AI CASTIGAT!");
                    playerMoney += playerBet;
                }

                playerDeck.moveAllToDeck(playingDeck);
                dealerDeck.moveAllToDeck(playingDeck);
                System.out.println("Sfarsitul rundei!");

            }
            System.out.println("Ti-ai pierdut toti banii!");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
