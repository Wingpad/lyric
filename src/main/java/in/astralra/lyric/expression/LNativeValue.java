package in.astralra.lyric.expression;

import in.astralra.lyric.core.*;

import java.util.Collections;
import java.util.List;

/**
 * Created by jszaday on 8/10/2016.
 */
public class LNativeValue extends LExpression implements LAssignable, LElement {
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

    @Override
    public String assign(LExpression value) {
        return expression + " = (" + type.getIdentifier() + ") " + value;
    }

    @Override
    public String toString() {
        return expression;
    }

    @Override
    public boolean needsSemicolon() {
        return true;
    }

    @Override
    public List<LElement> getBackElements() {
        if (isPointer) {
            return Collections.emptyList();
        } else {
            throw new UnsupportedOperationException("Can't generate back elements for a native value yet.");
        }
    }
}
