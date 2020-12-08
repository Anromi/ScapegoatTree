package main;

import java.util.*;

public class ScapegoatTree<T extends Comparable<T>> extends AbstractSet<T> implements SortedSet<T> {
    Queue<T> queueInOrder = new LinkedList<>();
    private NodeSCG<T> root;
    private int n;
    private int q;

    ScapegoatTree() {
        root = null;
        n = 0;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public void emptyTree() {
        root = null;
        n = 0;
    }

    public int size() {
        return n;
    }

    private int setSize(NodeSCG<T> rt) {
        if (rt == null) {
            return 0;
        } else {
            int i = 1;
            i += setSize(rt.left);
            i += setSize(rt.right);
            return i;
        }
    }

    // Проверка элемента дерева
    public boolean findKey(T Key) {
        if (root == null)
            return false;
        return findKey(root, Key);
    }

    private boolean findKey(NodeSCG<T> rt, T key) {
        int comparison = key.compareTo(rt.value);
        if (comparison == 0) {
            return true;
        } else if (comparison < 0) {
            if (rt.left == null)
                return true;
            return findKey(rt.left, key);
        } else {
            if (rt.right == null)
                return true;
            return findKey(rt.right, key);
        }
    }

    // Функция для получения значения log32 (n)
    private static final int log32(int val) {
        final double lg32 = 2.4663034623764317;
        return (int)Math.ceil(lg32 * Math.log(val));
    }

    // Функция с глубиной, выполняет вставку и возращает глубуну вставленного узла
    public int insertWithDepth(NodeSCG<T> tNodeSCG) {

        // Если дерево пусто
        NodeSCG<T> rt = root;
        if(rt == null) {
            root = tNodeSCG;
            n++;
            q++;
            return 0;
        }

        // Пока узел не вставлен или существует узел с таким же ключом.
        boolean inserted = false;
        int depth = 0;
        do {
            if (0 < (rt.value).compareTo((tNodeSCG.value))) {
                if (rt.left == null) {
                    rt.left = tNodeSCG;
                    tNodeSCG.parent = rt;
                    inserted = true;
                } else {
                    rt = rt.left;
                }
            } else if ((tNodeSCG.value).compareTo(rt.value) > 0) {
                if (rt.right == null) {
                    rt.right = tNodeSCG;
                    tNodeSCG.parent = rt;
                    inserted = true;
                } else {
                    rt = rt.right;
                }
            } else {
                return -1;
            }
            depth++;
        } while (!inserted);
        n++;
        q++;
        return depth;
    }

    // Чтобы вставить новый элемент в дерево
    public boolean add(T key) {
        NodeSCG<T> n = new NodeSCG<>(key);
        int depth = insertWithDepth(n);
        if (depth > log32(q)) {         // Если дерево становится неуравновешенным

            // Превышена глубина, найдем козла отпущения
            NodeSCG<T> temp = n.parent;
            while (3 * setSize(temp) <= 2 * setSize(temp.parent)) {
                temp = temp.parent;
            }
            rebuild(temp.parent);
        }
        return false;
    }

    // Функция для восстановления дерева и узла N
    public void rebuild(NodeSCG<T> tNodeSCG) {
        int nodeSize = setSize(tNodeSCG);
        NodeSCG<T> parent = tNodeSCG.parent;
        List<NodeSCG<T>> list = new ArrayList<>();
        pack(tNodeSCG, list, 0);
        if (parent == null) {
            root = buildBalanced(list, 0, nodeSize);
            root.parent = null;
        } else if (parent.right == tNodeSCG) {
            parent.right = buildBalanced(list, 0, nodeSize);
            parent.right.parent = parent;
        } else {
            parent.left = buildBalanced(list, 0, nodeSize);
            parent.left.parent = parent;
        }
    }

    public int pack(NodeSCG<T> tNodeSCG, List<NodeSCG<T>> list, int i) {
        if(tNodeSCG==null)
            return i;
        i=pack(tNodeSCG.left,list,i);
        list.set(i++, tNodeSCG);
        return pack(tNodeSCG.right,list,i);
    }

    // Функция для сбалансированных узлов
    public NodeSCG<T> buildBalanced(List<NodeSCG<T>> list, int i, int nodeSize) {
        if (nodeSize == 0)
            return null;
        int m = nodeSize / 2;
        list.get(i + m).left = buildBalanced(list, i, m);
        if (list.get(i + m).left != null) {
            list.get(i + m).left.parent = list.get(i + m);
        }
        // сохраняются в правом поддереве
        list.get(i + m).right = buildBalanced(list, i + m + 1, nodeSize - m - 1);
        if (list.get(i + m).right != null) {
            list.get(i + m).right.parent = list.get(i + m);
        }
        return list.get(i + m);
    }

    // Обход порядка
    public void inOrder() { inOrder(root); }

    private void inOrder(NodeSCG<T> rt) {
        if (rt != null) {
            inOrder(rt.left);
            queueInOrder.add(rt.value);
            inOrder(rt.right);
        }
    }

    // Удаление
    public void delete(T key) { root=delete(root,key); }

    private NodeSCG<T> delete(NodeSCG<T> rt, T key) {
        if (rt == null)
            return rt;
        if (0 < (rt.value).compareTo(key)) {
            rt.left = delete(rt.left, key);
        } else if (key.compareTo(rt.value) > 0) {
            rt.right = delete(rt.right, key);
        } else {
            if (rt.left == null) {
                n--;
                return rt.right;
            } else if (rt.right == null) {
                n--;
                return rt.left;
            }
            rt.value = minVal(rt.right);
            rt.right = delete(rt.right, rt.value);
        }
        return rt;
    }

    private T minVal(NodeSCG<T> rt) {
        T minimum = rt.value;
        while (rt.left != null) {
            minimum = rt.left.value;
            rt = rt.left;
        }
        return minimum;
    }

    @Override
    public Comparator<? super T> comparator() { return null; }

    public SortedSet<T> subSet(T fromElement, T toElement, boolean higher) {
        TreeSet<T> treeSet = new TreeSet<>();
        Iterator<T> iterator = new SCGTreeIterator();
        while (iterator.hasNext()) {
            boolean b;
            T i = iterator.next();
            if (higher) {
                b = i.compareTo(toElement) <= 0;
            } else {
                b = i.compareTo(toElement) < 0;
            }
            if (i.compareTo(fromElement) >= 0 && b)
                treeSet.add(i);
        }
        return treeSet;
    }

    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        return subSet(fromElement, toElement, false);
    }

