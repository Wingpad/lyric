package in.astralra.lyric;

import java.util.Collection;
import java.util.List;

/**
 * Created by jszaday on 8/5/2016.
 */
public class LInstance extends LObject {

    private LClass lClass;
    private List<LType> types;

    public LInstance(LClass lClass) {
        this.lClass = lClass;
    }

    public void setTypes(List<LType> types) {
        if (lClass.validTypes(types)) {
            this.types = types;
        } else {
            throw new IllegalArgumentException("Types do not implement the required types of the parent class.");
        }
    }

    @Override
    public LType getType() {
        return lClass;
    }

    @Override
    public LFunction invokeWith(Collection<LExpression> arguments) {
        return null;
    }
}
