package me.wcy.code.merchant_guide_to_the_galaxy;

/**
 * Created by wcy on 2018/3/18.
 */
public enum Rome {
    M(1000, true),
    D(500, false),
    C(100, true, Rome.D, Rome.M),
    L(50, false),
    X(10, true, Rome.L, Rome.C),
    V(5, false),
    I(1, true, Rome.V, Rome.X);

    private int value;
    private boolean repeatable;
    private Rome[] nexts;

    Rome(int value, boolean repeatable, Rome... nexts) {
        this.value = value;
        this.repeatable = repeatable;
        this.nexts = nexts;
    }

    public int getValue() {
        return value;
    }

    public boolean isRepeatable() {
        return repeatable;
    }

    public boolean isNextValid(Rome next) {
        if (value > next.value) {
            return true;
        }
        if (this.equals(next) && repeatable) {
            return true;
        }
        for (Rome rome : nexts) {
            if (rome == next) {
                return true;
            }
        }
        return false;
    }
}
