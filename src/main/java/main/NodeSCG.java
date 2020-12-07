package main;

public class NodeSCG<T> {
    protected NodeSCG<T> right, left, parent;
    protected T value;

    NodeSCG(T val) {
        right=left=parent=null;
        value=val;
    }
}
