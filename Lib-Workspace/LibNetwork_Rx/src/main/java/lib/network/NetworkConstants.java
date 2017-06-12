package lib.network;

/**
 * @author yuansui
 */
public interface NetworkConstants {

    String KEmpty = "";

    interface Http {
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

    interface MediaType {
        String KStream = "application/octet-stream";
    }
}
