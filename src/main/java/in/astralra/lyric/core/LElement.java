package in.astralra.lyric.core;

import java.util.List;

/**
 * Created by jszaday on 8/17/2016.
 */
public interface LElement {
    boolean needsSemicolon();
    List<LElement> getBackElements();
}
