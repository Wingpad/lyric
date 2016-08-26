package in.astralra.lyric.expression;

import in.astralra.lyric.core.*;
import in.astralra.lyric.type.LNativeType;
import in.astralra.lyric.util.LUnboxer;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by jszaday on 8/25/2016.
 */
public class LArithmeticExpr extends LExpression {

    private LExpression left, right;
    private LOperator operator;
    private LScope scope;
    private LExpression resolved;

    public LArithmeticExpr(LScope scope, LExpression left, LOperator operator, LExpression right) {
        this.left = left;
        this.right = right;
        this.operator = operator;
        this.scope = scope;
    }

    private LExpression resolve() {
        if (resolved == null) {
            if (left.getType().isNativeType()) {
                resolved = new LNativeValue(scope, (LNativeType) left.getType(), false, left, operator.getOperator(), LUnboxer.unbox(right, (LNativeType) left.getType()));
            } else {
                resolved = new LFunctionCall(new LConnector(left, operator.getFunction()), Collections.singletonList(right));
            }
        }
        return resolved;
    }

    @Override
    public String lift(Collection<LExpression> arguments) {
        return getObject().lift(arguments);
    }

    @Override
    public LType getType() {
        return resolve().getType();
    }

    @Override
    LObject getObject() {
        return resolve().getObject();
    }

    @Override
    public String toString() {
        return resolve().toString();
    }

    @Override
    public List<LElement> getBackElements() {
        return resolve().getBackElements();
    }
}
