package org.apache.excalibur.source;

/**
 * Implemented by a SourceFactory when it supports applying a relative URI
 * to a base URI to form an absolute URI.
 *
 * <p>If a source factory does not implement this interface, the standard
 * algorithm (as described in RFC 2396) will be used. This interface only
 * needs to be implemented for source-types which have a different behaviour.
 */
public interface URIAbsolutizer
{
    public String absolutize(String baseURI, String location);
}
