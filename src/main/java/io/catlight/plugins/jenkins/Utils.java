package io.catlight.plugins.jenkins;

import org.jenkinsci.main.modules.instance_identity.InstanceIdentity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


/**
 * Plugin utilities
 */
class Utils {

    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);


    /**
     * Converts array to hex string
     *
     * @param bytes bytes to convert
     * @return array converted to hex string
     */
    public static String bytesToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }


    /**
     * @param s string to encode
     * @return url-encoded component
     */
    public static String encodeURIComponent(String s) {
        String result = null;

        try {
            result = URLEncoder.encode(s, "UTF-8")
                    .replaceAll("\\+", "%20")
                    .replaceAll("\\%21", "!")
                    .replaceAll("\\%27", "'")
                    .replaceAll("\\%28", "(")
                    .replaceAll("\\%29", ")")
                    .replaceAll("\\%7E", "~");
        }

        // This exception should never occur.
        catch (UnsupportedEncodingException e) {
            result = s;
        }

        return result;
    }


    /**
     * @return Url for the default space of current server
     */
    public static String getDefaultSpaceUri() {
        StringBuilder uriBuilder = new StringBuilder();
        uriBuilder.append("jenkins:");

        String serverId = getServerId();

        uriBuilder.append(serverId)
                .append(":default");

        return uriBuilder.toString();
    }


    /**
     * @return CatLight-compatible id of Jenkins server
     */
    private static String getServerId() {
        String instanceIdentity = Base64.getEncoder()
                .encodeToString(InstanceIdentity.get().getPublic().getEncoded());

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] hash = digest.digest(instanceIdentity.getBytes(StandardCharsets.US_ASCII));

        var serverId = Utils.bytesToHex(hash).substring(0, 32).toLowerCase();
        return serverId;
    }
}
