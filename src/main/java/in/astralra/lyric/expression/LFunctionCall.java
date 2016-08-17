package in.astralra.lyric.expression;

import in.astralra.lyric.core.*;

import java.util.Collection;

/**
 * Created by jszaday on 8/5/2016.
 */
public class LFunctionCall extends LExpression {

    private LInvocable invocable;
    private LFunction resolved;
    private Collection<LExpression> arguments;

    public LFunctionCall(LInvocable invocable, Collection<LExpression> arguments) {
        this.invocable = invocable;
        this.arguments = arguments;
    }

    public LFunctionCall(LScope scope, String name, Collection<LExpression> arguments) {
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
            return (resolved = invocable.lift(arguments));
        } else {
            return resolved;
        }
    }

    public LInvocable getInvocable() {
        return invocable;
    }

    public Collection<LExpression> getArguments() {
        return arguments;
    }

    private static class LFunctionReference implements LInvocable {

        private LScope scope;
        private String name;

        LFunctionReference(LScope scope, String name) {
            this.scope = scope;
            this.name = name;
        }

        @Override
        public LFunction lift(Collection<LExpression> arguments) {
            return scope.findFunction(name, LFunction.map(arguments));
        }
    }
}
