package in.astralra.lyric;

import java.util.Collection;
import java.util.List;

/**
 * Created by jszaday on 8/9/2016.
 */
public class LReference implements LExpression, LInvocable {

    private LScope scope;
    private String target;
    private LDeclaration resolved;

    public LReference(LScope scope, String target) {
        this.scope = scope;
        this.target = target;
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

    public boolean isAccessible() {
        return scope.isAccessible(resolve());
    }

    @Override
    public LType getType() {
        return resolve().getType();
    }

    @Override
    public LFunction invokeWith(Collection<LExpression> arguments) {
        LFunction function = scope.findFunction(target, LFunction.map(arguments));

        resolved = scope.findDeclarableForFunction(function);

        return function;
    }
}
