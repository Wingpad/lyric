package in.astralra.lyric.core;

import com.sun.istack.internal.NotNull;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by jszaday on 8/9/2016.
 */
public enum LOperator {
    NONE("", null), ADD("+", "plus"), SUBTRACT("-", "minus"), DIVIDE("/", "div"),
    MODULUS("%", "mod"), MULTIPLY("*", "times"), LEFT_SHIFT("<<", "leftShift"), RIGHT_SHIFT(">>", "rightShift"),
    BITWISE_AND("&", "bitwiseAnd"), BITWISE_OR("|", "bitwiseOr"), BITWISE_XOR("^", "bitwiseXor"),
    GREATER_THAN(">", "compareTo"), LESS_THAN("<", "compareTo"),
    GREATER_THAN_EQUALS(">=", "compareTo"), LESS_THAN_EQUALS("<=", "compareTo");

    private final String operator;
    private final String function;

    LOperator(@NotNull String operator, String function) {
        this.operator = operator;
        this.function = function;
    }

    public static Optional<LOperator> lookup(String operator) {
        return Arrays.stream(values())
                .filter(other -> operator.equals(other.operator))
                .findFirst();
    }

    public String getOperator() {
        return operator;
    }

    public String getFunction() {
        return function;
    }
}
