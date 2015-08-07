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
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;
import net.handle.hdllib.AdminRecord;
import net.handle.hdllib.HandleValue;
import net.handle.hdllib.Util;
import nl.mpi.handle.util.HandleInfoProvider;
import nl.mpi.util.Checksum;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.stub;

/**
 *
 * @author guisil
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Checksum.class, HandleInfoProviderImpl.class})
public class HandleInfoProviderImplTest {
    
    @Rule public JUnitRuleMockery context = new JUnitRuleMockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
    
    private HandleInfoProvider handleInfoRetriever;

    private final String prefix = "11142";
    private final String prefixWithSlash = prefix + "/";
    private final String handleShortPrefix = HandleConstants.HDL_SHORT_PROXY + ":" + prefixWithSlash;
    private final String handleLongPrefix = HandleConstants.HDL_LONG_PROXY + prefixWithSlash;
    
    private final File mockFile = context.mock(File.class);
    
    public HandleInfoProviderImplTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        
        handleInfoRetriever = new HandleInfoProviderImpl(prefix);
    }
    
    @After
    public void tearDown() {
    }


    @Test
    public void retrieveHandleInformation() throws URISyntaxException {
        
        final URI uri = new URI("http://server/archive/target,cmdi");
        final boolean onsite = Boolean.TRUE;
        final long lastModifiedLong = Calendar.getInstance().getTimeInMillis();
        final Timestamp lastModified = new Timestamp(lastModifiedLong);
        final long fileSize = 10240;
        final String checksum = "asf0esu0j2tgiouh8923h0gfpowe";
        
        final AdminRecord adminRecord = new AdminRecord(
            Util.encodeString("0.NA/" + prefix), 200,
            false, false, false, false, true, true, true, true,
            false, false, false, false); // not being checked at the moment
        
        context.checking(new Expectations() {{
            
            oneOf(mockFile).lastModified(); will(returnValue(lastModifiedLong));
            oneOf(mockFile).length(); will(returnValue(fileSize));
        }});
        
        stub(method(Checksum.class, "create", String.class)).toReturn(checksum);
        
        HandleValue[] retrievedHandleValues = handleInfoRetriever.createHandleInformation(mockFile, uri);
        
        assertTrue("Retrieved handle values array has size different from expected", retrievedHandleValues.length == 7);
        
        int[] expectedArrayIndexes = {6, 5, 4, 3, 2, 1, 100};
        String[] expectedArrayTypes = {"FILETIME", "CHECKSUM", "ONSITE", "FILESIZE", "CRAWLTIME", "URL", null};
        String[] expectedArrayData = {lastModified.toString(), checksum, Boolean.toString(onsite), Long.toString(fileSize), null, uri.toString(), null};
        
        for(int i = 0; i < expectedArrayIndexes.length; i++) {
            assertEquals("Array index in position " + i + " different from expected", expectedArrayIndexes[i], retrievedHandleValues[i].getIndex());
            if(i < expectedArrayIndexes.length - 1) {
                assertEquals("Array type in position " + i + " different from expected", expectedArrayTypes[i], Util.decodeString(retrievedHandleValues[i].getType()));
                if(expectedArrayData[i] != null) { //for crawltime won't be checking the exact time
                    assertEquals("Array data in position " + i + " different from expected", expectedArrayData[i], Util.decodeString(retrievedHandleValues[i].getData()));
                }
            }
            
            //TODO not checking last position - how should it be matched?
        }
    }
    
    @Test
    public void retrieveHandleInformationNullFile() throws URISyntaxException {
        
        final URI uri = new URI("http://server/archive/target,cmdi");
        final boolean onsite = Boolean.TRUE;
        final long lastModifiedLong = Calendar.getInstance().getTimeInMillis();
        final Timestamp lastModified = new Timestamp(lastModifiedLong);
        final long fileSize = 0;
        final String checksum = "";
        
        final AdminRecord adminRecord = new AdminRecord(
            Util.encodeString("0.NA/" + prefix), 200,
            false, false, false, false, true, true, true, true,
            false, false, false, false); // not being checked at the moment
        
        
        HandleValue[] retrievedHandleValues = handleInfoRetriever.createHandleInformation(null, uri);
        
        assertTrue("Retrieved handle values array has size different from expected", retrievedHandleValues.length == 7);
        
        int[] expectedArrayIndexes = {6, 5, 4, 3, 2, 1, 100};
        String[] expectedArrayTypes = {"FILETIME", "CHECKSUM", "ONSITE", "FILESIZE", "CRAWLTIME", "URL", null};
        String[] expectedArrayData = {null, checksum, Boolean.toString(onsite), Long.toString(fileSize), null, uri.toString(), null};
        
        for(int i = 0; i < expectedArrayIndexes.length; i++) {
            assertEquals("Array index in position " + i + " different from expected", expectedArrayIndexes[i], retrievedHandleValues[i].getIndex());
            if(i < expectedArrayIndexes.length - 1) {
                assertEquals("Array type in position " + i + " different from expected", expectedArrayTypes[i], Util.decodeString(retrievedHandleValues[i].getType()));
                if(expectedArrayData[i] != null) { //for filetime / crawltime won't be checking the exact time
                    assertEquals("Array data in position " + i + " different from expected", expectedArrayData[i], Util.decodeString(retrievedHandleValues[i].getData()));
                }
            }
            
            //TODO not checking last position - how should it be matched?
        }
    }

    @Test
    public void generateHandle() {
        
        final UUID handleUuid = UUID.randomUUID();
        final String expectedHandle = prefixWithSlash + handleUuid.toString().toUpperCase();
        
        stub(method(UUID.class, "randomUUID")).toReturn(handleUuid);
        
        String retrievedHandle = handleInfoRetriever.generateUuidHandle();
        
        assertEquals("Retrieved handle different from expected", expectedHandle, retrievedHandle);
    }

    @Test
    public void handleIsValidUuidWithHandlePrefix() {
        
        final UUID handleUuid = UUID.randomUUID();
        final String handleWithValidUuid = handleLongPrefix + handleUuid.toString().toUpperCase();
        
        boolean result = handleInfoRetriever.handleIsValidUuid(handleWithValidUuid);
        
        assertTrue("Result should be true", result);
    }
    
    @Test
    public void handleIsValidUuidWithAltPrefix() {
        
        final UUID handleUuid = UUID.randomUUID();
        final String handleWithValidUuid = handleShortPrefix + handleUuid.toString().toUpperCase();
        
        boolean result = handleInfoRetriever.handleIsValidUuid(handleWithValidUuid);
        
        assertTrue("Result should be true", result);
    }
    
    @Test
    public void handleIsInvalidUuid() {
        
        final String handleWithInvalidUuid = handleLongPrefix + "123";
        
        boolean result = handleInfoRetriever.handleIsValidUuid(handleWithInvalidUuid);
        
        assertFalse("Result should be false", result);
    }
}