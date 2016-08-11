package in.astralra.lyric.core;

import java.util.List;

/**
 * Created by jszaday on 8/4/2016.
 */
public interface LType {
    boolean isAssignableFrom(LType other);
    List<LType> getTypeParameters();
    String getIdentifier();
    String getName();
    boolean isNativeType();
}
