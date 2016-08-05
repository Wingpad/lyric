package in.astralra.lyric;

import java.util.Optional;
/**
 * Created by jszaday on 8/4/2016.
 */
public interface LDeclarable {
    LType getType();
    String getName();
    boolean isValid();
    Optional<LObject> getValue();
    // LAssignment getValue();
}
