package in.astralra.lyric.impl;

import in.astralra.lyric.LDeclarable;
import in.astralra.lyric.LObject;
import in.astralra.lyric.LType;

import java.util.Optional;

/**
 * Created by jszaday on 8/4/2016.
 */
public class LSimpleDeclaration implements LDeclarable {

    private final LType type;
    private final String name;
    private final LObject object;

    public LSimpleDeclaration(LType type, String name) {
        this(type, name, null);
    }

    public LSimpleDeclaration(LType type, String name, LObject object) {
        this.type = type;
        this.name = name;
        this.object = object;
    }

    public LType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean isValid() {
        return object == null || type.isAssignableFrom(object.getType());
    }

    @Override
    public Optional<LObject> getValue() {
        return Optional.ofNullable(object);
    }
}
