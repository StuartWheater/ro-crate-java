package edu.kit.datamanager.ro_crate.special;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Optional;

import org.apache.commons.validator.routines.UrlValidator;

import com.apicatalog.jsonld.uri.UriUtils;

/**
 * This class defines methods regarding URIs in general, which in RO-Crate
 * context means usually a valid, resolvable URL or a relative file path.
 * 
 * The purpose is to have a simple abstraction where the way e.g. a URL is
 * checked can be changed and tested easily for the whole library.
 */
public class UriUtil {

    /**
     * Hidden constructor, as this class only has static methods.
     */
    private UriUtil() {
    }

    /**
     * Returns true, if the given String is decoded.
     * 
     * @param uri the given URI. Usually a URL or relative file path.
     * @return true if url is decoded, false if it is not.
     */
    public static boolean isDecoded(String uri) {
        return !isEncoded(uri);
    }

    /**
     * Returns true, if the given String is encoded.
     * 
     * @param uri the given URI. Usually a URL or relative file path.
     * @return trie if the url is decoded, false if it is not.
     */
    public static boolean isEncoded(String uri) {
        return UriUtils.isURI(uri);
    }

    /**
     * Returns true, if the given string is a url.
     * 
     * @param uri the given string
     * @return true if it is a url, false otherwise.
     */
    public static boolean isUrl(String uri) {
        return asUrl(uri).isPresent();
    }

    /**
     * Tests if the given String is a URL, and if so, returns it.
     * 
     * @param uri the given String which will be tested.
     * @return the url, if it is one.
     */
    public static Optional<URL> asUrl(String uri) {
        try {
            return Optional.of(new URL(uri));
        } catch (MalformedURLException e) {
            return Optional.empty();
        }
    }

    /**
     * Returns true, if the given string is a path.
     * 
     * @param uri the given string
     * @return true if it is a path, false otherwise.
     */
    public static boolean isPath(String uri) {
        return asPath(uri).isPresent();
    }

    /**
     * Tests if the given String is a path, and if so, returns it.
     * 
     * @param uri the given String which will be tested.
     * @return the path, if it is one.
     */
    public static Optional<Path> asPath(String uri) {
        try {
            Path u = Path.of(uri);
            if (!isUrl(uri)) {
                return Optional.of(u);
            }
        } catch (InvalidPathException e) {
            return Optional.empty();
        }
        return Optional.empty();
    }

    /**
     * Encodes a string using the description if the ro-crate-specification.
     * 
     * @param uri the string to encode.
     * @return the encoded version of the given string, if possible.
     */
    public static Optional<String> encode(String uri) {
        if (isEncoded(uri)) {
            return Optional.of(uri);
        }

        Optional<URL> url = asUrl(uri);
        Optional<Path> p = asPath(uri);
        if (url.isPresent()) {
            try {
                URI u = url.get().toURI();
                return Optional.of(u.toASCIIString());
            } catch (URISyntaxException e) {
                return Optional.empty();
            }
        } else if (p.isPresent()) {
            // according to
            // https://www.researchobject.org/ro-crate/1.1/data-entities.html#encoding-file-paths
            // a file path may not be fully encoded and may contain international unicode
            // characters. So we try a "soft"-encoding first, and if this is not yet valid,
            // we really fully encode it.
            String result = p.get().toString();
            result = result.replace("\\", "/");
            result = result.replace("%", "%25");
            result = result.replace(" ", "%20");
            return Optional.of(result);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Returns true, if the URLs domain exists.
     * 
     * @param url the given URL
     * @return true if domain exists.
     */
    public static boolean hasValidDomain(String url) {
        UrlValidator validator = new UrlValidator();
        if (isDecoded(url)) {
            String encoded = URLEncoder.encode(url, StandardCharsets.UTF_8);
            return validator.isValid(encoded);
        }
        return validator.isValid(url);
    }
}