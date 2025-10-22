package java.io;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class FileDescriptor {
    private int fd;

    public static final FileDescriptor in = new FileDescriptor(0);
    public static final FileDescriptor out = new FileDescriptor(1);
    public static final FileDescriptor err = new FileDescriptor(2);

    public FileDescriptor() {
        fd = -1;
    }

    private FileDescriptor(int fd) {
        this.fd = fd;
    }

    public boolean valid() {
        return fd != -1;
    }

    public native void sync() throws SyncFailedException;
}
