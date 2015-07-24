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
package nl.mpi.handle.util.implementation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import net.handle.hdllib.HandleException;
import net.handle.hdllib.HandleValue;
import nl.mpi.handle.util.HandleInfoProvider;
import nl.mpi.handle.util.HandleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @see HandleManager
 * @author guisil
 */
public class HandleManagerImpl implements HandleManager, Serializable {
    
    private static final Logger logger = LoggerFactory.getLogger(HandleManagerImpl.class);
    
    private final HandleInfoProvider handleInfoProvider;
    private final HandleUtil handleUtil;
    
    public HandleManagerImpl(HandleInfoProvider hdlInfoRetriever, HandleUtil hdlUtil, String prefix)
            throws FileNotFoundException, IOException {

        this.handleInfoProvider = hdlInfoRetriever;
        this.handleUtil = hdlUtil;
    }

    
    /**
     * @see HandleManager#assignNewHandle(java.io.File, java.net.URI)
     */
    @Override
    public URI assignNewHandle(File file, URI targetURI) throws HandleException, IOException {
        
        logger.debug("Assigning a newly generated handle. File: {}; target uri: {}", file, targetURI);
        
        String generatedHandle = handleInfoProvider.generateUuidHandle();
        
        HandleValue[] handleInformation = handleInfoProvider.createHandleInformation(file, targetURI);
        
        handleUtil.createHandle(generatedHandle, handleInformation);
        
        URI generatedHandleURI = URI.create(generatedHandle);
        logger.debug("Generated handle - {} - was successfully created", generatedHandleURI);
        return generatedHandleURI;
    }

    /**
     * @see HandleManager#updateHandle(java.io.File, java.net.URI, java.net.URI)
     */
    @Override
    public void updateHandle(File file, URI handle, URI newTarget) throws HandleException, IOException {
        
        logger.debug("Handle '{}' for file '{}' being updated to new target uri: {}", handle, file, newTarget);
        
        HandleValue[] handleInformation = handleInfoProvider.createHandleInformation(file, newTarget);
        
        handleUtil.updateHandleValue(handle.toString(), handleInformation);
    }

    /**
     * @see HandleManager#deleteHandle(java.net.URI)
     */
    @Override
    public void deleteHandle(URI handle) throws HandleException, IOException {
        
        logger.debug("Handle '{}' being deleted", handle);
        
        handleUtil.deleteHandle(handle.toString());
    }
}
