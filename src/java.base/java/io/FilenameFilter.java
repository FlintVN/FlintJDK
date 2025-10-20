package java.io;

@SuppressWarnings("doclint:reference")
@FunctionalInterface
public interface FilenameFilter {
    boolean accept(File dir, String name);
}
