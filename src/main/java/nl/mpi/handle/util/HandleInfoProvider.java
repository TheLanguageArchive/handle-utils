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
import java.net.URI;
import net.handle.hdllib.HandleValue;

/**
 * Helper class that provides information related with handles.
 * @author guisil
 */
public interface HandleInfoProvider {
 
    /**
     * Creates the information for the handle, based on the file and URI
     * @param file
     * @param uri
     * @return handle information
     */
    public HandleValue[] createHandleInformation(File file, URI uri);
    
    /**
     * Generates a random UUID which is meant to be used as a handle

     * @return handle as UUID
     */
    public String generateUuidHandle();
    
    /**
     * Checks if the given handle is a valid UUID
     * (when stripped)
     * @param handle
     * @return true if the handle is a valid UUID
     */
    public boolean handleIsValidUuid(String handle);
}
