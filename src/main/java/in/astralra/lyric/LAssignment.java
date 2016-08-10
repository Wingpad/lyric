package in.astralra.lyric;

/**
 * Created by jszaday on 8/9/2016.
 */
public class LAssignment {
    private LExpression target;
    private LOperator operator;
    private LExpression value;

    public LAssignment(LExpression target, LOperator operator, LExpression value) {
        this.target = target;
        this.operator = operator;
        this.value = value;
    }
}
