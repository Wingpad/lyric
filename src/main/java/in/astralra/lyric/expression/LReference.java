package in.astralra.lyric.expression;

import in.astralra.lyric.core.*;
import in.astralra.lyric.type.LNativeType;
import in.astralra.lyric.type.LTypeReference;

import java.util.Collection;
import java.util.List;

/**
 * Created by jszaday on 8/9/2016.
 */
public class LReference extends LExpression implements LAssignable {

    private LObject scope;
    private String target;
    private LDeclaration resolved;

    private static LNativeValue SELF = new LNativeValue(null, LNativeType.OBJECT, "self", true);

    public LReference(LObject scope, String target) {
        this.scope = scope;
        this.target = target;
    }

    public LObject getScope() {
        return scope;
    }

    public String getTarget() {
        return target;
    }

    public LDeclaration resolve() {
        if (resolved == null) {
            List<LDeclaration> declarations = scope.findByName(target);
            if (declarations.isEmpty()) {
                throw new RuntimeException("Could not find " + target);
            } else if (declarations.size() > 1) {
                throw new RuntimeException("Ambiguous reference, " + target);
            } else {
                return (resolved = declarations.get(0));
            }
        } else {
            return resolved;
        }
    }

    @Override
    public LType getType() {
        return resolve().getType();
    }

    @Override
    LObject getObject() {
        LDeclaration resolved = resolve();

        if (resolved.getType() == LNativeType.CLASS || resolved.getType() == LNativeType.FUNCTION) {
            return resolved.getValue().get();
        } else if (resolved.getType().isNativeType()) {
            throw new RuntimeException(target + " does not have object properties!");
        } else if (resolved.getType() instanceof LTypeReference) {
            return (LObject) ((LTypeReference) resolved.getType()).resolve();
        } else {
            return (LObject) resolved.getType();
        }
    }

    @Override
    public LFunction liftFunction(Collection<LExpression> arguments) {
        LFunction function = getObject().liftFunction(arguments);

        if (function == null) {
            function = scope.findFunction(target, LFunction.map(arguments));

            resolved = scope.findDeclarableForFunction(function);
        }

        if (function == null) {
            throw new RuntimeException("Could not resolve the target " + target);
        }

        return function;
    }

    @Override
    public String lift(Collection<LExpression> arguments) {
        LObject obj = getObject();

        if (obj instanceof LFunction) {
            return ((LFunction) obj).getReferenceName();
        } else {
            return obj.lift(arguments);
        }
    }

    @Override
    public String toString() {
        LDeclaration resolved = resolve();

        if (!(scope instanceof LReference) && scope.isMember(resolved)) {
            return "LObject_get(self, \"" + target + "\")";
        } else {
            return resolve().getName();
        }
    }

    @Override
    public String assign(LExpression value) {
        LDeclaration resolved = resolve();

        if (scope.isMember(resolved)) {
            return new LConnector(SELF, target).assign(value);
        } else if (getType().isNativeType()) {
            if (value.getType().isNativeType()) {
                if (getType() == LNativeType.VOID) {
                    return target + " = (" + getType().getIdentifier() + ")" + value;
                } else if (getType() == value.getType()) {
                    return target + " = " + value;
                } else {
                    throw new RuntimeException("Cannot assign " + value + " to " + target + "!");
                }
            } else {
                List<LDeclaration> found = value.findByName("value");
                if (found.isEmpty() || found.get(0).getType() != getType()) {
                    throw new RuntimeException("Cannot assign " + value + " to " + target + "!");
                } else {
                    return target + " = (" + getType().getIdentifier() + ")" + new LConnector(value, "value");
                }
            }
        } else {
            return target + " = " + value;
        }
    }

    @Override
    public LObject getSelf() {
        return scope.getSelf();
    }
}
