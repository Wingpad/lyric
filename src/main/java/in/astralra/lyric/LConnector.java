package in.astralra.lyric;

import java.util.Collection;

/**
 * Created by jszaday on 8/5/2016.
 */
public class LConnector implements LExpression, LInvocable {

    private LObject target;
    private Object identifier;
    private LConnectorType type;

    public LConnector(LObject target, String identifier) {
        this.target = target;
        this.identifier = identifier;
        this.type = LConnectorType.DOT;
    }

    public LConnector(LObject target, LExpression expression) {
        this.target = target;
        this.identifier = expression;
        this.type = LConnectorType.ARRAY;
    }

    @Override
    public LType getType() {
        return null;
    }

    @Override
    public LFunction invokeWith(Collection<LExpression> arguments) {
        return null;
    }

    private enum LConnectorType {
        DOT, ARRAY
    }
}
