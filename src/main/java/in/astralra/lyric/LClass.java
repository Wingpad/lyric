package in.astralra.lyric;

import in.astralra.lyric.impl.LNativeType;

import java.util.List;

/**
 * Created by jszaday on 8/5/2016.
 */
public class LClass extends LScope implements LObject {

    @Override
    public LType getType() {
        return LNativeType.CLASS;
    }

    @Override
    public LFunction invokeWith(List<LExpression> arguments) {
        return null;
    }
}
