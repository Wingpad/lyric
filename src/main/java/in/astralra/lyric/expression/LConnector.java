package in.astralra.lyric.expression;

import in.astralra.lyric.core.*;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jszaday on 8/5/2016.
 */
// TODO: 8/9/2016 Might have to extend Assignment 
public class LConnector extends LExpression {

    private LReference identifier;
    private LConnectorType type;
    private List<LExpression> expressions;

    public LConnector(LObject target, String identifier) {
        this.identifier = new LReference(target, identifier);
        this.expressions = null;
        this.type = LConnectorType.DOT;
    }

    public LConnector(LObject target, LExpression... expression) {
        this.identifier = new LReference(target, "get");
        this.expressions = Arrays.asList(expression);
        this.type = LConnectorType.ARRAY;
    }

    @Override
    LObject getObject() {
        if (type == LConnectorType.DOT) {
            return identifier;
        } else {
            return identifier.lift(expressions);
        }
    }

    private enum LConnectorType {
        DOT, ARRAY
    }
}
