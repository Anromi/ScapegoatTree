package main;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.SortedSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SCTreeTest {

    ScapegoatTree<Integer> SGT = new ScapegoatTree<>();

    @Test
    public void emptyTree() {
        assertTrue(SGT.isEmpty());
        assertFalse(SGT.findKey(0));

        SGT.emptyTree();
    }

    @Test
    public void add() {
        SGT.add(5);
        assertTrue(SGT.findKey(5));
        assertEquals(1, SGT.size());

        SGT.add(6);
        SGT.add(7);
        assertEquals(3, SGT.size());

        SGT.emptyTree();
    }

    @Test
    public void delete() {
        SGT.add(5);
        SGT.add(6);
        assertEquals(2, SGT.size());

        SGT.delete(5);
        SGT.add(7);
        SGT.add(8);
        assertEquals(3, SGT.size());

        // попытаем удалить элемент, которо нет
        SGT.delete(55555);
        assertEquals(3, SGT.size());

        SGT.emptyTree();
    }

    @Test
    public  void testFirstAndLast() {
        SGT.add(5);
        SGT.add(6);
        SGT.add(8);
        SGT.add(7);
        SGT.add(4);
        SGT.add(3);
        SGT.add(14);
        SGT.add(20);
        assertEquals(3, SGT.first());
        assertEquals(20, SGT.last());

        SGT.emptyTree();
    }

    @Test
    public void testSubSet() {
        SGT.add(5);
        SGT.add(6);
        SGT.add(8);
        SGT.add(7);
        SGT.add(4);
        SGT.add(3);
        SGT.add(14);
        SGT.add(20);

        HashSet<Integer> set = new HashSet<>();
        set.add(5);
        set.add(6);
        set.add(7);
        set.add(8);
        set.add(14);

        SortedSet<Integer> subSet = SGT.subSet(5, 20);
        assertEquals(set, subSet);

        SGT.emptyTree();
    }

    @Test
    public void testInOrder() {
        SGT.add(5);
        SGT.add(6);
        SGT.add(65);
        SGT.add(7);
        SGT.add(1);
        SGT.add(4);
        SGT.add(13);
        SGT.add(14);
        SGT.add(20);

        Queue<Integer> queueInOrderTest = new LinkedList<>();
        queueInOrderTest.add(1);
        queueInOrderTest.add(4);
        queueInOrderTest.add(5);
        queueInOrderTest.add(6);
        queueInOrderTest.add(7);
        queueInOrderTest.add(13);
        queueInOrderTest.add(14);
        queueInOrderTest.add(20);
        queueInOrderTest.add(65);

        SGT.inOrder();
        assertEquals(queueInOrderTest, SGT.queueInOrder);

        SGT.emptyTree();
    }
}
