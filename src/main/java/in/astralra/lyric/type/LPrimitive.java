package in.astralra.lyric.type;

import in.astralra.lyric.core.LType;

import java.util.List;

/**
 * Created by jszaday on 8/17/2016.
 */
public enum LPrimitive implements LType {
    NIL("Nil"), VOID("Void");

    private final String name;

    LPrimitive(String name) {
        this.name = name;
    }

    @Override
    public boolean isAssignableFrom(LType other) {
        return true;
    }

    @Override
    public List<LType> getTypeParameters() {
        return null;
    }

    @Override
    public String getIdentifier() {
        return "NULL";
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isNativeType() {
        return true;
    }
}
