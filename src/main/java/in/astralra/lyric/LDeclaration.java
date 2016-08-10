package in.astralra.lyric;

import java.util.List;
import java.util.Optional;

/**
 * Created by jszaday on 8/4/2016.
 */
public class LDeclaration {
    private final LType type;
    private final String name;
    private final LExpression object;
    private int modifiers = 0;

    public LDeclaration(LType type, String name) {
        this(type, name, null);
    }

    public LDeclaration(LType type, String name, LExpression object) {
        this.type = type;
        this.name = name;
        this.object = object;

        if (object instanceof LInstance) {
            ((LInstance) object).setTypes(type.getTypeParameters());
        }
    }

    public LType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public boolean isValid() {
        return object == null || type.isAssignableFrom(object.getType());
    }

    public Optional<LExpression> getValue() {
        return Optional.ofNullable(object);
    }

    public int getModifiers() {
        return modifiers;
    }

    public List<LType> getImplementedTypes() {
        return null;
    }

    public void setModifiers(int modifiers) {
        this.modifiers |= modifiers;
    }
}
