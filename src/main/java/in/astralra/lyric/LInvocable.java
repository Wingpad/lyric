package in.astralra.lyric;

import java.util.Collection;

/**
 * Created by jszaday on 8/5/2016.
 */
public interface LInvocable {
    LFunction invokeWith(Collection<LExpression> arguments);
}
