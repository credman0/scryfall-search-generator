import com.searchmachine.core.JSONHandler;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        for (int j = 0; j < 10; j++) {
            Generator generator = new Generator();
            for (int i = 0; i < 10; i++) {
                ArrayList<Reduction> reductions = generator.generateReductionsAnyNumber(0.1);
                StringBuilder queryBuilder = new StringBuilder();
                for (Reduction reduction : reductions) {
                    queryBuilder.append(reduction.getQuery() + " ");
                }
                if (Math.abs(0.10 - generator.getReductionAmount(queryBuilder.toString()))>0.015)
                    continue;
                System.out.println(queryBuilder.toString() + " ~= " + generator.getReductionAmount(queryBuilder.toString()));
            }
        }
    }

}
