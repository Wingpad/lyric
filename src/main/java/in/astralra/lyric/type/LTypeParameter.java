package in.astralra.lyric.type;

import in.astralra.lyric.core.LType;

import java.util.List;

/**
 * Created by jszaday on 8/9/2016.
 */
public class LTypeParameter implements LType {
    @Override
    public boolean isAssignableFrom(LType other) {
        return false;
    }

    @Override
    public List<LType> getTypeParameters() {
        return null;
    }

    @Override
    public String getIdentifier() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean isNativeType() {
        throw new RuntimeException("What do??? This is weird.");
    }
}
