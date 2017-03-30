package lib.network.model;

/**
 * @author yuansui
 */
public class NetworkConstants {

    public interface Http {
        String KUserAgent = "User-Agent";

        String KContentLen = "Content-Length";
        String KContentType = "Content-Type";

        /**
         * Encodings
         */
        String KContentEncoding = "Content-Encoding";
        String KAcceptEncoding = "Accept-Encoding";
        String KEncoding_gzip = "gzip";
        String KEncoding_identity = "identity";

        /**
         * Accept-Ranges
         */
        String KAcceptRanges = "Accept-Ranges";
        String KAcceptRanges_bytes = "bytes";

        String KContentRanges = "Content-Range";
    }

    public interface ContentType {
        String KStream = "application/octet-stream";
    }
}
