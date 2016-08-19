package in.astralra.lyric.core;

import in.astralra.lyric.expression.LExpression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by jszaday on 8/9/2016.
 */
public class LSimpleBlock extends LScope implements LBlock {
    private List<Object> list = new ArrayList<>();

    @Override
    public LScope getScope() {
        return this;
    }

    @Override
    public LBlock add(Object object) {
        list.add(object);

        return this;
    }

    @Override
    public List<Object> list() {
        return list;
    }

    @Override
    public LFunction liftFunction(Collection<LExpression> arguments) {
        throw new RuntimeException("Cannot invoke a block.");
    }

    @Override
    public String lift(Collection<LExpression> arguments) {
        throw new RuntimeException("This requires pre-processor magic.");
    }

    @Override
    public LType getType() {
        throw new RuntimeException("Blocks do not have a return type.");
    }
}
