package flint.net.www;

import java.io.*;
import java.lang.reflect.Array;
import java.net.ProtocolException;
import java.util.Collections;
import java.util.*;

public class MessageHeader {
    private String keys[];
    private String values[];
    private int nkeys;
    private final int maxHeaderSize;
    private long size;

    public MessageHeader() {
       this(0);
    }

    public MessageHeader(int maxHeaderSize) {
        this.maxHeaderSize = maxHeaderSize;
        grow();
    }

    @SuppressWarnings("this-escape")
    public MessageHeader(InputStream is) throws java.io.IOException {
        maxHeaderSize = 0;
        parseHeader(is);
    }

    public synchronized String getHeaderNamesInList() {
        StringJoiner joiner = new StringJoiner(",");
        for (int i = 0; i < nkeys; i++)
            joiner.add(keys[i]);
        return joiner.toString();
    }

    public synchronized void reset() {
        keys = null;
        values = null;
        nkeys = 0;
        grow();
    }

    public synchronized String findValue(String k) {
        if (k == null) {
            for (int i = nkeys; --i >= 0;) {
                if (keys[i] == null)
                    return values[i];
            }
        }
        else {
            for (int i = nkeys; --i >= 0;) {
                if (k.equalsIgnoreCase(keys[i]))
                    return values[i];
            }
        }
        return null;
    }

    public synchronized int getKey(String k) {
        for (int i = nkeys; --i >= 0;) {
            if ((keys[i] == k) || (k != null && k.equalsIgnoreCase(keys[i])))
                return i;
        }
        return -1;
    }

    public synchronized String getKey(int n) {
        if (n < 0 || n >= nkeys)
            return null;
        return keys[n];
    }

    public synchronized String getValue(int n) {
        if (n < 0 || n >= nkeys)
            return null;
        return values[n];
    }

    public synchronized String findNextValue(String k, String v) {
        boolean foundV = false;
        if (k == null) {
            for (int i = nkeys; --i >= 0;) {
                if (keys[i] == null) {
                    if (foundV)
                        return values[i];
                    else if (values[i] == v)
                        foundV = true;
                }
            }
        }
        else {
            for (int i = nkeys; --i >= 0;) {
                if (k.equalsIgnoreCase(keys[i])) {
                    if (foundV)
                        return values[i];
                    else if (values[i] == v)
                        foundV = true;
                }
            }
        }
        return null;
    }

    public boolean filterNTLMResponses(String k) {
        boolean found = false;
        for (int i = 0; i < nkeys; i++) {
            if (k.equalsIgnoreCase(keys[i]) && values[i] != null && values[i].length() > 5 && values[i].substring(0, 5).equalsIgnoreCase("NTLM ")) {
                found = true;
                break;
            }
        }
        if (found) {
            int j = 0;
            for (int i = 0; i < nkeys; i++) {
                if (k.equalsIgnoreCase(keys[i]) && ("Negotiate".equalsIgnoreCase(values[i]) || "Kerberos".equalsIgnoreCase(values[i])))
                    continue;
                if (i != j) {
                    keys[j] = keys[i];
                    values[j] = values[i];
                }
                j++;
            }
            if (j != nkeys) {
                nkeys = j;
                return true;
            }
        }
        return false;
    }

    class HeaderIterator implements Iterator<String> {
        int index = 0;
        int next = -1;
        String key;
        boolean haveNext = false;
        Object lock;

        public HeaderIterator(String k, Object lock) {
            key = k;
            this.lock = lock;
        }

        public boolean hasNext() {
            synchronized (lock) {
                if (haveNext)
                    return true;
                while (index < nkeys) {
                    if (key.equalsIgnoreCase(keys[index])) {
                        haveNext = true;
                        next = index++;
                        return true;
                    }
                    index ++;
                }
                return false;
            }
        }

        public String next() {
            synchronized (lock) {
                if (haveNext) {
                    haveNext = false;
                    return values[next];
                }
                if (hasNext())
                    return next();
                else
                    throw new NoSuchElementException("No more elements");
            }
        }
        public void remove () {
            throw new UnsupportedOperationException("remove not allowed");
        }
    }

    public Iterator<String> multiValueIterator (String k) {
        return new HeaderIterator (k, this);
    }

