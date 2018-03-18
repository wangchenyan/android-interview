package me.wcy.code;

/**
 * 请20分钟内完成以下题目（以纸笔或者文本形式完成，请勿使用IDE等开发工具）
 * 1、请写出实际代码（可以使用任意熟悉的编码语言）
 * 2、需要考虑时间、空间复杂度
 * <p>
 * 题目1：
 * 百词斩的程序员喜欢研究各种算法。百词斩的Jerry自己做了一个简单的IP变换算法，将一个IP地址转化为字符串。比如IP地址1.1.1.129：
 * 第一步：转化为二进制：00000001000000010000000110000001
 * 第二步：替换（0用a表示，1用b表示）：aaaaaaabaaaaaaabaaaaaaabbaaaaaab
 * 第三步：将连续相同的字符合并：7a1b7a1b7a2b6a1b
 * 第四步：减少长度，将数字1去掉：7ab7ab7a2b6ab。
 * 现在给出一个Jerry的算法生成的字符串，请你来还原最初的IP地址。
 * <p>
 * 输入：
 * 1、输入为一行，含有a、b和数字的字符串
 * <p>
 * 输出：
 * 1、输出为一行，即最初的IP地址
 * <p>
 * Sample Input：
 * 7ab7ab7a2b6ab
 * <p>
 * Sample Output：
 * 1.1.1.129
 */
public class BCZ_IPTransform {

    public static void main(String[] args) {
        String ip = "1.1.1.129";
        transform(ip);
    }

    public static void transform(String ip) {
        System.out.println("Input:");
        System.out.println(ip);

        String[] ips = ip.split("\\.");
        String[] hexs = new String[ips.length];
        for (int i = 0; i < ips.length; i++) {
            // 转为整形
            int num = Integer.parseInt(ips[i]);
            // 转为二进制
            StringBuilder hex = new StringBuilder();
            while (num > 0) {
                hex.append(String.valueOf(num % 2));
                num = num / 2;
            }
            // 补0
            while (hex.length() < 8) {
                hex.insert(0, "0");
            }
            hexs[i] = hex.toString();
        }
        System.out.println("First Step:");
        String print = "";
        for (String hex : hexs) {
            print += hex;
        }
        System.out.println(print);
    }
}