    @Override
    public SortedSet<T> headSet(T toElement) { return this.subSet(first(), toElement, true); }

    @Override
    public SortedSet<T> tailSet(T fromElement) { return this.subSet(fromElement, last(), true); }

    @Override
    public T first() {
        if (root == null)
            throw new NoSuchElementException();
        NodeSCG<T> current = root;
        while (current.left != null)
            current = current.left;
        return current.value;
    }

    @Override
    public T last() {
        if (root == null)
            throw new NoSuchElementException();
        NodeSCG<T> current = root;
        while (current.right != null)
            current = current.right;
        return current.value;
    }

    @Override
    public Iterator<T> iterator() { return new SCGTreeIterator(); }

    public class SCGTreeIterator implements Iterator<T>{
        ArrayDeque<NodeSCG<T>> deque = new ArrayDeque<>();
        T current = null;

        private SCGTreeIterator() {
            if (root != null)
                wholeStack(root);
        }

        public void wholeStack(NodeSCG<T> current) {
            if (current.left != null)
                wholeStack(current.left);
            deque.push(current);
            if (current.right != null)
                wholeStack(current.right);
        }

        @Override
        public boolean hasNext() { return !deque.isEmpty(); }

        @Override
        public T next() {
            current = deque.removeLast().value;
            return current;
        }

        @Override
        public void remove() {
            if (current != null) {
                ScapegoatTree.this.remove(current);
                current = null;
            } else {
                throw new IllegalStateException();
            }
        }
    }
}