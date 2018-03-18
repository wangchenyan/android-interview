package me.wcy.code.merchant_guide_to_the_galaxy;

import java.util.regex.Pattern;

/**
 * Created by wcy on 2018/3/18.
 */
public class Main {
    private static final Pattern DEFINE_COUNT_PATTERN = Pattern.compile("([a-z]+) is ([IVXLCDM])");
    private static final Pattern DEFINE_PRICE_PATTERN = Pattern.compile("([a-zA-Z]+) is ([IVXLCDM])");

    public static void main(String[] args) {
        Rome[] romes = new Rome[]{Rome.I, Rome.X, Rome.V, Rome.I, Rome.I};
        System.out.println(compute(romes) + "");
    }

    private static boolean valid(Rome[] romes) {
        if (romes == null || romes.length == 0) {
            return false;
        }

        for (int i = 0; i < romes.length; i++) {
            Rome rome = romes[i];
            if (i < romes.length - 1) {
                Rome next = romes[i + 1];
                if (!rome.isNextValid(next)) {
                    return false;
                }
            }
        }

        for (int i = 0; i < romes.length; i++) {
            Rome rome = romes[i];
            if (i < romes.length - 1) {
                Rome next1 = romes[i + 1];
                if (rome.equals(next1)) {
                    if (!rome.isRepeatable()) {
                        return false;
                    }
                    if (i < romes.length - 3) {
                        Rome next2 = romes[i + 2];
                        Rome next3 = romes[i + 3];
                        if (rome.equals(next2) && rome.equals(next3)) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    private static int compute(Rome[] romes) {
        int total = 0;
        for (int i = 0; i < romes.length; i++) {
            Rome rome = romes[i];
            Rome next;
            if (i < romes.length - 1 && (next = romes[i + 1]).getValue() > rome.getValue()) {
                total += (next.getValue() - rome.getValue());
                i++;
            } else {
                total += rome.getValue();
            }
        }

        return total;
    }
}
