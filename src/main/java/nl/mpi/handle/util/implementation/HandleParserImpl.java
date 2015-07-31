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
package nl.mpi.handle.util.implementation;

import java.io.Serializable;
import java.net.URI;
import nl.mpi.handle.util.HandleParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author guisil
 */
public class HandleParserImpl implements HandleParser, Serializable {

    private static final Logger logger = LoggerFactory.getLogger(HandleParserImpl.class);
    
    private final String prefixWithSlash;
    private final String completeHdlPrefix;
    
    
    public HandleParserImpl(String prefix) {
        prefixWithSlash = prefix + "/";
        completeHdlPrefix = HandleConstants.HDL_SHORT_PROXY + prefixWithSlash;
    }
    
    
    /**
     * @see HandleParser#startsWithKnownHandleProxy(java.net.URI)s
     */
    @Override
    public boolean startsWithKnownHandleProxy(URI uri) {
        
        logger.debug("Checking if uri {} starts with a known proxy", uri);
        
        if(uri == null) {
            return false;
        }
        
        return uri.toString().startsWith(HandleConstants.HDL_SHORT_PROXY) || uri.toString().startsWith(HandleConstants.HDL_LONG_PROXY);
    }
    
        /**
     * @see HandleManager#isHandlePrefixKnown(java.net.URI)
     */
    @Override
    public boolean isHandlePrefixKnown(URI handleUri) {
        
        logger.debug("Checking if handle '{}' has a known prefix", handleUri);
        
        String handle = null;
        
        if(handleUri != null) {
            handle = handleUri.toString();
        }
        
        if(handle != null && !handle.isEmpty()) {
            return isHandlePrefixKnown(handle);
        }
        
        throw new IllegalArgumentException("Invalid handle");
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
           
            String aStrippedHandle = stripHandleIfPrefixIsKnown(aHandleUri);
            String anotherStrippedHandle = stripHandleIfPrefixIsKnown(anotherHandleUri);

            return aStrippedHandle.equals(anotherStrippedHandle);
        }
        
        throw new IllegalArgumentException("Invalid handle(s)");
    }
    
    /**
     * @see HandleManager#prepareHandleWithHdlPrefix(java.net.URI)
     */
    @Override
    public URI prepareHandleWithHdlPrefix(URI handleToPrepare) {

        logger.debug("Preparing handle '{}' with hdl prefix", handleToPrepare);

        String strippedHandle = stripHandleIfPrefixIsKnown(handleToPrepare);
       // URI handleWithHdlPrefix = URI.create(completeHdlPrefix + strippedHandle);

        URI handleWithHdlPrefix = URI.create(isHandlePrefixKnown(strippedHandle) || strippedHandle.indexOf("/") == -1 ? 
            completeHdlPrefix + strippedHandle : HandleConstants.HDL_SHORT_PROXY + strippedHandle);

        logger.debug("Prepared handle: {}", handleWithHdlPrefix);
        return handleWithHdlPrefix;
    }
    
    /**
     * @see HandleParser#stripHandleIfPrefixIsKnown(java.net.URI)
     */
    @Override
    public String stripHandleIfPrefixIsKnown(URI handle) {
        
        logger.debug("Stripping handle: {}", handle);
        
        String handleWithoutProxy = getHandleWithoutProxy(handle.toString());
        
        logger.debug("Removed proxy from handle: {}", handleWithoutProxy);
        
        if(handleWithoutProxy.startsWith(prefixWithSlash)) {
            String handleWithoutPrefix = handleWithoutProxy.replace(prefixWithSlash, "");
            logger.debug("Removed known prefix from handle: {}", handleWithoutPrefix);
            return handleWithoutPrefix;
        } else {
            logger.debug("Kept unknown prefix in handle: {}", handleWithoutProxy);
            return handleWithoutProxy;
        }
    }
    
    
    private boolean isHandlePrefixKnown(String handle) {
        
        String handleWithoutProxy = getHandleWithoutProxy(handle);
        
        if(handleWithoutProxy.startsWith(prefixWithSlash)) {
            return true;
        }
        
        return false;
    }

    
    private String getHandleWithoutProxy(String handle) {
        if(handle.startsWith(HandleConstants.HDL_SHORT_PROXY)) {
            return handle.replace(HandleConstants.HDL_SHORT_PROXY, "");
        } else if(handle.startsWith(HandleConstants.HDL_LONG_PROXY)) {
            return handle.replace(HandleConstants.HDL_LONG_PROXY, "");
        } else if(handle.startsWith("/"))
            return handle.substring(1);
        else {
            return handle;
        }
    }
}
