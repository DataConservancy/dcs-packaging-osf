package org.dataconservancy.cos.osf.client.support;

import java.net.URI;
import java.util.Map;
import java.util.function.Function;

/**
 * Extracts a link named "download" from the supplied map.  If the link is not present, {@code null} is returned.
 */
public class DownloadLinkTransform implements Function<Map<String, ?>, URI> {

    @Override
    public URI apply(Map<String, ?> links) {
        String link = (String) links.get("download");
        if (link != null) {
            return URI.create(link);
        }

        return null;
    }
}
