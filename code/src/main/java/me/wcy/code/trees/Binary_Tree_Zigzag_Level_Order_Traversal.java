package me.wcy.code.trees;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

/**
 * Created by wcy on 2020/11/25.
 */
class Binary_Tree_Zigzag_Level_Order_Traversal {

    public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;
        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);
        boolean reverse = false;
        while (!stack.isEmpty()) {
            int size = stack.size();
            List<Integer> list = new ArrayList<>(size);
            Queue<TreeNode> queue = new ArrayDeque<>(size);
            while (size > 0) {
                TreeNode node = stack.pop();
                list.add(node.val);
                if (reverse) {
                    if (node.right != null) queue.add(node.right);
                    if (node.left != null) queue.add(node.left);
                } else {
                    if (node.left != null) queue.add(node.left);
                    if (node.right != null) queue.add(node.right);
                }
                size--;
            }
            stack.addAll(queue);
            reverse = !reverse;
            result.add(list);
        }
        return result;
    }
}
