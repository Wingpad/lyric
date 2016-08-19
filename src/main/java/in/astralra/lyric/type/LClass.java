package in.astralra.lyric.type;

import in.astralra.lyric.core.*;
import in.astralra.lyric.expression.LDeclaration;
import in.astralra.lyric.expression.LExpression;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by jszaday on 8/5/2016.
 */
public class LClass extends LScope implements LType {

    // Add parent classes - check if we can use generics and what isAssignable should do

    private List<LType> typeParameters;
    private List<LDeclaration> constructors;
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
    public LFunction liftFunction(Collection<LExpression> arguments) {
        final List<LType> types = arguments.stream().map(LExpression::getType).collect(Collectors.toList());
        Optional<LFunction> functionOptional = constructors.stream()
                .map(LDeclaration::getValue)
                .map(Optional::get)
                .map(LFunction.class::cast)
                .filter(function -> function.argumentsMatch(types)).findFirst();

        if (functionOptional.isPresent()) {
            return functionOptional.get();
        } else {
            return null;
        }
    }

    @Override
    public String lift(Collection<LExpression> arguments) {
        LFunction lifted = liftFunction(arguments);

        if (lifted == null) {
            throw new RuntimeException("You tried lifting a constructor that is not present.");
        } else {
            return "LObject_lift(" + getName() + ", NULL, \"" + lifted.getIdentifier() + "\")";
        }
    }

    public void addConstructor(LDeclaration lFunction) {
        constructors.add(lFunction);
    }

    public List<LDeclaration> getConstructors() {
        return constructors;
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
        // TODO: 8/13/2016 Check into what this should return because it probably shouldn't be this!!
        return LNativeType.OBJECT.getIdentifier();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isNativeType() {
        return false;
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
