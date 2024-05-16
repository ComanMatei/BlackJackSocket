import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Dealer {

    private static ArrayList<String> deserializeCards(String serializedCards) {
        ArrayList<String> cards = new ArrayList<>();
        String[] cardStrings = serializedCards.split(",");
        for (String card : cardStrings) {
            cards.add(card.trim());
        }
        return cards;
    }

    public static void main(String[] args) {

        try {

            System.out.println("Dealerul a deschis jocul!");
            ServerSocket ss = new ServerSocket(9999);

            System.out.println("Dealerul asteapta ca un client sa se alature!");

            while (true) {
                Socket s = ss.accept();
                System.out.println("Clientul s-a asezat");

                BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));

                boolean continueGame = true;
                while (continueGame) {

                    double playerBet = Double.parseDouble(br.readLine().trim());
                    System.out.println("Clientul pariaza : " + playerBet);

                    String serializedCardsPlayer = br.readLine();
                    ArrayList<String> receivedCardsPlayer = deserializeCards(serializedCardsPlayer);
                    System.out.println("Cartile clientului: [" + receivedCardsPlayer.get(0) + "], [" + receivedCardsPlayer.get(1) + "]" );

                    String serializedCardsDealer = br.readLine();
                    ArrayList<String> receivedCardsDealer = deserializeCards(serializedCardsDealer);
                    System.out.println("Cartile dealerului: [" + receivedCardsDealer.get(0)  + "], [Ascunsa]");

                    int enterDealerCardsValue = Integer.parseInt(br.readLine().trim());

                    int enterPlayerCardsValue = Integer.parseInt(br.readLine().trim());
                    System.out.println("Valoarea cartilor clientului : " + enterPlayerCardsValue);
                    System.out.println();

                    if (playerBet == 0) {
                        break;
                    } else {
                        int enterResponse = Integer.parseInt(br.readLine().trim());
                        System.out.println("Raspunsul clientului : " + enterResponse);

                        if(enterResponse == 2)
                            System.out.println("Cartea ascunsa a dealerului : [" + receivedCardsDealer.get(1) + "]");

                        while (enterDealerCardsValue < 17 && enterResponse == 2){
                            serializedCardsDealer = br.readLine();
                            receivedCardsDealer = deserializeCards(serializedCardsDealer);

                            int dealerCardsValue = Integer.parseInt(br.readLine().trim());
                            enterDealerCardsValue = dealerCardsValue;
                            System.out.println("Dealerul a tras: [" + receivedCardsDealer.get(receivedCardsDealer.size() - 1) + "]");
                        }

                        if(enterResponse == 2){
                            if(enterDealerCardsValue >= 17)
                                System.out.println("Cartile dealerului : " + receivedCardsDealer);
                                System.out.println("Valoarea cartilor dealerului : " + enterDealerCardsValue);

                            if (enterPlayerCardsValue == enterDealerCardsValue){
                                System.out.println("REMIZA!");
                                System.out.println();
                            }
                            else if(enterPlayerCardsValue <= 21 && (enterPlayerCardsValue > enterDealerCardsValue || enterDealerCardsValue >= 22)){
                                System.out.println("CLIENTUL A CASTIGAT!");
                                System.out.println();
                            }
                            else {
                                System.out.println("CLIENTUL A PIERDUT!");
                                System.out.println();
                            }
                        }

                        while (enterResponse != 2) {

                            serializedCardsPlayer = br.readLine();
                            receivedCardsPlayer = deserializeCards(serializedCardsPlayer);
                            System.out.println("Dealerul ii da o carte : [" + receivedCardsPlayer.get(receivedCardsPlayer.size() - 1) + "]");
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

                            int response = Integer.parseInt(br.readLine());
                            enterResponse = response;
                            System.out.println();
                            System.out.println("Raspunsul clientului : " + enterResponse);

                            if(enterResponse == 2){

                                System.out.println("Cartea ascunsa a dealerului : [" + receivedCardsDealer.get(1) + "]");
                                while (enterDealerCardsValue < 17){
                                    serializedCardsDealer = br.readLine();
                                    receivedCardsDealer = deserializeCards(serializedCardsDealer);

                                    int dealerCardsValue = Integer.parseInt(br.readLine().trim());
                                    enterDealerCardsValue = dealerCardsValue;
                                    System.out.println("Dealerul a tras: [" + receivedCardsDealer.get(receivedCardsDealer.size() - 1) + "]");
                                }

                                if(enterDealerCardsValue >= 17)
                                    System.out.println("Cartile dealerului : " + receivedCardsDealer);
                                    System.out.println("Valoarea cartilor dealerului : " + enterDealerCardsValue);

                                if (enterPlayerCardsValue == enterDealerCardsValue){
                                    System.out.println("REMIZA!");
                                    System.out.println();
                                    break;
                                }

                                else if(enterPlayerCardsValue <= 21 && (enterPlayerCardsValue > enterDealerCardsValue || enterDealerCardsValue >= 22)){
                                    System.out.println("CLIENTUL A CASTIGAT!");
                                    System.out.println();
                                }
                                else {
                                    System.out.println("CLIENTUL A PIERDUT!");
                                    System.out.println();
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Clientul si-a pierdut toti banii!");
            System.out.println("JOCUL A LUAT SFARSIT!");
        }
    }
}