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
    
    private String prefix;
    
    private String handlePrefix;
    private String altPrefix;
    private String justPrefix;
    
    
    public HandleInfoRetrieverImpl(String prefix, String proxy) {
        
        this.prefix = prefix;
        
        this.justPrefix = prefix + "/";
        this.handlePrefix = proxy + justPrefix;
	this.altPrefix = "hdl:" + justPrefix;
    }
    
    /**
     * @see HandleInfoRetriever#stripHandle(java.lang.String)
     */
    @Override
    public String stripHandle(String handle) {
        
        logger.debug("Stripping handle: {}", handle);
        
        String strippedHandle;
        
        if(handle.startsWith(justPrefix)) {
            strippedHandle = handle.replace(justPrefix, "");
        } else if(handle.startsWith(handlePrefix)) {
            strippedHandle = handle.replace(handlePrefix, "");
        } else if(handle.startsWith(altPrefix)) {
            strippedHandle = handle.replace(altPrefix, "");
        } else {
            //the handle should have a valid prefix
            throw new IllegalArgumentException("Invalid handle prefix: " + handle);
        }
        
        logger.debug("Stripped handle: {}", strippedHandle);
        
        return strippedHandle;
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
	String testHandlePrefix = handlePrefix + "/00-";
	String testAltPrefix = altPrefix + "/00-";
	try {
	    if (handle.startsWith(testHandlePrefix)) {
		uuid = handle.replace(testHandlePrefix, "");
	    } else {
		uuid = handle.replace(testAltPrefix, "");
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
