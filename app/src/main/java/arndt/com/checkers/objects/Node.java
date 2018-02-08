package arndt.com.checkers.objects;

/**
 * Created by jarndt on 1/29/18.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Node<T, V> {
    Node parent;
    List<Node> children = new ArrayList<>();
    T data;
    V value;
    HashMap<String, Object> values = new HashMap<>();

    public Node(T data, V value) {
        this.data = data;
        this.value = value;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public HashMap<String, Object> getValues() {
        return values;
    }

    public void setValues(HashMap<String, Object> values) {
        this.values = values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node<?, ?> node = (Node<?, ?>) o;
        return Objects.equals(parent, node.parent) &&
                Objects.equals(children, node.children) &&
                Objects.equals(data, node.data) &&
                Objects.equals(value, node.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parent, children, data, value);
    }
}

