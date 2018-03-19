package me.wcy.code.merchant_guide_to_the_galaxy;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by wcy on 2018/3/18.
 */
public class Main {
    private static final Pattern DEFINE_PATTERN = Pattern.compile("([a-z]+) is ([IVXLCDM])");

    public static void main(String[] args) {
        Map<String, Rome> romeMap = new HashMap<>();
        Map<String, Double> priceMap = new HashMap<>();

        System.out.println("please input ...");

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            try {
                String line = scanner.nextLine();
                // strip
                line = line.replaceAll(" +", " ");
                if (DEFINE_PATTERN.matcher(line).matches()) { // define count
                    String[] array = line.split(" is ");
                    Rome rome = Rome.valueOf(array[1].trim());
                    romeMap.put(array[0].trim(), rome);
                } else if (line.endsWith("Credits") && line.contains(" is ")) { // define product price
                    String[] array = line.split(" is ");
                    String[] array2 = array[0].trim().split(" ");
                    if (array2.length < 2) {
                        throw new IllegalArgumentException();
                    }
                    Rome[] romes = new Rome[array2.length - 1];
                    for (int i = 0; i < array2.length - 1; i++) {
                        romes[i] = romeMap.get(array2[i].trim());
                    }
                    String product = array2[array2.length - 1];
                    String priceStr = array[1].trim();
                    double totalPrice = Integer.parseInt(priceStr.substring(0, priceStr.indexOf(" ")));
                    int count = compute(romes);
                    double price = totalPrice / count;
                    priceMap.put(product, price);
                } else if (line.startsWith("how much is ") && line.endsWith(" ?")) { // ask count
                    String countStr = line.substring(12, line.length() - 2).trim();
                    String[] array = countStr.split(" ");
                    Rome[] romes = new Rome[array.length];
                    for (int i = 0; i < array.length; i++) {
                        romes[i] = romeMap.get(array[i].trim());
                    }
                    int count = compute(romes);
                    System.out.println(countStr + " is " + count);
                } else if (line.startsWith("how many Credits is ") && line.endsWith(" ?")) { // ask total price
                    String productStr = line.substring(20, line.length() - 2).trim();
                    String[] array = productStr.split(" ");
                    if (array.length < 2) {
                        throw new IllegalArgumentException();
                    }
                    Rome[] romes = new Rome[array.length - 1];
                    for (int i = 0; i < array.length - 1; i++) {
                        romes[i] = romeMap.get(array[i].trim());
                    }
                    int count = compute(romes);
                    double price = priceMap.get(array[array.length - 1].trim());
                    double total = count * price;
                    System.out.println(productStr + " is " + subZeroAndDot(total) + " Credits");
                } else {
                    throw new IllegalArgumentException();
                }
            } catch (Exception e) {
                System.out.println("I have no idea what you are talking about");
            }
        }
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

    private static String subZeroAndDot(double d) {
        String s = String.valueOf(d);
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");
            s = s.replaceAll("[.]$", "");
        }
        return s;
    }
}
