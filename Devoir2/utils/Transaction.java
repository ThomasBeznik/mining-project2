import java.util.ArrayList;

/**
 * Represents a transaction. You will probably need to modify this class.
 */
public class Transaction{
    private ArrayList<String> items;
    private int firstPosition;

    public Transaction() {
        items = new ArrayList<>();
    }

    public boolean isEmpty(){
        return items.isEmpty();
    }

    public void setFirstPosition(int position) {
        firstPosition = position;
    }

    public void addItem(String item) {
        items.add(item);
    }

    public ArrayList<String> getItems() {
        return items;
    }

    public int getFirstPosition() {
        return isEmpty() ? -1 : firstPosition;
    }

    public String toString() {
        String s = "[";
        for(int i =0; i < items.size(); i++){
            s += "(" + items.get(i) + ", " + (i + firstPosition) + ")";
            if(i < items.size() - 1) s += ", ";
        }
        s += "]";
        return s;
    }
}
