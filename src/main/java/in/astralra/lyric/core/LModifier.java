package in.astralra.lyric.core;

import java.util.Arrays;

/**
 * Created by jszaday on 8/8/2016.
 */
public enum LModifier {
    PUBLIC, PRIVATE, PROTECTED, STATIC, FINAL;

    public int getFlag() {
        return (int) Math.pow(2, ordinal());
    }

    public boolean isPresent(int flags) {
        return (flags & getFlag()) != 0;
    }

    public static int reduceToInt(int previousFlags, LModifier... modifiers) {
        return Arrays.stream(modifiers)
                .map(LModifier::getFlag)
                .reduce(previousFlags, (i, j) -> i | j);
    }

    public static int reduceToInt(LModifier... modifiers) {
        return reduceToInt(0, modifiers);
    }
}
