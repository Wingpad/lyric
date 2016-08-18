package in.astralra.lyric.expression;

import in.astralra.lyric.core.*;
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
    public LFunction lift(Collection<LExpression> arguments) {
        LFunction function = getObject().lift(arguments);

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
    public String toString() {
        return resolve().toString();
    }

    @Override
    public String assign(LExpression value) {
        LDeclaration resolved = resolve();

        if (scope.isMember(resolved)) {
            return new LConnector(new LNativeValue(LNativeType.OBJECT, "self", true), target).assign(value);
        } else {
            return scope + " = " + target;
        }
    }
}
