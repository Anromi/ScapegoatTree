package main;

import java.util.*;

public class ScapegoatTree<T extends Comparable<T>> extends AbstractSet<T> implements SortedSet<T> {
    Queue<T> queueInOrder = new LinkedList<T>();
    private NodeSCG<T> Root;
    private int n;
    private int q;

    ScapegoatTree() {
        Root = null;
        n = 0;
    }

    public boolean IsEmpty() {
        return Root == null;
    }

    public void EmptyTree() {
        Root = null;
        n = 0;
    }

    public int size() {
        return n;
    }

    private int GetSize(NodeSCG<T> Rt) {
        if (Rt == null) {
            return 0;
        } else {
            int i = 1;
            i += GetSize(Rt.left);
            i += GetSize(Rt.right);
            return i;
        }
    }

    // Проверка элемента дерева
    public boolean FindKey(T Key) {
        if (Root == null)
            return false;
        return FindKey(Root, Key);
    }

    private boolean FindKey(NodeSCG<T> Rt, T Key) {
        int comparison = Key.compareTo(Rt.value);
        if (comparison == 0) {
            return true;
        } else if (comparison < 0) {
            if (Rt.left == null)
                return true;
            return FindKey(Rt.left, Key);
        } else {
            if (Rt.right == null)
                return true;
            return FindKey(Rt.right, Key);
        }
    }

    // Функция для получения значения log32 (n)
    private static final int Log32(int Val) {
        final double lg32 = 2.4663034623764317;
        return (int)Math.ceil(lg32 * Math.log(Val));
    }

    // Функция с глубиной, выполняет вставку и возращает глубуну вставленного узла
    public int InsertWithDepth(NodeSCG<T> N) {

        // Если дерево пусто
        NodeSCG<T> Rt = Root;
        if(Rt == null) {
            Root = N;
            n++;
            q++;
            return 0;
        }

        // Пока узел не вставлен или существует узел с таким же ключом.
        boolean inserted = false;
        int depth = 0;
        do {
            if (0 < (Rt.value).compareTo((N.value))) {
                if (Rt.left == null) {
                    Rt.left = N;
                    N.parent = Rt;
                    inserted = true;
                } else {
                    Rt = Rt.left;
                }
            } else if ((N.value).compareTo(Rt.value) > 0) {
                if (Rt.right == null) {
                    Rt.right = N;
                    N.parent = Rt;
                    inserted = true;
                } else {
                    Rt = Rt.right;
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
    public boolean add(T Key) {
        NodeSCG<T> n = new NodeSCG<T>(Key);
        int depth = InsertWithDepth(n);
        if (depth > Log32(q)) {         // Если дерево становится неуравновешенным

            // Превышена глубина, найдем козла отпущения
            NodeSCG<T> temp = n.parent;
            while (3 * GetSize(temp) <= 2 * GetSize(temp.parent)) {
                temp = temp.parent;
            }
            Rebuild(temp.parent);
        }
        return false;
    }

    // Функция для восстановления дерева и узла N
    public void Rebuild(NodeSCG<T> N) {
        int NodeSize = GetSize(N);
        NodeSCG<T> Parent = N.parent;
        List<NodeSCG<T>> list = new ArrayList<>();
        Pack(N, list, 0);
        if (Parent == null) {
            Root = BuildBalanced(list, 0, NodeSize);
            Root.parent = null;
        } else if (Parent.right == N) {
            Parent.right = BuildBalanced(list, 0, NodeSize);
            Parent.right.parent = Parent;
        } else {
            Parent.left = BuildBalanced(list, 0, NodeSize);
            Parent.left.parent = Parent;
        }
    }

    public int Pack(NodeSCG<T> N, List<NodeSCG<T>> list, int i) {
        if(N==null)
            return i;
        i=Pack(N.left,list,i);
        list.set(i++, N);
        return Pack(N.right,list,i);
    }

    // Функция для сбалансированных узлов
    public NodeSCG<T> BuildBalanced(List<NodeSCG<T>> list, int i, int NodeSize) {
        if (NodeSize == 0)
            return null;
        int m = NodeSize / 2;
        list.get(i + m).left = BuildBalanced(list, i, m);
        if (list.get(i + m).left != null) {
            list.get(i + m).left.parent = list.get(i + m);
        }
        // сохраняются в правом поддереве
        list.get(i + m).right = BuildBalanced(list, i + m + 1, NodeSize - m - 1);
        if (list.get(i + m).right != null) {
            list.get(i + m).right.parent = list.get(i + m);
        }
        return list.get(i + m);
    }

    // Обход порядка
    public void InOrder() { InOrder(Root); }

    private void InOrder(NodeSCG<T> Rt) {
        if (Rt != null) {
            InOrder(Rt.left);
            queueInOrder.add(Rt.value);
            InOrder(Rt.right);
        }
    }

    // Удаление
    public void Delete(T Key) { Root=Delete(Root,Key); }

    private NodeSCG<T> Delete(NodeSCG<T> Rt, T Key) {
        if (Rt == null)
            return Rt;
        if (0 < (Rt.value).compareTo(Key)) {
            Rt.left = Delete(Rt.left, Key);
        } else if (Key.compareTo(Rt.value) > 0) {
            Rt.right = Delete(Rt.right, Key);
        } else {
            if (Rt.left == null) {
                n--;
                return Rt.right;
            } else if (Rt.right == null) {
                n--;
                return Rt.left;
            }
            Rt.value = MinVal(Rt.right);
            Rt.right = Delete(Rt.right, Rt.value);
        }
        return Rt;
    }

    private T MinVal(NodeSCG<T> Rt) {
        T Minimum = Rt.value;
        while (Rt.left != null) {
            Minimum = Rt.left.value;
            Rt = Rt.left;
        }
        return Minimum;
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
        if (Root == null)
            throw new NoSuchElementException();
        NodeSCG<T> current = Root;
        while (current.left != null)
            current = current.left;
        return current.value;
    }

    @Override
    public T last() {
        if (Root == null)
            throw new NoSuchElementException();
        NodeSCG<T> current = Root;
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
            if (Root != null)
                wholeStack(Root);
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