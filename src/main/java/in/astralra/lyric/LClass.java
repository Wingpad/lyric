package in.astralra.lyric;

import in.astralra.lyric.impl.LNativeType;

import java.util.Collection;
import java.util.List;

/**
 * Created by jszaday on 8/5/2016.
 */
public class LClass extends LObject {

    @Override
    public LType getType() {
        return LNativeType.CLASS;
    }

    @Override
    public LFunction invokeWith(Collection<LExpression> arguments) {
        return null;
    }
}
