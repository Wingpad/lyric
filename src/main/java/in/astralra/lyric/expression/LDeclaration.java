package in.astralra.lyric.expression;

import in.astralra.lyric.core.*;
import in.astralra.lyric.type.LClass;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by jszaday on 8/4/2016.
 */
public class LDeclaration implements LElement, LAssignable {
    private final LType type;
    private final String name;
    private final LObject object;
    private int modifiers = 0;

    public LDeclaration(LType type, String name) {
        this(type, name, null);
    }

    public LDeclaration(LType type, String name, LObject object) {
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
        return !type.getIdentifier().equals("NULL") && (object == null || type.isAssignableFrom(object
                .getType()));
    }

    public Optional<LObject> getValue() {
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

    @Override
    public boolean needsSemicolon() {
        return true;
    }

    @Override
    public List<LElement> getBackElements() {
        if (object instanceof LElement) {
            return ((LElement) object).getBackElements();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public String assign(LExpression value) {
        if (type.isNativeType()) {
            String expression;
            if (value instanceof LNativeValue && (expression = ((LNativeValue) value).getOriginal()) != null) {
                return "*" + name + " = " + expression;
            } else {
                return name + " = (" + type.getIdentifier() + ") " + value.toString();
            }
        } else {
            throw new RuntimeException("This might be an invalid assignment.");
        }
    }
}
