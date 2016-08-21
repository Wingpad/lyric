package in.astralra.lyric.expression;

import in.astralra.lyric.core.*;
import in.astralra.lyric.type.LNativeType;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by jszaday on 8/10/2016.
 */
public class LNativeValue extends LExpression implements LAssignable, LElement {
    private LNativeType type;
    private String expression;
    private String original;
    private List<LElement> backElements;

    public LNativeValue(LScope scope, LNativeType type, String expression, boolean isPointer) {
        this.expression = expression;
        this.type = type;

        if (isPointer) {
            this.backElements = Collections.emptyList();
        } else {
            String name = scope.issueValue();
            String plainType = type.getIdentifier().replaceAll("\\*", "");
            LDeclaration declaration = new LDeclaration(type, name, new LNativeValue(null, type, "(" + type.getIdentifier() + ") malloc(sizeof(" + plainType + "))", true));
            scope.declare(declaration);

            this.original = expression;
            this.backElements = Arrays.asList(declaration, new LAssignment(declaration, LOperator.NONE, this));
            this.expression = name;
        }
    }

    @Override
    public LType getType() {
        return type;
    }

    public String getOriginal() {
        return original;
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
        return backElements;
    }

    @Override
    public String lift(Collection<LExpression> arguments) {
        throw new RuntimeException("Can't lift from dis yet.");
    }
}
