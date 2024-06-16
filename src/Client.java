import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class Client {

    private static ArrayList<String> detransformeCards(String transformedCards) {
        ArrayList<String> cards = new ArrayList<>();
        String[] cardStrings = transformedCards.split(",");
        for (String card : cardStrings) {
            cards.add(card.trim());
        }
        return cards;
    }

    public static void main(String[] args) {

        try {

            System.out.println("Bine ai venit la jocul de Blackjack!");

            // Inițializare conexiune cu dealerul
            Socket s = new Socket("localhost", 9999);

            BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));

            PrintWriter out = new PrintWriter(s.getOutputStream(), true);

            try {

                while (true) {

                    boolean continueGame = true;
                    while (continueGame) {

                        double playerMoney = Double.parseDouble(br.readLine());
                        System.out.println("Ai " + playerMoney + "RON");
                        // Mesaj "Cat vrei sa pariezi?"
                        String message = br.readLine();
                        System.out.println(message);
                        // Valorea pariata
                        double playerBet = Double.parseDouble(userIn.readLine());
                        out.println(playerBet);
                        out.flush();

                        while (playerBet > playerMoney) {
                            // "Nu poti paria mai mult decat ai! Incearca din nou!";
                            message = br.readLine();
                            System.out.println(message);

                            playerBet = Double.parseDouble(userIn.readLine());
                            out.println(playerBet);
                            out.flush();
                            System.out.println("Clientul pariaza : " + playerBet + " RON");
                        }

                        String transformedCardsPlayer = br.readLine();
                        ArrayList<String> receivedCardsPlayer = detransformeCards(transformedCardsPlayer);
                        System.out.println("Cartile clientului : [" + receivedCardsPlayer.get(0) + "], [" + receivedCardsPlayer.get(1) + "]");

                        String transformedCardsDealer = br.readLine();
                        ArrayList<String> receivedCardsDealer = detransformeCards(transformedCardsDealer);
                        System.out.println("Cartile dealerului : [" + receivedCardsDealer.get(0) + "], [Ascunsa]");

                        int enterDealerCardsValue = Integer.parseInt(br.readLine().trim());

                        int enterPlayerCardsValue = Integer.parseInt(br.readLine().trim());
                        System.out.println("Valoarea cartilor clientului : " + enterPlayerCardsValue);
                        System.out.println();

                        // Mesaj primit "1 sau 2?"
                        message = br.readLine();
                        System.out.println(message);

                        if (playerBet == 0) {
                            break;
                        } else {
                            // Am trimis raspunsul 1 sau 2
                            int response = Integer.parseInt(userIn.readLine());
                            out.println(response);
                            out.flush();

                            if (response == 2) {
                                System.out.println("Cartea ascunsa a dealerului : [" + receivedCardsDealer.get(1) + "]");
                            }

                            if (response == 2 && enterPlayerCardsValue == 21) {
                                System.out.println("Cartile dealerului : " + receivedCardsDealer);
                                System.out.println("Valoarea cartilor dealerului : " + enterDealerCardsValue);
                                System.out.println("FELICITARI AI FACUT BLACKJACK!");
                                System.out.println();
                                break;
                            }

                            while (enterDealerCardsValue < 17 && response == 2) {
                                transformedCardsDealer = br.readLine();
                                receivedCardsDealer = detransformeCards(transformedCardsDealer);

                                int dealerCardsValue = Integer.parseInt(br.readLine().trim());
                                enterDealerCardsValue = dealerCardsValue;
                                System.out.println("Dealerul a tras : [" + receivedCardsDealer.get(receivedCardsDealer.size() - 1) + "]");
                            }

                            if (response == 2) {
                                if (enterDealerCardsValue >= 17) {
                                    System.out.println("Cartile dealerului : " + receivedCardsDealer);
                                    System.out.println("Valoarea cartilor dealerului : " + enterDealerCardsValue);
                                }

                                if (enterPlayerCardsValue == enterDealerCardsValue) {
                                    System.out.println("REMIZA1!");
                                    System.out.println();
                                } else if (enterPlayerCardsValue <= 21 && (enterPlayerCardsValue > enterDealerCardsValue || enterDealerCardsValue >= 22)) {
                                    System.out.println("CLIENTUL A CASTIGAT!");
                                    System.out.println();
                                } else {
                                    System.out.println("CLIENTUL A PIERDUT!");
                                    System.out.println();
                                }
                            }
                            while (response != 2) {

                                transformedCardsPlayer = br.readLine();
                                receivedCardsPlayer = detransformeCards(transformedCardsPlayer);
                                System.out.println("Dealerul imi da cartea : [" + receivedCardsPlayer.get(receivedCardsPlayer.size() - 1) + "]");
                                System.out.println("Cartile clientului : " + receivedCardsPlayer);

                                int playerCardsValue = Integer.parseInt(br.readLine().trim());
                                enterPlayerCardsValue = playerCardsValue;
                                System.out.println("Valoarea cartilor clientului : " + enterPlayerCardsValue);

                                if (enterPlayerCardsValue >= 22) {
                                    System.out.println("Cartea ascunsa a dealerului : [" + receivedCardsDealer.get(1) + "]");
                                    System.out.println("Cartile dealerului : " + receivedCardsDealer);
                                    System.out.println("Valoarea cartilor dealerului : " + enterDealerCardsValue);
                                    System.out.println("CLIENTUL A PIERDUT!");
                                    System.out.println();
                                    break;
                                }
                                // Mesaj primit "1 sau 2?"
                                message = br.readLine();
                                System.out.println(message);

                                response = Integer.parseInt(userIn.readLine());
                                out.println(response);
                                out.flush();
                                
                                if (response == 2) {

                                    System.out.println("Cartea ascunsa a dealerului : [" + receivedCardsDealer.get(1) + "]");

                                    while (enterDealerCardsValue < 17) {
                                        transformedCardsDealer = br.readLine();
                                        receivedCardsDealer = detransformeCards(transformedCardsDealer);

                                        int dealerCardsValue = Integer.parseInt(br.readLine().trim());
                                        enterDealerCardsValue = dealerCardsValue;
                                        System.out.println("Dealerul a tras : [" + receivedCardsDealer.get(receivedCardsDealer.size() - 1) + "]");
                                    }

                                    if (enterDealerCardsValue >= 17)
                                        System.out.println("Cartile dealerului : " + receivedCardsDealer);
                                    System.out.println("Valoarea cartilor dealerului : " + enterDealerCardsValue);

                                    if (enterPlayerCardsValue == enterDealerCardsValue) {
                                        System.out.println("REMIZA!");
                                        System.out.println();
                                    } else if (enterPlayerCardsValue <= 21 && (enterPlayerCardsValue > enterDealerCardsValue || enterDealerCardsValue >= 22)) {
                                        System.out.println("CLIENTUL A CASTIGAT!");
                                        System.out.println();
                                    } else {
                                        System.out.println("CLIENTUL A PIERDUT!");
                                        System.out.println();
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("TI-AI PIERDUT TOTI BANII!");
            }
        }
        catch (SocketException e) {
                System.out.println("AI RULAT CLIENTUL INAINTE SA RULEZI SERVERUL! " + e.getMessage());
            }
        catch (IOException e) {
                System.out.println("A apărut o eroare de I/O: " + e.getMessage());
            }
    }
}
