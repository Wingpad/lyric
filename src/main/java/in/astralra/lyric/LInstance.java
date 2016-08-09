package in.astralra.lyric;

import in.astralra.lyric.impl.LNativeType;

import java.util.Collection;

/**
 * Created by jszaday on 8/5/2016.
 */
public class LInstance extends LObject {
    @Override
    public LType getType() {
        return LNativeType.OBJECT;
    }

    @Override
    public LFunction invokeWith(Collection<LExpression> arguments) {
        return null;
    }
}
