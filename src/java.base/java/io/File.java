package java.io;

import java.util.List;
import java.util.ArrayList;

public class File implements Comparable<File> {
    public static final char separatorChar = getSeparatorChar0();
    public static final String separator = String.valueOf(separatorChar);
    public static final char pathSeparatorChar = getPathSeparatorChar0();
    public static final String pathSeparator = String.valueOf(pathSeparatorChar);

    private final String path;

    private static native char getSeparatorChar0();
    private static native char getPathSeparatorChar0();
    private native boolean delete0();
    private native boolean rmdir0();
    private native boolean setLastModified0(long time);

    public File(String pathname) {
        if(pathname == null)
            throw new NullPointerException();
        this.path = pathname;
    }

    public File(String parent, String child) {
        if(child == null)
            throw new NullPointerException();
        if(parent != null) {
            if(parent.endsWith(separator))
                this.path = parent + child;
            else
                this.path = parent + separator + child;
        }
        else
            this.path = child;
    }

    public File(File parent, String child) {
        this(parent == null ? (String)null : parent.getPath(), child);
    }

    public String getName() {
        int index = path.lastIndexOf(separatorChar);
        return (index < 0) ? path : path.substring(index + 1);
    }

    public String getParent() {
        int index = path.lastIndexOf(separatorChar);
        if(index < 0)
            return null;
        if(!isAbsolute() || (path.indexOf(separatorChar) != index))
            return path.substring(0, index);
        if(index < path.length() - 1)
            return path.substring(0, index + 1);
        return null;
    }

    public File getParentFile() {
        String p = this.getParent();
        if(p == null) return null;
        return new File(p);
    }

    public String getPath() {
        return path;
    }

    public native boolean isAbsolute();

    public native String getAbsolutePath();

    public File getAbsoluteFile() {
        String absPath = getAbsolutePath();
        return new File(absPath);
    }

    public native String getCanonicalPath() throws IOException;

    public File getCanonicalFile() throws IOException {
        String canonPath = getCanonicalPath();
        return new File(canonPath);
    }

    public native boolean exists();

    public native boolean canWrite();

    public native boolean canRead();

    public native boolean isFile();

    public native boolean isDirectory();

    public native boolean isHidden();

    public native long lastModified();

    public native long length();

    public native boolean createNewFile() throws IOException;

    public boolean delete() {
        if(isDirectory())
            return rmdir0();
        else
            return delete0();
    }

    // TODO
    // public void deleteOnExit() {
    //     DeleteOnExitHook.add(path);
    // }

    public native String[] list();

    public String[] list(FilenameFilter filter) {
        String[] names = list();
        if((names == null) || (filter == null))
            return names;
        List<String> v = new ArrayList<>();
        for(int i = 0 ; i < names.length ; i++) {
            if(filter.accept(this, names[i]))
                v.add(names[i]);
        }
        return v.toArray(new String[v.size()]);
    }

    public File[] listFiles() {
        String[] ss = list();
        if(ss == null) return null;
        String dir = this.getPath();
        if(!dir.endsWith(separator))
            dir += separator;
        int n = ss.length;
        File[] fs = new File[n];
        for(int i = 0; i < n; i++) {
            String p = dir + ss[i];
            fs[i] = new File(p);
        }
        return fs;
    }

    public File[] listFiles(FilenameFilter filter) {
        String[] ss = list();
        if(ss == null) return null;
        String dir = this.getPath();
        if(!dir.endsWith(separator))
            dir += separator;
        ArrayList<File> files = new ArrayList<>();
        for(String s : ss) {
            if((filter == null) || filter.accept(this, s)) {
                String p = dir + s;
                files.add(new File(p));
            }
        }
        return files.toArray(new File[files.size()]);
    }

    public File[] listFiles(FileFilter filter) {
        String[] ss = list();
        if(ss == null) return null;
        String dir = this.getPath();
        if(!dir.endsWith(separator))
            dir += separator;
        ArrayList<File> files = new ArrayList<>();
        for(String s : ss) {
            String p = dir + s;
            File f = new File(p);
            if((filter == null) || filter.accept(f))
                files.add(f);
        }
        return files.toArray(new File[files.size()]);
    }

    public native boolean mkdir();

