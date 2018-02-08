package arndt.com.checkers.objects;

/**
 * Created by jarndt on 1/29/18.
 */

import java.util.Objects;

public class Pair<M, N> {
    M first;
    N second;

    public Pair(M first, N second) {
        this.first = first;
        this.second = second;
    }

    public M getFirst() {
        return first;
    }

    public void setFirst(M first) {
        this.first = first;
    }

    public N getSecond() {
        return second;
    }

    public void setSecond(N second) {
        this.second = second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(first, pair.first) &&
                Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "Pair{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}
