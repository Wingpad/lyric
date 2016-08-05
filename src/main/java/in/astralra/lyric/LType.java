package in.astralra.lyric;

/**
 * Created by jszaday on 8/4/2016.
 */
public interface LType {
    boolean isAssignableFrom(LType other);
    String getIdentifier();
    String getName();
}
