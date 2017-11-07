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
