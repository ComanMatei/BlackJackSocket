import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Dealer {

    private static String transformeCards(ArrayList<String> cards) {
        StringBuilder trasformedCards = new StringBuilder();
        for (String card : cards) {
            trasformedCards.append(card).append(",");
        }
        if (trasformedCards.length() > 0) {
            trasformedCards.deleteCharAt(trasformedCards.length() - 1);
        }
        return trasformedCards.toString();
    }

    public static void main(String[] args) {

        try {

            System.out.println("Dealerul a deschis jocul!");
            ServerSocket ss = new ServerSocket(9999);

            System.out.println("Dealerul asteapta ca un client sa se alature!");

            Socket s = ss.accept();

            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);

            System.out.println("Clientul s-a asezat!");
            System.out.println();

            Deck playingDeck = new Deck();
            playingDeck.createFullDeck();
            playingDeck.shuffle();

            // Create a deck for the player and delaer
            Deck playerDeck = new Deck();
            Deck dealerDeck = new Deck();

            double playerMoney = 100.00;
            out.println(playerMoney);
            out.flush();

            while (playerMoney > 0) {

                String message = "Cat doresti sa pariezi?";
                out.println(message);
                out.flush();

                double playerBet = Double.parseDouble(br.readLine().trim());
                System.out.println("Clientul pariaza : " + playerBet + " RON");

                while(playerBet > playerMoney) {
                        message = "Nu poti paria mai mult decat ai! Incearca din nou!";
                        System.out.println(message);
                        System.out.println();
                        out.println(message);
                        out.flush();

                        playerBet = Double.parseDouble(br.readLine().trim());
                        System.out.println("Clientul pariaza : " + playerBet + " RON");
                }

                boolean endRound = false;

                ArrayList<String> cardsPlayer = new ArrayList<>();
                ArrayList<String> cardsDealer = new ArrayList<>();

                playerDeck.draw(playingDeck);
                playerDeck.draw(playingDeck);
                cardsPlayer.add(String.valueOf(playerDeck.getCard(0)));
                cardsPlayer.add(String.valueOf(playerDeck.getCard(1)));

                String transformedCardsPlayer = transformeCards(cardsPlayer);
                out.println(transformedCardsPlayer);

                // Deealer gest two cards
                dealerDeck.draw(playingDeck);
                dealerDeck.draw(playingDeck);
                cardsDealer.add(String.valueOf(dealerDeck.getCard(0)));
                cardsDealer.add(String.valueOf(dealerDeck.getCard(1)));

                String transformedCardsDealer = transformeCards(cardsDealer);
                out.println(transformedCardsDealer);

                int dealerCardsValue = dealerDeck.cardsValue();
                out.println(dealerCardsValue);
                out.flush();

                while (true) {
                    System.out.println("Mana clientului : " + cardsPlayer);
                    System.out.println("Valoarea cartilor clientului : " + playerDeck.cardsValue());

                    int playerCardsValue = playerDeck.cardsValue();
                    out.println(playerCardsValue);
                    out.flush();

                    System.out.println("Mana dealerului : [" + dealerDeck.getCard(0).toString() + "], [Ascunsa]");

                    // Alege 1 sau 2
                    message = "Ce vrei sa faci (1)Trage sau (2)Stai?";
                    out.println(message);
                    out.flush();

                    int enterResponse = Integer.parseInt(br.readLine().trim());
                    System.out.println();
                    System.out.println("Raspunsul clientului : " + enterResponse);

                    if (enterResponse == 1) {
                        playerDeck.draw(playingDeck);
                        System.out.println("Clientul a tras : [" + playerDeck.getCard(playerDeck.deckSize() - 1).toString() + "]");
                        System.out.println("Cartile clientului : " + transformedCardsPlayer);

                        cardsPlayer.add(String.valueOf(playerDeck.getCard(playerDeck.deckSize() - 1)));
                        transformedCardsPlayer = transformeCards(cardsPlayer);
                        out.println(transformedCardsPlayer);
                        out.flush();

                        // Bust if > 21
                        if (playerDeck.cardsValue() > 21) {
                            System.out.println("Ai pierdut! Valoarea cartilor este : " + playerDeck.cardsValue());
                            int endPlayerCardsValue = playerDeck.cardsValue();
                            out.println(endPlayerCardsValue);
                            out.flush();

                            playerMoney -= playerBet;
                            out.println(playerMoney);
                            out.flush();

                            endRound = true;
                            break;
                        }
                    }

                    if (enterResponse == 2)
                        break;
                }
                // Reveal Dealer Cards
                // Dealer draws at 16, stand at 17
                System.out.println("Cartea ascunsa a dealerului : [" + dealerDeck.getCard(1) + "]");

                if(cardsPlayer.size() == 2 && playerDeck.cardsValue() == 21 && dealerDeck.cardsValue() < 21 && endRound == false){
                    System.out.println("Cartile dealerului : " + cardsDealer);
                    // Display total value for Dealer
                    System.out.println("Valoarea cartilor dealerului: " + dealerDeck.cardsValue());

                    System.out.println("CLIENTUL A FACUT BLACKJACK!");
                    playerMoney += playerBet * 1.5;
                    out.println(playerMoney);
                    out.flush();

                    endRound = true;
                }

                while (dealerDeck.cardsValue() < 17 && endRound == false) {
                    dealerDeck.draw(playingDeck);
                    System.out.println("Dealerul trage : [" + dealerDeck.getCard(dealerDeck.deckSize() - 1).toString() + "]");

                    cardsDealer.add(String.valueOf(dealerDeck.getCard(dealerDeck.deckSize() - 1)));
                    transformedCardsDealer = transformeCards(cardsDealer);
                    out.println(transformedCardsDealer);

                    int enterDealerCardsValue = dealerDeck.cardsValue();
                    out.println(enterDealerCardsValue);
                    out.flush();
                }
                if(endRound == false) {
                    System.out.println("Cartile dealerului : " + cardsDealer);
                    // Display total value for Dealer
                    System.out.println("Valoarea cartilor dealerului: " + dealerDeck.cardsValue());
                }

                // Determine if dealer busted
                if ((dealerDeck.cardsValue() > 21) && endRound == false) {
                    System.out.println("AI CASTIGAT!");
                    playerMoney += playerBet;
                    out.println(playerMoney);
                    out.flush();

                    endRound = true;
                }

                if ((dealerDeck.cardsValue() > playerDeck.cardsValue()) && endRound == false) {
                    System.out.println("AI PIERDUT!");
                    playerMoney -= playerBet;
                    out.println(playerMoney);
                    out.flush();

                    endRound = true;
                }

                // Determine if push
                if ((playerDeck.cardsValue() == dealerDeck.cardsValue()) && endRound == false) {
                    System.out.println("REMIZA!");
                    out.println(playerMoney);
                    out.flush();
                    endRound = true;
                }

                if (playerDeck.cardsValue() > dealerDeck.cardsValue() && endRound == false) {
                    System.out.println("AI CASTIGAT!");
                    playerMoney += playerBet;
                    out.println(playerMoney);
                    out.flush();
                }

                playerDeck.moveAllToDeck(playingDeck);
                dealerDeck.moveAllToDeck(playingDeck);
                System.out.println("Sfarsitul rundei!");
                System.out.println();

            }
            System.out.println("Clientul si-a pierdut toti banii!");
        }
        catch (IOException e) {
            System.out.println("Jocul a luat sfârșit!");
        }
    }
}


