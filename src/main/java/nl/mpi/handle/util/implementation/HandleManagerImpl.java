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
import java.net.URI;
import java.net.URISyntaxException;
import net.handle.hdllib.HandleException;
import net.handle.hdllib.HandleValue;
import nl.mpi.handle.util.HandleInfoRetriever;
import nl.mpi.handle.util.HandleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @see HandleManager
 * @author guisil
 */
public class HandleManagerImpl implements HandleManager {
    
    private static final Logger logger = LoggerFactory.getLogger(HandleManagerImpl.class);
    
    private HandleInfoRetriever handleInfoRetriever;
    private HandleUtil handleUtil;
    
    private String justPrefix;
    private String altPrefix;
    
    public HandleManagerImpl(HandleInfoRetriever hdlInfoRetriever, HandleUtil hdlUtil, String prefix) throws FileNotFoundException, IOException {

        this.handleInfoRetriever = hdlInfoRetriever;
        this.handleUtil = hdlUtil;
        
        this.justPrefix = prefix + "/";
	this.altPrefix = "hdl:" + justPrefix;
    }
    
    /**
     * @see HandleManager#areHandlesEquivalent(java.net.URI, java.net.URI)
     */
    @Override
    public boolean areHandlesEquivalent(URI aHandleUri, URI anotherHandleUri) {
        
        logger.debug("Checking if handles '{}' and '{}' are equivalent", aHandleUri, anotherHandleUri);

        String aHandle = null;
        String anotherHandle = null;
        
        if(aHandleUri != null && anotherHandleUri != null) {
            aHandle = aHandleUri.toString();
            anotherHandle = anotherHandleUri.toString();
        }
        
        //TODO validate handles first?
        if(aHandle != null && !aHandle.isEmpty() && anotherHandle != null && !anotherHandle.isEmpty()) {
           
            String aStrippedHandle = handleInfoRetriever.stripHandle(aHandle);
            String anotherStrippedHandle = handleInfoRetriever.stripHandle(anotherHandle);

            return aStrippedHandle.equals(anotherStrippedHandle);
        }
        
        throw new IllegalArgumentException("Invalid handle(s)");
    }

    /**
     * @see HandleManager#prepareHandleWithHdlPrefix(java.net.URI)
     */
    @Override
    public URI prepareHandleWithHdlPrefix(URI handleToPrepare) throws URISyntaxException {
        
        logger.debug("Preparing handle '{}' with hdl prefix", handleToPrepare);
        
        String strippedHandle = handleInfoRetriever.stripHandle(handleToPrepare.toString());
        URI handleWithHdlPrefix = new URI(altPrefix + strippedHandle);
        
        logger.debug("Prepared handle: {}", handleWithHdlPrefix);
        return handleWithHdlPrefix;
    }
    
    /**
     * @see HandleManager#assignNewHandle(java.io.File, java.net.URI)
     */
    @Override
    public URI assignNewHandle(File file, URI targetURI) throws HandleException, IOException, URISyntaxException {
        
        logger.debug("Assigning a newly generated handle. File: {}; target uri: {}", file, targetURI);
        
        String generatedHandle = handleInfoRetriever.generateUuidHandle();
        
        HandleValue[] handleInformation = handleInfoRetriever.createHandleInformation(file, targetURI);
        
        handleUtil.createHandle(generatedHandle, handleInformation);
        
        URI generatedHandleURI = new URI(generatedHandle);
        logger.debug("Generated handle - {} - was successfully created", generatedHandleURI);
        return generatedHandleURI;
    }

    /**
     * @see HandleManager#updateHandle(java.io.File, java.net.URI, java.net.URI)
     */
    @Override
    public void updateHandle(File file, URI handle, URI newTarget) throws HandleException, IOException {
        
        logger.debug("Handle '{}' for file '{}' being updated to new target uri: {}", handle, file, newTarget);
        
        HandleValue[] handleInformation = handleInfoRetriever.createHandleInformation(file, newTarget);
        
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
