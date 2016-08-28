package in.astralra.lyric.expression;

import in.astralra.lyric.core.LAssignable;
import in.astralra.lyric.core.LElement;
import in.astralra.lyric.core.LOperator;

import java.util.Collections;
import java.util.List;

/**
 * Created by jszaday on 8/9/2016.
 */
public class LAssignment implements LElement {
    private LAssignable target;
    private LOperator operator;
    private LExpression value;

    public LAssignment(LAssignable target, LOperator operator, LExpression value) {
        this.target = target;
        this.operator = operator == null ? LOperator.NONE : operator;
        this.value = value;
    }

    // TODO: 8/9/2016 Might have to extend Declaration

    @Override
    public String toString() {
        return target.assign(value);
    }

    @Override
    public boolean needsSemicolon() {
        return true;
    }

    @Override
    public List<LElement> getBackElements() {
        if (value == null || target instanceof LDeclaration) {
            return Collections.emptyList();
        } else {
            return value.getBackElements();
        }
    }
}
