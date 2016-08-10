package in.astralra.lyric;

import java.util.ArrayList;
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
}
