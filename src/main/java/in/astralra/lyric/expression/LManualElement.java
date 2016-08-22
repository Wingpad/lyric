package in.astralra.lyric.expression;

import in.astralra.lyric.core.LElement;

import java.util.Collections;
import java.util.List;

/**
 * Created by jszaday on 8/21/2016.
 */
public class LManualElement implements LElement {

    private String expression;
    private boolean needsSemicolon;

    public LManualElement(String expression) {
        this(expression, true);
    }

    public LManualElement(String expression, boolean needsSemicolon) {
        this.expression = expression;
        this.needsSemicolon = needsSemicolon;
    }

    @Override
    public boolean needsSemicolon() {
        return needsSemicolon;
    }

    @Override
    public List<LElement> getBackElements() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return expression;
    }
}
