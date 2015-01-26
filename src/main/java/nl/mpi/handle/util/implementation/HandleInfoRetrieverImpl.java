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
import java.net.URI;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;
import net.handle.hdllib.AdminRecord;
import net.handle.hdllib.Common;
import net.handle.hdllib.Encoder;
import net.handle.hdllib.HandleValue;
import net.handle.hdllib.Util;
import nl.mpi.handle.util.HandleInfoRetriever;
import nl.mpi.util.Checksum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @see HandleInfoRetriever
 * 
 * Part of the implementation was adapted from the CMDI Handle Assigner project
 * 
 * @author guisil
 */
public class HandleInfoRetrieverImpl implements HandleInfoRetriever {
    
    private static final Logger logger = LoggerFactory.getLogger(HandleInfoRetrieverImpl.class);
    
    private String hdlShortProxy = "hdl:";
    private String hdlLongProxy = "http://hdl.handle.net/";
    
    private String prefix;
    private String prefixWithSlash;
    
    
    public HandleInfoRetrieverImpl(String prefix) {
        
        this.prefix = prefix;
        
        prefixWithSlash = prefix + "/";
    }
    
    /**
     * @see HandleInfoRetriever#isHandlePrefixKnown(java.lang.String)
     */
    @Override
    public boolean isHandlePrefixKnown(String handle) {
        
        String handleWithoutProxy = getHandleWithoutProxy(handle);
        
        if(handleWithoutProxy.startsWith(prefixWithSlash)) {
            return true;
        }
        
        return false;
    }
    
    /**
     * @see HandleInfoRetriever#stripHandle(java.lang.String)
     */
    @Override
    public String stripHandle(String handle) {
        
        logger.debug("Stripping handle: {}", handle);
        
        String handleWithoutProxy = getHandleWithoutProxy(handle);
        
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
    
    private String getHandleWithoutProxy(String handle) {
        if(handle.startsWith(hdlShortProxy)) {
            return handle.replace(hdlShortProxy, "");
        } else if(handle.startsWith(hdlLongProxy)) {
            return handle.replace(hdlLongProxy, "");
        } else {
            return handle;
        }
    }
    
    /**
     * @see HandleInfoRetriever#createHandleInformation(java.io.File, java.net.URI)
     */
    @Override
    public HandleValue[] createHandleInformation(File file, URI uri) {
        
        logger.debug("Creating handle information; file: {}; uri: {}", file, uri);
        
        long currentTimeInMills = Calendar.getInstance().getTimeInMillis();
        
        Timestamp crawlTime = new Timestamp(currentTimeInMills);
        boolean onsite = true;
        Timestamp fileTime;
        long fileSize;
        String checksum;
        if (file != null) {
            fileTime = new Timestamp(file.lastModified());
            fileSize = file.length(); // currently 0 for nonlocal files
            checksum = Checksum.create(file.toString());
        } else {
            fileTime = new Timestamp(currentTimeInMills);
            fileSize = 0;
            checksum = "";
        }

        HandleValue iValues [] = {
            new HandleValue(6, Util.encodeString("FILETIME"), Util.encodeString((fileTime.toString()))),
            new HandleValue(5, Util.encodeString("CHECKSUM"), Util.encodeString(checksum)),//checksum.getBytes()),
            new HandleValue(4, Util.encodeString("ONSITE"), Util.encodeString(Boolean.toString(onsite))),
            new HandleValue(3, Util.encodeString("FILESIZE"), Util.encodeString(Long.toString(fileSize))),
            new HandleValue(2, Util.encodeString("CRAWLTIME"), Util.encodeString(crawlTime.toString())),
            new HandleValue(1, Util.encodeString("URL"), Util.encodeString(uri.toString())),
            new HandleValue(100, Common.STD_TYPE_HSADMIN,
            Encoder.encodeAdminRecord(new AdminRecord(
            Util.encodeString("0.NA/" + prefix), 200,
            false, false, false, false, true, true, true, true,
            false, false, false, false)))
        };
        
        return iValues;
    }
    
    /**
     * @see HandleInfoRetriever#generateUuidHandle()
     */
    @Override
    public String generateUuidHandle() {
        
        logger.debug("Generating UUID for new handle");
        
	UUID uuid = UUID.randomUUID();
	String randomUUIDString = uuid.toString().toUpperCase();
	String pid = prefix + "/00-" + randomUUIDString;
	return pid;
    }
    
    /**
     * @see HandleInfoRetriever#handleIsValidUuid(java.lang.String)
     */
    @Override
    public boolean handleIsValidUuid(String handle) {
        
        logger.debug("Checking if handle '{}' is a valid UUID", handle);
        
	String uuid;
	String testShortHandlePrefix = hdlShortProxy + prefixWithSlash + "/00-";
	String testLongHandlePrefix = hdlLongProxy + prefixWithSlash + "/00-";
	try {
	    if (handle.startsWith(testShortHandlePrefix)) {
		uuid = handle.replace(testShortHandlePrefix, "");
	    } else {
		uuid = handle.replace(testLongHandlePrefix, "");
	    }
	    logger.info("uuid : {}", uuid);
	    UUID.fromString(uuid);
	    return true;
	} catch (IllegalArgumentException ex) {
	    logger.error("uuid not valid", ex);
	    return false;
	}
    }
}
