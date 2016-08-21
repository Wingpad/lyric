package in.astralra.lyric.type;

import in.astralra.lyric.core.LObject;
import in.astralra.lyric.core.LScope;
import in.astralra.lyric.core.LType;
import in.astralra.lyric.expression.LDeclaration;

import java.util.List;
import java.util.Optional;

/**
 * Created by jszaday on 8/10/2016.
 */
public class LTypeReference implements LType {

    private LScope scope;
    private String name;
    private LType resolved;

    public LTypeReference(LScope scope, String name) {
        this.scope = scope;
        this.name = name;
    }

    public LType resolve() {
        if (resolved == null) {
            List<LDeclaration> found = scope.findByName(name);

            if (found.isEmpty()) {
                throw new RuntimeException("No definition found for type " + name);
            } else if (found.size() > 1) {
                throw new RuntimeException("Multiple definitions found for type " + name);
            } else if (found.get(0).getType() == LNativeType.CLASS) {
                Optional<LObject> optional = found.get(0).getValue();

                if (optional.isPresent() && optional.get() instanceof LClass) {
                    return (resolved = (LType) optional.get());
                } else {
                    throw new RuntimeException(name + " is not a valid class definition.");
                }
            } else {
                throw new RuntimeException(name + " is not a class, but a(n) " + found.get(0).getType());
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

    @Override
    public boolean isNativeType() {
        return resolve().isNativeType();
    }
}
