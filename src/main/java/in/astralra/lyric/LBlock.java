package in.astralra.lyric;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jszaday on 8/9/2016.
 */
public interface LBlock {
    LScope getScope();
    LBlock add(Object object);
    List<Object> list();
}
