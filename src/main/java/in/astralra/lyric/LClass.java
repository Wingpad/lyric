package in.astralra.lyric;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by jszaday on 8/5/2016.
 */
public class LClass extends LObject implements LType {

    // Add parent classes - check if we can use generics and what isAssignable should do

    private List<LType> typeParameters;
    private List<LFunction> constructors;
    private String name;

    public LClass(String name) {
        this(name, Collections.emptyList());
    }

    public LClass(String name, List<LType> typeParameters) {
        this.name = name;
        this.typeParameters = typeParameters;
        this.constructors = new ArrayList<>();
    }

    @Override
    public LType getType() {
        return LNativeType.CLASS;
    }

    @Override
    public LFunction invokeWith(Collection<LExpression> arguments) {
        final List<LType> types = arguments.stream().map(LExpression::getType).collect(Collectors.toList());
        Optional<LFunction> functionOptional = constructors.stream().filter(function -> function.argumentsMatch(types)).findFirst();

        if (functionOptional.isPresent()) {
            return functionOptional.get();
        } else {
            return null;
        }
    }

    public void addConstructor(LFunction lFunction) {
        constructors.add(lFunction);
    }

    @Override
    public List<LType> getTypeParameters() {
        return typeParameters;
    }

    @Override
    public boolean isAssignableFrom(LType other) {
        return other == this || (other instanceof LClass && ((LScope) other).getParent() == this);
    }

    @Override
    public String getIdentifier() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    public boolean validTypes(List<LType> types) {
        if (types.size() == typeParameters.size()) {
            final Iterator<LType> parameterIterator = typeParameters.iterator();

            for (LType type : types) {
                if (!parameterIterator.next().isAssignableFrom(type)) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }
}
