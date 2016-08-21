package in.astralra.lyric.core;

import in.astralra.lyric.expression.LExpression;
import in.astralra.lyric.type.LClass;
import in.astralra.lyric.type.LNativeType;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jszaday on 8/5/2016.
 */
public class LInstance extends LScope implements LType {

    private List<LType> types;
    private LClass lClass;

    public LInstance(LClass lClass, List<LType> types) {
        super(lClass);
        this.types = types;
        this.lClass = lClass;
    }

    public void setTypes(List<LType> types) {
        if (lClass.validTypes(types)) {
            this.types = types;
        } else {
            throw new IllegalArgumentException("Types do not implement the required types of the parent class.");
        }
    }

    @Override
    public LType getType() {
        return lClass;
    }

    @Override
    public LFunction liftFunction(Collection<LExpression> arguments) {
        return this.lClass.liftFunction(arguments);
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

    @Override
    public boolean isAssignableFrom(LType other) {
        return lClass.validTypes(types) && (other == this || other == lClass || other == LNativeType.OBJECT);
    }

    @Override
    public List<LType> getTypeParameters() {
        return types;
    }

    @Override
    public String getIdentifier() {
        return LNativeType.OBJECT.getIdentifier();
    }

    @Override
    public String getName() {
        return lClass + "<" + this.types.stream().map(String::valueOf).collect(Collectors.joining(", ")) + ">";
    }

    @Override
    public boolean isNativeType() {
        return false;
    }
}
