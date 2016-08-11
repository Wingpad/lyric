package in.astralra.lyric.expression;

import in.astralra.lyric.core.LNativeType;
import in.astralra.lyric.core.LObject;
import in.astralra.lyric.core.LType;

/**
 * Created by jszaday on 8/10/2016.
 */
public class LNativeValue extends LExpression {
    private LNativeType type;
    private String expression;
    private boolean isPointer;

    public LNativeValue(LNativeType type, String expression, boolean isPointer) {
        this.type = type;
        this.expression = expression;
        this.isPointer = isPointer;
    }

    @Override
    public LType getType() {
        return type;
    }

    @Override
    LObject getObject() {
        throw new RuntimeException(expression + " does not have object properties.");
    }
}
