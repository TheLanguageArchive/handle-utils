/*
 * Copyright (C) 2014 Max Planck Institute for Psycholinguistics
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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import net.handle.hdllib.HandleException;

/**
 * Class containing some useful operations related with handles
 * @author guisil
 */
public interface HandleManager {
    
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
    public URI prepareHandleWithHdlPrefix(URI handleToPrepare) throws URISyntaxException;
    
    /**
     * Generates a handle and associated information, and assigns it to the
     * target URI
     * 
     * @param file current location of the file to which the handle should be assigned
     * @param targetURI final URI of the file, which the handle should target
     * @return URI corresponding to the handle, with the appropriate prefix
     */
    public URI assignNewHandle(File file, URI targetURI) throws HandleException, IOException, URISyntaxException;
    
    /**
     * Updates the target of the given handle.
     * @param current location of the file to which the handle should be updated
     * @param handle handle to update, as a URI
     * @param newTarget new target to which the handle should point
     */
    public void updateHandle(File file, URI handle, URI newTarget) throws HandleException, IOException;
    
    /**
     * Deletes the given handle
     * @param handle handle to be deleted
     */
    public void deleteHandle(URI handle) throws HandleException, IOException;
}
