package in.astralra.lyric.expression;

import in.astralra.lyric.core.*;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by jszaday on 8/5/2016.
 */
public class LFunctionCall extends LExpression {

    private LInvokable invocable;
    private LFunction resolved;
    private Collection<LExpression> arguments;

    public LFunctionCall(LInvokable invocable, Collection<LExpression> arguments) {
        this.invocable = invocable;
        this.arguments = arguments;
    }

    public LFunctionCall(LObject scope, String name, Collection<LExpression> arguments) {
        this(new LFunctionReference(scope, name), arguments);
    }

    @Override
    public LType getType() {
        return ((LFunction) getObject()).getReturnType();
    }

    @Override
    public LObject getObject() {
        if (resolved == null) {
            // TODO throw exception if still null?
            return (resolved = invocable.liftFunction(arguments));
        } else {
            return resolved;
        }
    }

    @Override
    public String lift(Collection<LExpression> arguments) {
        throw new RuntimeException("Can't lift from dis.");
    }

    public Collection<LExpression> getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        String result = "LFunction_invoke(" + invocable.lift(arguments) + ", " +
                arguments.stream().map(String::valueOf).collect(Collectors.joining(", "));

        if (result.endsWith(", ")) {
            result += "NULL)";
        } else {
            result += ", NULL)";
        }

        return result;
    }

    private static class LFunctionReference implements LInvokable {

        private LObject scope;
        private String name;

        LFunctionReference(LObject scope, String name) {
            this.scope = scope;
            this.name = name;
        }

        @Override
        public LFunction liftFunction(Collection<LExpression> arguments) {
            return scope.findFunction(name, LFunction.map(arguments));
        }

        @Override
        public String lift(Collection<LExpression> arguments) {
            return "LObject_lift(" + scope + ", \"" + name + "\", \"" + liftFunction(arguments).getIdentifier() + "\")";
        }
    }
}
