package in.astralra.lyric;

import java.util.List;
import java.util.Optional;

/**
 * Created by jszaday on 8/9/2016.
 */
public class LTypeReference implements LType {

    private LScope scope;
    private String target;
    private LType resolved;

    public LTypeReference(LScope scope, String target) {
        this.scope = scope;
        this.target = target;
    }

    private LType resolve() {
        if (resolved == null) {
            List<LDeclaration> declarations = scope.findByName(target);
            Optional<LExpression> object;

            if (declarations.isEmpty()) {
                throw new RuntimeException("No definition found for " + target);
            } else if (declarations.get(0).getType() != LNativeType.CLASS || (object = declarations.get(0).getValue()).isPresent()) {
                throw new RuntimeException("Invalid type - " + target);
            } else {
                return (resolved = (LType) object.get());
            }
        } else {
            return resolved;
        }
    }

    @Override
    public boolean isAssignableFrom(LType other) {
        return resolve().isAssignableFrom(other);
    }

    @Override
    public List<LType> getTypeParameters() {
        return resolve().getTypeParameters();
    }

    @Override
    public String getIdentifier() {
        return resolve().getIdentifier();
    }

    @Override
    public String getName() {
        return resolve().getName();
    }
}
