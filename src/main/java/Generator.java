import com.searchmachine.core.Card;
import com.searchmachine.core.JSONHandler;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

public class Generator {
    private SortedReductions sortedReductions = new SortedReductions();
    private JSONHandler handler = new JSONHandler("/ModernCards.json");
    private ArrayList<String> cardNamesList = new ArrayList<>(handler.getCardNames());
    private Random random = new Random();
    private int setSize;

    private static final int NAME_Q_LENGTH_MIN = 1;
    private static final int NAME_Q_LENGTH_RANGE = 2;
    private static final int TEXT_Q_LENGTH_MIN = 2;
    private static final int TEXT_Q_LENGTH_RANGE = 2;

    public Generator(){
        setSize = cardNamesList.size();
        for (int i = 0; i < 512; i ++) {
            if (!sortedReductions.insert(generateReduction())){
                i--;
            }
        }
        sortedReductions.purgeOutliers(0.1);
    }

    //f:modern -p o:t}:
    public ArrayList<Reduction> generateReductions(double target) {
        ArrayList<Reduction> ret = new ArrayList<>();
        Reduction first = sortedReductions.get(random.nextInt(sortedReductions.size()));
        while (first.getAmount()*sortedReductions.getMax().getAmount() < target) {
            first = sortedReductions.get(random.nextInt(sortedReductions.size()));
        }
        ret.add(first);
        ret.add(sortedReductions.getClosest(target/first.getAmount()));
        return ret;
    }

    public ArrayList<Reduction> generateReductionsAnyNumber(double target) {
        double amount = 1;
        ArrayList<Reduction> ret = new ArrayList<>();
        while (amount > target) {
            Reduction reduction = sortedReductions.get(random.nextInt(sortedReductions.size()));
            while (reduction.getAmount() * sortedReductions.getMax().getAmount() < target) {
                reduction = sortedReductions.get(random.nextInt(sortedReductions.size()));
            }
            ret.add(reduction);
            amount*=reduction.getAmount();
        }
        return ret;
    }

    public double getReductionAmount(String query) {
        return handler.getSearchResultList(query).size()/(double)setSize;
    }

    /**
     * true if a closer than b
     * @param a
     * @param b
     * @param target
     * @return
     */
    public boolean closerTo(double a, double b, double target) {
        return Math.abs(a-target) < Math.abs(b-target);
    }

    private Reduction generateReduction() {
        String query = generateQuery();
        if (random.nextBoolean()) {
            query = "-"+query;
        }
        double amount = getReductionAmount(query);
        return new Reduction(amount,query);
    }

    private String generateQuery() {
        Card card = getRandomCard();
        if (true){
            int length = NAME_Q_LENGTH_MIN + random.nextInt(NAME_Q_LENGTH_RANGE);
            // name
            while (card.getName().length() < length) {
                card = getRandomCard();
            }
            String choice = getRandomSubstring(card.getName(), length);
            while (choice.split("\\W", -1).length!=1) {
                choice = getRandomSubstring(card.getName(), length);
            }
            return choice;
        } else {
//            int length = TEXT_Q_LENGTH_MIN + random.nextInt(TEXT_Q_LENGTH_RANGE);
//            // text
//            while (card.getText().length() < length) {
//                card = getRandomCard();
//            }
//            String choice = getRandomSubstring(card.getText(), length);
//            while (choice.split("\\s", -1).length!=1) {
//                choice = getRandomSubstring(card.getText(), length);
//            }
//            return "o:" +  choice;
            int length = 1;
            // text
            while (card.getText().length() < length) {
                card = getRandomCard();
            }
            String choice = getRandomWord(card.getText());
            while (choice.split("\\s", -1).length!=1) {
                choice = getRandomSubstring(card.getText(), length);
            }
            return "o:" +  choice;
        }
    }

    private Card getRandomCard() {
        Card card = handler.getCard(cardNamesList.get(random.nextInt(cardNamesList.size())));
        return card;
    }

    private String getRandomSubstring(String string, int size) {
        int startIndex = random.nextInt(string.length()-size);
        return string.substring(startIndex, startIndex+size);
    }

    private String getRandomWord(String string) {
        String[] words = string.split("\\W");
        return words[random.nextInt(words.length)];
    }


}
