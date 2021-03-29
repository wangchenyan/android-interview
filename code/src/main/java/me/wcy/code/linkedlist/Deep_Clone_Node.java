package me.wcy.code.linkedlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wcy on 2021/3/1.
 */
class Deep_Clone_Node {
    class Node {
        int val;
        List<Node> nodes;
    }

    Map<Node, Node> map = new HashMap();

    public Node deepClone(Node node) {
        if (map.containsKey(node)) {
            return map.get(node);
        }
        Node n = new Node();
        n.val = node.val;
        map.put(node, n);
        n.nodes = new ArrayList<Node>(n.nodes.size());
        for (Node subNode : n.nodes) {
            n.nodes.add(deepClone(subNode));
        }
        return n;
    }
}