    public synchronized Map<String, List<String>> getHeaders() {
        return getHeaders(null);
    }

    public synchronized Map<String, List<String>> getHeaders(String[] excludeList) {
        return filterAndAddHeaders(excludeList, null);
    }

    public synchronized Map<String, List<String>> filterAndAddHeaders(String[] excludeList, Map<String, List<String>> include) {
        boolean skipIt = false;
        Map<String, List<String>> m = new HashMap<String, List<String>>();
        for (int i = nkeys; --i >= 0;) {
            if (excludeList != null) {
                for (int j = 0; j < excludeList.length; j++) {
                    if ((excludeList[j] != null) && (excludeList[j].equalsIgnoreCase(keys[i]))) {
                        skipIt = true;
                        break;
                    }
                }
            }
            if (!skipIt) {
                List<String> l = m.get(keys[i]);
                if (l == null) {
                    l = new ArrayList<String>();
                    m.put(keys[i], l);
                }
                l.add(values[i]);
            }
            else
                skipIt = false;
        }

        if (include != null) {
                for (Map.Entry<String, List<String>> entry: include.entrySet()) {
                List<String> l = m.get(entry.getKey());
                if (l == null) {
                    l = new ArrayList<String>();
                    m.put(entry.getKey(), l);
                }
                l.addAll(entry.getValue());
            }
        }

        for (String key : m.keySet())
            m.put(key, Collections.unmodifiableList(m.get(key)));

        return Collections.unmodifiableMap(m);
    }

    private boolean isRequestline(String line) {
        String k = line.trim();
        int i = k.lastIndexOf(' ');
        if (i <= 0)
            return false;
        int len = k.length();
        if (len - i < 9)
            return false;

        char c1 = k.charAt(len - 3);
        char c2 = k.charAt(len - 2);
        char c3 = k.charAt(len - 1);
        if (c1 < '1' || c1 > '9')
            return false;
        if (c2 != '.')
            return false;
        if (c3 < '0' || c3 > '9')
            return false;

        return (k.substring(i + 1, len - 3).equalsIgnoreCase("HTTP/"));
    }

    public synchronized void print(PrintStream p) {
        for (int i = 0; i < nkeys; i++) {
            if (keys[i] != null) {
                StringBuilder sb = new StringBuilder(keys[i]);
                if (values[i] != null)
                    sb.append(": " + values[i]);
                else if (i != 0 || !isRequestline(keys[i]))
                    sb.append(":");
                p.print(sb.append("\r\n"));
            }
        }
        p.print("\r\n");
        p.flush();
    }

    public synchronized void add(String k, String v) {
        grow();
        keys[nkeys] = k;
        values[nkeys] = v;
        nkeys++;
    }

    public synchronized void prepend(String k, String v) {
        grow();
        for (int i = nkeys; i > 0; i--) {
            keys[i] = keys[i - 1];
            values[i] = values[i - 1];
        }
        keys[0] = k;
        values[0] = v;
        nkeys++;
    }

    public synchronized void set(int i, String k, String v) {
        grow();
        if (i < 0)
            return;
        else if (i >= nkeys)
            add(k, v);
        else {
            keys[i] = k;
            values[i] = v;
        }
    }

    private void grow() {
        if (keys == null || nkeys >= keys.length) {
            String[] nk = new String[nkeys + 4];
            String[] nv = new String[nkeys + 4];
            if (keys != null)
                System.arraycopy(keys, 0, nk, 0, nkeys);
            if (values != null)
                System.arraycopy(values, 0, nv, 0, nkeys);
            keys = nk;
            values = nv;
        }
    }

    public synchronized void remove(String k) {
        if(k == null) {
            for (int i = 0; i < nkeys; i++) {
                while (keys[i] == null && i < nkeys) {
                    for(int j = i; j < nkeys - 1; j++) {
                        keys[j] = keys[j + 1];
                        values[j] = values[j + 1];
                    }
                    nkeys--;
                }
            }
        }
        else {
            for (int i = 0; i < nkeys; i++) {
                while (k.equalsIgnoreCase(keys[i]) && i < nkeys) {
                    for(int j = i; j < nkeys - 1; j++) {
                        keys[j] = keys[j + 1];
                        values[j] = values[j + 1];
                    }
                    nkeys--;
                }
            }
        }
    }

