package org.dataconservancy.cos.osf.client.config;

/**
 * Factory responsible for returning populated instances of {@link WbClientConfiguration}.
 */
public interface WbConfigurationService {

    /**
     * Return an instance of the Waterbutler client configuration.  Should never be {@code null}.
     *
     * @return the Waterbutler client configuration
     */
    WbClientConfiguration getConfiguration();
}
