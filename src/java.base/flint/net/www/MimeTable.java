package flint.net.www;

import java.util.Map;
import java.util.HashMap;
import java.net.FileNameMap;

public class MimeTable implements FileNameMap {
    private static MimeTable instance;

    private Map<String, String> extensionMap = new HashMap<>();

    private MimeTable() {

    }

    public static synchronized MimeTable loadTable() {
        if (instance == null)
            instance = new MimeTable();
        return instance;
    }

    public String getContentTypeFor(String fileName) {
        String ext = getExtensionName(fileName);
        if (ext == null)
            return null;
        String contentType = extensionMap.get(ext);
        if (contentType == null) {
            contentType = getContentTypeFromExtensionName(ext);
            if (contentType != null)
                extensionMap.put(ext, contentType);
        }
        return contentType;
    }

    private static String getExtensionName(String fileName) {
        int dot = fileName.lastIndexOf('.');
        if (dot < 0)
            return null;
        return fileName.substring(dot).toLowerCase();
    }

    private static String getContentTypeFromExtensionName(String extensionName) {
        switch (extensionName.toLowerCase()) {
            case ".txt":
            case ".text":
            case ".log":
            case ".c":
            case ".h":
            case ".c++":
            case ".cpp":
            case ".php":
            case ".py":
            case ".cc":
            case ".java":     return "text/plain";
            case ".htm":
            case ".html":     return "text/html";
            case ".xml":      return "text/xml";
            case ".css":      return "text/css";
            case ".csv":      return "text/csv";
            case ".js":       return "text/javascript";
            case ".md":
            case ".markdown": return "text/markdown";

            case ".png":      return "image/png";
            case ".jpg":
            case ".jpeg":     return "image/jpeg";
            case ".gif":      return "image/gif";
            case ".bmp":      return "image/bmp";
            case ".webp":     return "image/webp";
            case ".svg":      return "image/svg+xml";
            case ".ico":      return "image/x-icon";
            case ".tif":
            case ".tiff":     return "image/tiff";

            case ".wav":      return "audio/wav";
            case ".mid":
            case ".midi":     return "audio/midi";
            case ".mp3":      return "audio/mpeg";
            case ".ogg":      return "audio/ogg";
            case ".aac":      return "audio/aac";
            case ".flac":     return "audio/flac";
            case ".m4a":      return "audio/mp4";

            case ".avi":      return "video/x-msvideo";
            case ".mp4":      return "video/mp4";
            case ".mpeg":
            case ".mpg":      return "video/mpeg";
            case ".mov":      return "video/quicktime";
            case ".wmv":      return "video/x-ms-wmv";
            case ".webm":     return "video/webm";
            case ".mkv":      return "video/x-matroska";

            case ".pdf":      return "application/pdf";
            case ".doc":      return "application/msword";
            case ".docx":     return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case ".xls":      return "application/vnd.ms-excel";
            case ".xlsx":     return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case ".ppt":      return "application/vnd.ms-powerpoint";
            case ".pptx":     return "application/vnd.openxmlformats-officedocument.presentationml.presentation";

            case ".zip":      return "application/zip";
            case ".rar":      return "application/vnd.rar";
            case ".7z":       return "application/x-7z-compressed";
            case ".tar":      return "application/x-tar";
            case ".gz":       return "application/gzip";
            case ".bin":      return "application/octet-stream";
            case ".json":     return "application/json";

            case ".jar":      return "application/java-archive";
            case ".class":    return "application/java-vm";

            default:          return "application/octet-stream";
        }
    }
}
