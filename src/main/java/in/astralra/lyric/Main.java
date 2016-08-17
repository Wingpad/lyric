package in.astralra.lyric;

import in.astralra.lyric.compiler.LCompiler;
import in.astralra.lyric.compiler.LWriter;
import in.astralra.lyric.core.LSimpleBlock;
import in.astralra.lyric.gen.LyricLexer;
import in.astralra.lyric.gen.LyricParser;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;

/**
 * Created by jszaday on 8/9/2016.
 */
public class Main {
    public static void main(String... args) throws IOException {
        ANTLRFileStream fileStream = new ANTLRFileStream(args[0]);
        LyricLexer lexer = new LyricLexer(fileStream);
        TokenStream tokenStream = new CommonTokenStream(lexer);
        LyricParser parser = new LyricParser(tokenStream);
        LCompiler compiler = new LCompiler();
        ParseTree parseTree = parser.program();
        Object object = compiler.visit(parseTree);
        LWriter writer = new LWriter((LSimpleBlock) object);

        System.out.println(writer.visit());
    }
}
