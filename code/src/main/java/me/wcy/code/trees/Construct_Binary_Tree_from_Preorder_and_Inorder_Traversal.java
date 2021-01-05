package me.wcy.code.trees;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wcy on 2020/11/25.
 */
class Construct_Binary_Tree_from_Preorder_and_Inorder_Traversal {
    public TreeNode buildTree(int[] preorder, int[] inorder) {
        if (preorder.length == 0 || inorder.length == 0) {
            return null;
        }
        List<Integer> preorderList = new ArrayList<>(preorder.length);
        List<Integer> inorderList = new ArrayList<>(preorder.length);
        for (int i : preorder) preorderList.add(i);
        for (int i : inorder) inorderList.add(i);
        return getRoot(preorderList, inorderList);
    }

    public TreeNode getRoot(List<Integer> preorder, List<Integer> inorder) {
        TreeNode root = new TreeNode(preorder.get(0));
        int leftSize = inorder.indexOf(preorder.get(0));
        int rightSize = inorder.size() - leftSize - 1;
        if (leftSize > 0) {
            root.left = getRoot(preorder.subList(1, leftSize + 1), inorder.subList(0, leftSize));
        }
        if (rightSize > 0) {
            root.right = getRoot(preorder.subList(leftSize + 1, preorder.size()), inorder.subList(leftSize + 1, inorder.size()));
        }
        return root;
    }
}
