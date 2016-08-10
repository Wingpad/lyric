package in.astralra.lyric;

import in.astralra.lyric.LType;
import in.astralra.lyric.LTypeParameter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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

    @Override
    public List<LType> getTypeParameters() {
        return Collections.emptyList();
    }

    public static Optional<LNativeType> lookup(final String name) {
        return Arrays.stream(values())
                .filter(value -> value.getName().equals(name))
                .findFirst();
    }
}
