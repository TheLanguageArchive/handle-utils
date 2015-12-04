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
 * @see HandleParser
 * @author guisil
 */
public class HandleParserImpl implements HandleParser, Serializable {

    private static final Logger logger = LoggerFactory.getLogger(HandleParserImpl.class);
    
    private final String completeHdlProxy;
    private final String completeLongHdlProxy;
    private final String prefixWithSlash;
    private final String completeHdlPrefix;
    private final String completeLongHdlPrefix;
    
    
    public HandleParserImpl(String prefix) {
        prefixWithSlash = prefix + "/";
        completeHdlProxy = HandleConstants.HDL_SHORT_PROXY + ":";
        completeHdlPrefix = completeHdlProxy + prefixWithSlash;
        completeLongHdlProxy = HandleConstants.HDL_LONG_PROXY;
        completeLongHdlPrefix = completeLongHdlProxy + prefixWithSlash;
    }
    
    
    /**
     * @see HandleParser#isHandleUriWithKnownPrefix(java.net.URI)
     */
    @Override
    public boolean isHandleUriWithKnownPrefix(URI handleUri) {
        
	return handleUri != null
                && ( (startsWithKnownHandleProxy(handleUri) && isHandleWithKnownPrefix(handleUri.toString()))
                    || isHandleWithKnownPrefix(handleUri.toString()) );
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
        
        return HandleConstants.HDL_SHORT_PROXY.equalsIgnoreCase(uri.getScheme()) || uri.toString().startsWith(HandleConstants.HDL_LONG_PROXY);
    }
    
    /**
     * @see HandleParser#areHandlesEquivalent(java.net.URI, java.net.URI)
     */
    @Override
    public boolean areHandlesEquivalent(URI aHandleUri, URI anotherHandleUri) {
        
        logger.debug("Checking if handles '{}' and '{}' are equivalent", aHandleUri, anotherHandleUri);

        String aStrippedHandle = stripAndValidateHandleIfPrefixIsKnown(aHandleUri);
        String anotherStrippedHandle = stripAndValidateHandleIfPrefixIsKnown(anotherHandleUri);

        return aStrippedHandle.equals(anotherStrippedHandle);
    }

    /**
     * @see HandleParser#prepareHandleWithHdlPrefix(java.net.URI)
     */
    @Override
    public URI prepareHandleWithHdlPrefix(URI handleToPrepare) {
        
        logger.debug("Preparing handle '{}' with hdl prefix", handleToPrepare);
        
        assureHandleIsNotNullOrEmpty(handleToPrepare);
        if(!matchesHandlePattern(handleToPrepare.toString())) {
            throw new IllegalArgumentException("Invalid handle (" + handleToPrepare + ")");
        }
        
        String strippedHandle;
        if(isHandlePrefixKnown(handleToPrepare.toString())) {
            strippedHandle = stripHandleIfPrefixIsKnown(handleToPrepare);
        } else {
            strippedHandle = getHandleWithoutProxy(handleToPrepare.toString());
        }

        return prepareHandle(strippedHandle, completeHdlPrefix, completeHdlProxy);
    }
    
    /**
     * @see HandleParser#prepareAndValidateHandleWithHdlPrefix(java.net.URI)
     */
    @Override
    public URI prepareAndValidateHandleWithHdlPrefix(URI handleToPrepare) {

        logger.debug("Preparing (and validating) handle '{}' with hdl prefix", handleToPrepare);
        
        String strippedHandle = stripAndValidateHandleIfPrefixIsKnown(handleToPrepare);

        return prepareHandle(strippedHandle, completeHdlPrefix, completeHdlProxy);
    }

    /**
     * @see HandleParser#prepareAndValidateHandleWithLongHdlPrefix(java.net.URI)
     */
    @Override
    public URI prepareAndValidateHandleWithLongHdlPrefix(URI handleToPrepare) {
        
        logger.debug("Preparing (and validating) handle '{}' with long hdl prefix", handleToPrepare);
        
        String strippedHandle = stripAndValidateHandleIfPrefixIsKnown(handleToPrepare);
        
        return prepareHandle(strippedHandle, completeLongHdlPrefix, completeLongHdlProxy);
    }
    
    /**
     * @see HandleParser#prepareAndValidateHandleWithoutProxy(java.net.URI)
     */
    @Override
    public URI prepareAndValidateHandleWithoutProxy(URI handleToPrepare) {
        
        logger.debug("Preparing (and validating) handle '{}' without proxy", handleToPrepare);
        
        assureHandleIsValid(handleToPrepare);
        
        return URI.create(getHandleWithoutProxy(handleToPrepare.toString()));
    }
    
    /**
     * @see HandleParser#stripAndValidateHandleIfPrefixIsKnown(java.net.URI)
     */
    @Override
    public String stripAndValidateHandleIfPrefixIsKnown(URI handle) {
        
        logger.debug("Stripping (and validating) handle: {}", handle);
        
        assureHandleIsValid(handle);
        
        return stripHandleIfPrefixIsKnown(handle);
    }

    /**
     * @see HandleParser#getHandleWithoutProxy(java.lang.String) 
     */
    @Override
    public String getHandleWithoutProxy(String handleString) {
        if(handleString.startsWith(completeHdlProxy)) {
            return handleString.replace(completeHdlProxy, "");
        } else if(handleString.startsWith(HandleConstants.HDL_LONG_PROXY)) {
            return handleString.replace(HandleConstants.HDL_LONG_PROXY, "");
        } else if(handleString.startsWith("/"))
            return handleString.substring(1);
        else {
            return handleString;
        }
    }
    
    
    private boolean isHandlePrefixKnown(String handleString) {
        
        String handleWithoutProxy = getHandleWithoutProxy(handleString);
        
        if(handleWithoutProxy.startsWith(prefixWithSlash)) {
            return true;
        }
        
        return false;
    }
    
    private boolean matchesHandlePattern(String handleString) {
        return HandleConstants.HANDLE_PATTERN.matcher(getHandleWithoutProxy(handleString)).matches();
    }
    
    private boolean isHandleWithKnownPrefix(String handleString) {
        return isHandlePrefixKnown(getHandleWithoutProxy(handleString)) &&
                matchesHandlePattern(handleString);
                
    }
    
    private void assureHandleIsNotNullOrEmpty(URI possibleHandle) {
        if(possibleHandle == null || possibleHandle.toString().trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid handle (" + possibleHandle + ")");
        }
    }
    
    private void assureHandleIsValid(URI possibleHandle) {
        assureHandleIsNotNullOrEmpty(possibleHandle);
        if(!isHandleUriWithKnownPrefix(possibleHandle)) {
            throw new IllegalArgumentException("Invalid handle (" + possibleHandle + ")");
        }
    }
    
    private String stripHandleIfPrefixIsKnown(URI handle) {
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
    
    private URI prepareHandle(String strippedHandle, String completePrefixToAdd, String completeProxyToAdd) {
        
        URI handleWithHdlPrefix = URI.create(isHandlePrefixKnown(strippedHandle) || !strippedHandle.contains("/") ? 
            completePrefixToAdd + strippedHandle : completeProxyToAdd + strippedHandle);

        logger.debug("Prepared handle: {}", handleWithHdlPrefix);
        return handleWithHdlPrefix;
    }
}