    public boolean mkdirs() {
        if(exists())
            return false;
        if(mkdir())
            return true;
        File canonFile = null;
        try {
            canonFile = getCanonicalFile();
        }
        catch(IOException e) {
            return false;
        }

        File parent = canonFile.getParentFile();
        return (parent != null && (parent.mkdirs() || parent.exists()) && canonFile.mkdir());
    }

    public native boolean renameTo(File dest);

    public boolean setLastModified(long time) {
        if(time < 0)
            throw new IllegalArgumentException("Negative time");
        return setLastModified0(time);
    }

    // TODO
    // public boolean setReadOnly() {
    //     return setPermission(ACCESS_WRITE, false, true);
    // }

    // TODO
    // public boolean setWritable(boolean writable, boolean ownerOnly) {
    //     return setPermission(ACCESS_WRITE, writable, ownerOnly);
    // }

    // TODO
    // public boolean setWritable(boolean writable) {
    //     return setWritable(writable, true);
    // }

    // TODO
    // public boolean setReadable(boolean readable, boolean ownerOnly) {
    //     return setPermission(ACCESS_READ, readable, ownerOnly);
    // }

    // TODO
    // public boolean setReadable(boolean readable) {
    //     return setReadable(readable, true);
    // }

    // TODO
    // public boolean setExecutable(boolean executable, boolean ownerOnly) {
    //     return setPermission(ACCESS_EXECUTE, executable, ownerOnly);
    // }

    // TODO
    // public boolean setExecutable(boolean executable) {
    //     return setExecutable(executable, true);
    // }

    // TODO
    // public boolean canExecute() {
    //     return canExecute0();
    // }

    // TODO
    // public static File[] listRoots() {
    //     return listRoots0();
    // }

    // TODO
    // public long getTotalSpace() {
    //     long space = getSpace(SPACE_TOTAL);
    //     return space >= 0L ? space : Long.MAX_VALUE;
    // }

    // TODO
    // public long getFreeSpace() {
    //     long space = getSpace(SPACE_FREE);
    //     return space >= 0L ? space : Long.MAX_VALUE;
    // }

    // TODO
    // public long getUsableSpace() {
    //     long space = getSpace(SPACE_USABLE);
    //     return space >= 0L ? space : Long.MAX_VALUE;
    // }

    // TODO
    // public static File createTempFile(String prefix, String suffix, File directory) throws IOException {
    //     // TODO
    //     throw new UnsupportedOperationException();
    // }

    // TODO
    // public static File createTempFile(String prefix, String suffix) throws IOException {
    //     return createTempFile(prefix, suffix, null);
    // }

    public int compareTo(File pathname) {
        return this.getPath().compareToIgnoreCase(pathname.getPath());
    }

    public boolean equals(Object obj) {
        if(obj instanceof File file)
            return compareTo(file) == 0;
        return false;
    }

    public int hashCode() {
        return path.toLowerCase().hashCode() ^ 1234321;
    }

    public String toString() {
        return getPath();
    }

    // TODO
    // @java.io.Serial
    // private synchronized void writeObject(java.io.ObjectOutputStream s) throws IOException {
    //     s.defaultWriteObject();
    //     s.writeChar(separatorChar);
    // }

    // TODO
    // @java.io.Serial
    // private synchronized void readObject(java.io.ObjectInputStream s) throws IOException, ClassNotFoundException {
    //     ObjectInputStream.GetField fields = s.readFields();
    //     String pathField = (String)fields.get("path", null);
    //     char sep = s.readChar();
    //     if(sep != separatorChar)
    //         pathField = pathField.replace(sep, separatorChar);
    //     String path = FS.normalize(pathField);
    //     UNSAFE.putReference(this, PATH_OFFSET, path);
    //     UNSAFE.putIntVolatile(this, PREFIX_LENGTH_OFFSET);
    // }

    // TODO
    // private static final jdk.internal.misc.Unsafe UNSAFE = jdk.internal.misc.Unsafe.getUnsafe();
    // private static final long PATH_OFFSET = UNSAFE.objectFieldOffset(File.class, "path");
    // private static final long PREFIX_LENGTH_OFFSET = UNSAFE.objectFieldOffset(File.class, "prefixLength");
    // private transient volatile Path filePath;

    // public Path toPath() {
    //     Path result = filePath;
    //     if(result == null) {
    //         synchronized (this) {
    //             result = filePath;
    //             if(result == null) {
    //                 result = FileSystems.getDefault().getPath(path);
    //                 filePath = result;
    //             }
    //         }
    //     }
    //     return result;
    // }
}
