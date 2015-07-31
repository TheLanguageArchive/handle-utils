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
     * @param uri
     * @return true if uri starts with one of the known proxies
     * ("hdl:" or "http://hdl.handle.net/").
     */
    public boolean startsWithKnownHandleProxy(URI uri);

    /**
     * @param handleUri
     * @return true if handle starts with the known prefix
     */
    public boolean isHandlePrefixKnown(URI handleUri);
    
    /**
     * Checks if the given handles are equivalent, by stripping them of eventual
     * prefixes.
     * 
     * @param aHandleUri
     * @param anotherHandleUri
     * @return true if the handles are equivalent
     */
    public boolean areHandlesEquivalent(URI aHandleUri, URI anotherHandleUri);
        
    /**
     * Adds the appropriate prefix to the given handle, if necessary
     * 
     * @param handleToPrepare handle which may or may not have already the prefix
     * @return handle with the appropriate prefix
     */
    public URI prepareHandleWithHdlPrefix(URI handleToPrepare);
    
    /**
     * Strips a handle of its prefixes.
     * If the prefix is unknown, it will be untouched.
     * @param handle
     * @return stripped handle
     */
    public String stripHandleIfPrefixIsKnown(URI handle);
}
