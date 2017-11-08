import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

/**
 * Utility class to read datasets stored in external files.git commit -
 *
 * @author Charles Thomas (charles.thomas@uclouvain.be)
 */
public class Utils {

    public static void main(String args[]){
        ArrayList<Transaction> tested =  readTransactions("/Users/Adrien/Documents/Ingenieur Civil/Master 1 - DATA/Q1/IntelliJ/Project 2 MPD/src/test_dataset/negative.txt");
        Map<String, Map<Integer, List<Integer>>> tested_2 =  readItemMap("/Users/Adrien/Documents/Ingenieur Civil/Master 1 - DATA/Q1/IntelliJ/Project 2 MPD/src/test_dataset/negative.txt");


        System.out.println(tested_2);
        Map<String, Map<Integer, List<Integer>>> tested_2_after = SPADE(tested_2,"AB");
        System.out.println(tested_2_after);

        // Works for "A" - "B" - "C" - "AA" - "AAB" - "BA" - "BB" - "CA"

        // Doesn't work for "AB" - "AC" - "BC"

    }

    public static Map<String, Map<Integer, List<Integer>>> SPADE( Map<String, Map<Integer, List<Integer>>> tested, String sequence){

        if (sequence.length() == 1) {

            Set<String> Set = tested.keySet();

            //First Step = Remove la premiere entree de chaque transaction dans laquelle "sequence" apparait.
            Map<Integer, List<Integer>> line_seq = tested.get(sequence); //On isole la ligne.
            ArrayList<Integer> removedEntries = new ArrayList<Integer>(); //On va stocker les entrees qu'on delete pour la suite de la methode.

            for (int i = 1; i <= line_seq.size(); i++) {
                List<Integer> transaction_i = line_seq.get(new Integer(i)); //On isole chaque transaction.
                if (transaction_i.size() != 0){
                    removedEntries.add(transaction_i.remove(0)); //On stocke l'entree qu'on remove en meme temps.
                }

            }
            Map<Integer, List<Integer>> RemainingOfsequence = tested.get(sequence);

            //Second Step = On enleve les entrees des autres items qui precedaient les entrees que l'on vient d'enlever
            Set.remove(sequence);
            Iterator<String> iteSet = Set.iterator();
            while (iteSet.hasNext()) {
                String current = iteSet.next();
                Map<Integer, List<Integer>> line_current = tested.get(current);

                for (int i = 1; i <= line_current.size(); i++) //# of Transactions
                {
                    List<Integer> transaction_current = line_current.get(i);
                    Iterator<Integer> transaction_item_current = transaction_current.iterator();
                    while (transaction_item_current.hasNext()) {
                        int current_item = transaction_item_current.next();
                        if (current_item < removedEntries.get(i - 1)) {
                            transaction_item_current.remove();
                        }
                    }
                }
            }

            tested.put(sequence, RemainingOfsequence);
        }
        else //means that the sequence is longer then just a single character
        {
           String[] sequence_array = sequence.split("");
            for (int i = 0; i < sequence_array.length ; i++) {
                SPADE(tested,sequence_array[i]);
            }


        }
            return tested;

    }


    public static int Support(Map<String, Map<Integer, List<Integer>>> tested, String sequence){

        Map<Integer, List<Integer>> line_seq = tested.get(sequence);
        Set<Integer> Set = line_seq.keySet();
        int support = 0;
        Iterator<Integer> ite = Set.iterator();

        while(ite.hasNext()){
            if(line_seq.get(ite.next()).isEmpty() == false){
                support = support + 1;
            }
        }
        return support;
    }



       public static int Wracc(int PositiveSize, int NegativeSize, int XsupportPos, int XsupportNeg){
        int first = (PositiveSize*NegativeSize/(PositiveSize + NegativeSize));
        int second = ((XsupportPos/PositiveSize ) - (XsupportNeg/NegativeSize));
        return first*second;
    }


    /**
     * Reads a dataset file and returns its transactions.
     * @param filePath the path to the file to read.
     * @return an ArrayList of Transactions.
     */
    public static ArrayList<Transaction> readTransactions(String filePath) {
        ArrayList<Transaction> transactions = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));

            Transaction currentTransaction = new Transaction();
            while (reader.ready()) {
                String line = reader.readLine();
                if(line.matches("\\s*")){
                    if(!currentTransaction.isEmpty()){
                        transactions.add(currentTransaction);
                        currentTransaction = new Transaction();
                    }
                }
                else{
                    String[] element = line.split(" ");
                    if(currentTransaction.isEmpty()) currentTransaction.setFirstPosition(Integer.parseInt(element[1]));
                    currentTransaction.addItem(element[0]);
                }
            }
            if(!currentTransaction.isEmpty()) transactions.add(currentTransaction);
            reader.close();
        }
        catch (IOException e) {
            System.err.println("Unable to read dataset file!");
            e.printStackTrace();
        }

        return transactions;
    }

    /**
     * Reads a dataset file and returns a map of its items with the transactions where they appear and their positions.
     * @param filePath the path to the file to read.
     * @return a Map of Strings (the symbol of each item) mapped to maps of Integers (the transactions where the item appear)
     *          mapped to Lists of Integers (the positions in the transaction where the item appear).
     */
    public static  Map<String, Map<Integer, List<Integer>>> readItemMap(String filePath) {
        Map<String, Map<Integer, List<Integer>>> itemMap = new HashMap<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));

            int currentTransaction = 0;
            while (reader.ready()) {
                String line = reader.readLine();
                if(line.matches("\\s*")) currentTransaction++;
                else{
                    String[] element = line.split(" ");
                    String symbol = element[0];
                    int position = Integer.parseInt(element[1]);
                    if(itemMap.containsKey(symbol)){
                        Map<Integer, List<Integer>> symbolMap = itemMap.get(symbol);
                        if(symbolMap.containsKey(currentTransaction)) symbolMap.get(currentTransaction).add(position);
                        else{
                            LinkedList<Integer> positionsList = new LinkedList<>();
                            positionsList.add(position);
                            symbolMap.put(currentTransaction, positionsList);
                        }
                    }
                    else {
                        Map<Integer, List<Integer>> symbolMap = new HashMap<>();
                        LinkedList<Integer> positionsList = new LinkedList<>();
                        positionsList.add(position);
                        symbolMap.put(currentTransaction, positionsList);
                        itemMap.put(symbol, symbolMap);
                    }

                }
            }
            reader.close();
        }
        catch (IOException e) {
            System.err.println("Unable to read dataset file!");
            e.printStackTrace();
        }

        return itemMap;
    }
}
