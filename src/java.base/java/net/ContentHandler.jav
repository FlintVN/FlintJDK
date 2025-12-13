package java.net;

import java.io.IOException;

abstract public class ContentHandler {
    abstract public Object getContent(URLConnection urlc) throws IOException;
}
