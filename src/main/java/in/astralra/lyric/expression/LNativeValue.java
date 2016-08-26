package in.astralra.lyric.expression;

import in.astralra.lyric.core.*;
import in.astralra.lyric.type.LNativeType;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by jszaday on 8/10/2016.
 */
public class LNativeValue extends LExpression implements LAssignable, LElement {
    private LNativeType type;
    private Object[] expressions;
    private Object[] originals;
    private List<LElement> backElements;

    public LNativeValue(LScope scope, LNativeType type, boolean isPointer, Object... lazyExpressions) {
        this.expressions = lazyExpressions;
        this.type = type;


        this.backElements = Arrays.stream(lazyExpressions)
                .filter(o -> o instanceof LElement)
                .map(LElement.class::cast)
                .map(LElement::getBackElements)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        if (!isPointer) {
            String name = scope.issueValue();
            String plainType = type.getIdentifier().replaceAll("\\*", "");
            LDeclaration declaration = new LDeclaration(type, name, new LNativeValue(null, type, true, "(" + type.getIdentifier() + ") malloc(sizeof(" + plainType + "))"));
            scope.declare(declaration);

            this.originals = lazyExpressions;
            this.backElements = new ArrayList<>(backElements);
            this.backElements.addAll(Arrays.asList(declaration, new LAssignment(declaration, LOperator.NONE, this)));
            this.expressions = new Object[] { name };
        }
    }

    @Override
    public LType getType() {
        return type;
    }

    public String getOriginal() {
        return originals == null ? null : Arrays.stream(originals)
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    public String getExpression() {
        return expressions == null ? null : Arrays.stream(expressions)
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    @Override
    LObject getObject() {
        throw new RuntimeException("A " + type + " does not have object properties.");
    }

    @Override
    public String assign(LExpression value) {
        return getExpression() + " = (" + type.getIdentifier() + ") " + value;
    }

    @Override
    public String toString() {
        return getExpression();
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
