package in.astralra.lyric;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by jszaday on 8/5/2016.
 */
public class LFunctionCall implements LExpression {

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

    private LFunction resolve() {
        if (resolved == null) {
            // TODO throw exception if still null?
            return (resolved = invocable.invokeWith(arguments));
        } else {
            return resolved;
        }
    }

    @Override
    public LType getType() {
        return resolve().getReturnType();
    }

    private static class LFunctionReference implements LInvocable {

        private LScope scope;
        private String name;

        LFunctionReference(LScope scope, String name) {
            this.scope = scope;
            this.name = name;
        }

        @Override
        public LFunction invokeWith(Collection<LExpression> arguments) {
            return scope.findFunction(name, LFunction.map(arguments));
        }
    }
}
