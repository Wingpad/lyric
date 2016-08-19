package in.astralra.lyric.core;

import in.astralra.lyric.expression.LDeclaration;
import in.astralra.lyric.expression.LExpression;
import in.astralra.lyric.expression.LReference;
import in.astralra.lyric.expression.LReturn;
import in.astralra.lyric.type.LClass;
import in.astralra.lyric.type.LPrimitive;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by jszaday on 8/5/2016.
 */
public class LFunction extends LScope implements LBlock {

    private Map<String, LType> arguments;
    private LType returnType;
    private LBlock block;
    private List<Object> elements = new ArrayList<>();

    public LFunction() {
        this(new HashMap<>());
    }

    public LFunction(Map<String, LType> arguments) {
        this(null, arguments);
    }

    public LFunction(LType returnType, Map<String, LType> arguments) {
        this.arguments = arguments;
        this.returnType = returnType;

        arguments.forEach((name, type) -> declare(new LDeclaration(type, name), LModifier.FINAL));
    }

    public LType getReturnType() {
        if (returnType == null) {
            return (returnType = findReturnType());
        } else {
            return returnType;
        }
    }

    private LType findReturnType() {
        LType returnType = null;

        for (Object element: list()) {
            if (element instanceof LReturn) {
                LType other = ((LReturn) element).getType();
                if (returnType == null) {
                    returnType = other;
                } else if (returnType.isAssignableFrom(other)) {
                    continue;
                } else if (other.isAssignableFrom(returnType)) {
                    returnType = other;
                } else {
                    throw new RuntimeException(other + " is not assignable from " + returnType);
                }
            }
        }

        if (returnType == null) {
            // This should actually be the high-level LVoid
            returnType = LPrimitive.VOID;
        }

        return returnType;
    }

    public boolean argumentsMatch(LFunction other) {
        return argumentsMatch(other.getArguments());
    }

    public String getExternalName() {
        LDeclaration declaration = findDeclarableForFunction(this);
        String name = declaration == null ? "UNKNOWN" : declaration.getName();

        if (getParent() instanceof LClass) {
            name = ((LClass) getParent()).getName() + "_" + name;
        }

        return name;
    }

    public String getIdentifier() {
        return arguments.values().stream()
                .map(LType::getName)
                .collect(Collectors.joining(";"));
    }

    public boolean argumentsMatch(Collection<LType> theirs) {
        Collection<LType> ours = getArguments();
        Iterator<LType> ourIterator = ours.iterator(),
                theirIterator = theirs.iterator();
        // Verify the lengths are the same
        boolean flag = ours.size() == theirs.size();
        // And until one differs
        while (flag && ourIterator.hasNext()) {
            // Iterate through the list
            flag = ourIterator.next().isAssignableFrom(theirIterator.next());
        }
        // Returning the flag when finished
        return flag;

    }

    public Collection<LType> getArguments() {
        return arguments.values();
    }

    public void putArgument(String name, LType type) {
        arguments.put(name, type);
    }

    public LType getType() {
        return LNativeType.FUNCTION;
    }

    @Override
    public LFunction liftFunction(Collection<LExpression> arguments) {
        if (argumentsMatch(map(arguments))) {
            return this;
        } else {
            throw new IllegalArgumentException("Arguments don't match.");
        }
    }

    @Override
    public String lift(Collection<LExpression> arguments) {
        throw new UnsupportedOperationException("You've gone too deep! You can't lift a function!");
    }

    @Override
    public LScope getScope() {
        return this;
    }

    @Override
    public LBlock add(Object object) {
        elements.add(object);

        return this;
    }

    @Override
    public List<Object> list() {
        return elements;
    }

    public static List<LType> map(Collection<LExpression> expressions) {
        return expressions.stream().map(LExpression::getType).collect(Collectors.toList());
    }
}
