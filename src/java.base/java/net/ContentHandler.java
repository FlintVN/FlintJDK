package java.net;

import java.io.IOException;

abstract public class ContentHandler {
    public ContentHandler() {

    }

    abstract public Object getContent(URLConnection urlc) throws IOException;
}
