package in.astralra.lyric.core;

import in.astralra.lyric.expression.LExpression;

import java.util.Collection;

/**
 * Created by jszaday on 8/5/2016.
 */
public interface LInvokable {
    String lift(Collection<LExpression> arguments);
    LFunction liftFunction(Collection<LExpression> arguments);
}
