package in.astralra.lyric.impl;

import in.astralra.lyric.LType;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by jszaday on 7/25/2016.
 */
public enum LNativeType implements LType {
    VOID("V", "void*"), INT("I", "int32_t*", "Int"),
    STRING("C", "char*", "String"), CLASS("LClass", "LClass*"),
    FUNCTION("LFunction", "LFunction"), SHORT("S", "int16_t*"),
    BYTE("B", "int8_t*"), BOOLEAN("Z", "bool*"), FLOAT("F", "float*"),
    DOUBLE("D", "double*"), LONG("J", "int64_t*"), OBJECT("LObject", "LObject*"),
    ARRAY("LArray", "LArray*");

    private final String name;
    private final String identifier;
    private final String wrapper;

    LNativeType(String name, String identifier) {
        this(name, identifier, null);
    }

    LNativeType(String name, String identifier, String wrapper) {
        this.name = name;
        this.identifier = identifier;
        this.wrapper = wrapper;
    }

    public String getWrapper() {
        return wrapper;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean isAssignableFrom(LType other) {
        return this == VOID || this == other;
    }
}
