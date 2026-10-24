package java.net;

import java.io.IOException;

abstract public class ContentHandler {
    public ContentHandler() {

    }

    abstract public Object getContent(URLConnection urlc) throws IOException;

    @SuppressWarnings("rawtypes")
    public Object getContent(URLConnection urlc, Class<?>[] classes) throws IOException {
        Object obj = getContent(urlc);

        for (int i = 0; i < classes.length; i++) {
            if (classes[i].isInstance(obj))
                return obj;
        }
        return null;
    }
}
