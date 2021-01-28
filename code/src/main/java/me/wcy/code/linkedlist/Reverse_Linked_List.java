package me.wcy.code.linkedlist;

/**
 * https://leetcode.com/explore/interview/card/top-interview-questions-medium/107/linked-list/784/
 * Created by wcy on 2021/1/20.
 */
class Reverse_Linked_List {
    public ListNode reverseList(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        ListNode curr = head;
        ListNode prev = null;
        while (curr != null) {
            ListNode next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }
        return prev;
    }
}
