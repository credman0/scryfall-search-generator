import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

public class SortedReductions {
    private ArrayList<Reduction> reductionsList = new ArrayList<>();

    public Reduction getMax() {
        return reductionsList.get(reductionsList.size()-1);
    }

    public Reduction get(int i) {
        return reductionsList.get(i);
    }

    public void purgeOutliers(double cutoff) {
        int i = 0;
        ArrayList<Reduction> removeList = new ArrayList<>();
        while (reductionsList.get(i).getAmount()<cutoff){
            removeList.add(reductionsList.get(i));
            i++;
        }
        i = reductionsList.size()-1;
        while (reductionsList.get(i).getAmount()>1-cutoff){
            removeList.add(reductionsList.get(i));
            i--;
        }
        reductionsList.removeAll(removeList);
    }

    public boolean insert(Reduction reduction) {
        int search = Collections.binarySearch(reductionsList, reduction);
        if (search >= 0) {
            return false;
        }
        int index = -1*search - 1;
        reductionsList.add(index, reduction);
        return true;
    }

    public Reduction getClosest(double amount) {
        if (reductionsList.isEmpty()){
            throw new IllegalStateException("Retrieval on empty list");
        }
        int search = Collections.binarySearch(reductionsList, new Reduction(amount, ""));
        if (search >= 0) {
            return reductionsList.get(search);
        }else {
            int index = -1*search - 1;
            if (index>=reductionsList.size()){
                return reductionsList.get(reductionsList.size()-1);
            } else {
                return reductionsList.get(index);
            }

        }
    }

    public int size(){
        return reductionsList.size();
    }
}