    public synchronized void set(String k, String v) {
        for (int i = nkeys; --i >= 0;)
            if (k.equalsIgnoreCase(keys[i])) {
                values[i] = v;
                return;
            }
        add(k, v);
    }

    public synchronized void setIfNotSet(String k, String v) {
        if (findValue(k) == null)
            add(k, v);
    }

    public static String canonicalID(String id) {
        if (id == null)
            return "";
        int st = 0;
        int len = id.length();
        boolean substr = false;
        int c;
        while (st < len && ((c = id.charAt(st)) == '<' || c <= ' ')) {
            st++;
            substr = true;
        }
        while (st < len && ((c = id.charAt(len - 1)) == '>' || c <= ' ')) {
            len--;
            substr = true;
        }
        return substr ? id.substring(st, len) : id;
    }

    public void parseHeader(InputStream is) throws java.io.IOException {
        synchronized (this) {
            nkeys = 0;
            size = 0;
        }
        mergeHeader(is);
    }

    private void checkMaxHeaderSize(int sz) throws ProtocolException {
        if (maxHeaderSize > 0)
            checkNewSize(size, sz, 0);
    }

    private long checkNewSize(long size, int name, int value) throws ProtocolException {
        long newSize = size + name + value + 32;
        if (maxHeaderSize > 0 && newSize > maxHeaderSize) {
            Arrays.fill(keys, 0, nkeys, null);
            Arrays.fill(values,0, nkeys, null);
            nkeys = 0;
            throw new ProtocolException("Header size too big: " + newSize + " > " + maxHeaderSize);
        }
        return newSize;
    }

    @SuppressWarnings("fallthrough")
    public void mergeHeader(InputStream is) throws java.io.IOException {
        if (is == null)
            return;
        char s[] = new char[10];
        int firstc = is.read();
        while (firstc != '\n' && firstc != '\r' && firstc >= 0) {
            int len = 0;
            int keyend = -1;
            int c;
            boolean inKey = firstc > ' ';
            s[len++] = (char)firstc;
            checkMaxHeaderSize(len);
            parseloop: {
                long maxRemaining = maxHeaderSize > 0 ? maxHeaderSize - size - 32 : Long.MAX_VALUE;
                while ((c = is.read()) >= 0) {
                    switch (c) {
                        case ':':
                            if (inKey && len > 0)
                                keyend = len;
                            inKey = false;
                            break;
                        case '\t':
                            c = ' ';
                        case ' ':
                            inKey = false;
                            break;
                        case '\r':
                        case '\n':
                            firstc = is.read();
                            if (c == '\r' && firstc == '\n') {
                                firstc = is.read();
                                if (firstc == '\r')
                                    firstc = is.read();
                            }
                            if (firstc == '\n' || firstc == '\r' || firstc > ' ')
                                break parseloop;
                            c = ' ';
                            break;
                    }
                    if (len >= s.length) {
                        char ns[] = new char[s.length * 2];
                        System.arraycopy(s, 0, ns, 0, len);
                        s = ns;
                    }
                    s[len++] = (char)c;
                    if (maxHeaderSize > 0 && len > maxRemaining)
                        checkMaxHeaderSize(len);
                }
                firstc = -1;
            }
            while (len > 0 && s[len - 1] <= ' ')
                len--;
            String k;
            if (keyend <= 0) {
                k = null;
                keyend = 0;
            }
            else {
                k = String.copyValueOf(s, 0, keyend);
                if (keyend < len && s[keyend] == ':')
                    keyend++;
                while (keyend < len && s[keyend] <= ' ')
                    keyend++;
            }
            String v;
            if (keyend >= len)
                v = new String();
            else
                v = String.copyValueOf(s, keyend, len - keyend);
            int klen = k == null ? 0 : k.length();

            size = checkNewSize(size, klen, v.length());
            add(k, v);
        }
    }

    public synchronized String toString() {
        String result = super.toString() + nkeys + " pairs: ";
        for (int i = 0; i < keys.length && i < nkeys; i++)
            result += "{" + keys[i] + ": " + values[i] + "}";
        return result;
    }
}
