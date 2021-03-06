/*
 * Copyright (C) 2015 Max Planck Institute for Psycholinguistics
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package nl.mpi.handle.util;

import java.net.URI;

/**
 * Provides some methods for parsing handles.
 * @author guisil
 */
public interface HandleParser {

    /**
     * Adapted from the HandleUtil class in the Metadata API.
     * @param handleUri
     * @return true if handleUri is composed of a known proxy and
     *  matches the expected handle pattern, including the known prefix
     */
    public boolean isHandleUriWithKnownPrefix(URI handleUri);
    
    /**
     * @param uri
     * @return true if uri starts with one of the known proxies
     * ("hdl:" or "http://hdl.handle.net/").
     */
    public boolean startsWithKnownHandleProxy(URI uri);
    
    /**
     * Checks if the given handles are equivalent, by stripping them of eventual
     * prefixes.
     * 
     * @param aHandleUri
     * @param anotherHandleUri
     * @return true if the handles are equivalent
     * @throws IllegalArgumentException if any of the handles is not valid
     */
    public boolean areHandlesEquivalent(URI aHandleUri, URI anotherHandleUri);
    
    /**
     * Adds the appropriate prefix ("hdl:") to the given handle, if necessary.
     * 
     * @param handleToPrepare handle which may or may not have already the prefix
     * @return handle with the appropriate prefix, or null if an empty or null handle is passed
     * @throws IllegalArgumentException if the handle is not valid
     */
    public URI prepareHandleWithHdlPrefix(URI handleToPrepare);
    
    /**
     * Adds the appropriate prefix ("hdl:") to the given handle, if necessary.
     * This method also makes sure the handle is valid and uses a known prefix.
     * 
     * @param handleToPrepare handle which may or may not have already the prefix
     * @return handle with the appropriate prefix, or null if an empty or null handle is passed
     * @throws IllegalArgumentException if the handle is not valid and has an unknown prefix
     */
    public URI prepareAndValidateHandleWithHdlPrefix(URI handleToPrepare);
    
    /**
     * Adds the long prefix ("http://hdl.handle.net/") to the given handle, if necessary.
     * This method also makes sure the handle is valid and uses a known prefix.
     * 
     * @param handleToPrepare handle which may or may not have already the prefix
     * @return handle with the long prefix, or null if an empty or null handle is passed
     * @throws IllegalArgumentException if the handle is not valid and has an unknown prefix
     */
    public URI prepareAndValidateHandleWithLongHdlPrefix(URI handleToPrepare);
    
    /**
     * Removes the possible prefixes ("hdl:" or "http://hdl.handle.net/") from he given handle, if necessary.
     * This method also makes sure the handle is valid and uses a known prefix.
     * 
     * @param handleToPrepare handle which may or may not have already the prefix
     * @return handle with the appropriate prefix, or null if an empty or null handle is passed
     * @throws IllegalArgumentException if the handle is not valid and has an unknown prefix
     */
    public URI prepareAndValidateHandleWithoutProxy(URI handleToPrepare);
    
    /**
     * Strips a handle of its prefixes.
     * If the prefix is unknown, it will be considered an invalid handle.
     * @param handle
     * @return stripped handle, or null if an empty or null handle is passed
     * @throws IllegalArgumentException if the handle is not valid and has an unknown prefix
     */
    public String stripAndValidateHandleIfPrefixIsKnown(URI handle);
    
    /**
     * Removes the proxy or scheme from a handle string
     * @param handleString a handle string, with or without scheme ("hdl:") or proxy ("http://hdl.handle.net/")
     * @return the handle, without scheme or proxy; or the input string itself in case no schema or proxy was detected
     */
    public String getHandleWithoutProxy(String handleString);
}
