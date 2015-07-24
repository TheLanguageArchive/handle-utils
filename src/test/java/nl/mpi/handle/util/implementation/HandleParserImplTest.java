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

import java.net.URI;
import java.util.UUID;
import nl.mpi.handle.util.HandleParser;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author guisil
 */
public class HandleParserImplTest {
    
    private HandleParser handleParser;
    
    private final String prefix = "11142";
    private final String prefixWithSlash = prefix + "/";
    private final String handleShortPrefix = HandleConstants.HDL_SHORT_PROXY + prefixWithSlash;
    private final String handleLongPrefix = HandleConstants.HDL_LONG_PROXY + prefixWithSlash;;
    
    private final String someOtherPrefix = "98765";
    private final String someOtherPrefixWithSlash = someOtherPrefix + "/";
    private final String handleSomeOtherShortPrefix = HandleConstants.HDL_SHORT_PROXY + someOtherPrefixWithSlash;
    private final String handleSomeOtherLongPrefix = HandleConstants.HDL_LONG_PROXY + someOtherPrefixWithSlash;
    
    public HandleParserImplTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        handleParser = new HandleParserImpl(prefix);
    }
    
    @After
    public void tearDown() {
    }
    
    
    @Test
    public void startsWithKnownHandleProxy() {
        
        URI uri = URI.create(HandleConstants.HDL_SHORT_PROXY + "12345/" + UUID.randomUUID().toString());
        boolean result = handleParser.startsWithKnownHandleProxy(uri);
        assertTrue("Result should be true", result);
        
        uri = URI.create(HandleConstants.HDL_LONG_PROXY + "12345/" + UUID.randomUUID().toString());
        result = handleParser.startsWithKnownHandleProxy(uri);
        assertTrue("Result should be true", result);
    }
    
    @Test
    public void doesNotStartWithKnownHandleProxy() {
        
        URI uri = URI.create("12345/" + UUID.randomUUID().toString());
        boolean result = handleParser.startsWithKnownHandleProxy(uri);
        assertFalse("Result should be false", result);
        
        uri = URI.create("http://some/other/url.cmdi");
        result = handleParser.startsWithKnownHandleProxy(uri);
        assertFalse("Result should be false", result);
        
        uri = null;
        result = handleParser.startsWithKnownHandleProxy(uri);
        assertFalse("Result should be false", result);
        
        uri = URI.create("");
        result = handleParser.startsWithKnownHandleProxy(uri);
        assertFalse("Result should be false", result);
    }

    @Test
    public void handlePrefixIsKnown() {
        
        String handleUuid = UUID.randomUUID().toString();
        String fullHandle = handleShortPrefix + handleUuid;
        URI handleUri = URI.create(fullHandle);
        
        boolean result = handleParser.isHandlePrefixKnown(handleUri);
        assertTrue("Result should be true (1)", result);
        
        
        handleUuid = UUID.randomUUID().toString();
        fullHandle = handleLongPrefix + handleUuid;
        handleUri = URI.create(fullHandle);
        
        result = handleParser.isHandlePrefixKnown(handleUri);
        assertTrue("Result should be true (2)", result);
        
        
        handleUuid = UUID.randomUUID().toString();
        fullHandle = prefixWithSlash + handleUuid;
        handleUri = URI.create(fullHandle);
        
        result = handleParser.isHandlePrefixKnown(handleUri);
        assertTrue("Result should be true (3)", result);
    }
    
    @Test
    public void handlePrefixIsUnknown() {
        
        String handleUuid = UUID.randomUUID().toString();
        String fullHandle = handleSomeOtherShortPrefix + handleUuid;
        URI handleUri = URI.create(fullHandle);
        
        boolean result = handleParser.isHandlePrefixKnown(handleUri);
        assertFalse("Result should be false (3)", result);
        
        
        handleUuid = UUID.randomUUID().toString();
        fullHandle = handleSomeOtherLongPrefix + handleUuid;
        handleUri = URI.create(fullHandle);
        
        result = handleParser.isHandlePrefixKnown(handleUri);
        assertFalse("Result should be false (2)", result);
        
        
        handleUuid = UUID.randomUUID().toString();
        fullHandle = someOtherPrefixWithSlash + handleUuid;
        handleUri = URI.create(fullHandle);
        
        result = handleParser.isHandlePrefixKnown(handleUri);
        assertFalse("Result should be false (3)", result);
    }
    
    @Test
    public void handleIsEmpty() {
        
        try {
            handleParser.isHandlePrefixKnown(URI.create(""));
            fail("should have thrown exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", "Invalid handle", ex.getMessage());
        }
    }
    
    @Test
    public void handleIsNull() {
        
        try {
            handleParser.isHandlePrefixKnown(null);
            fail("should have thrown exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", "Invalid handle", ex.getMessage());
        }
    }
    
    @Test
    public void handlesEqual() {
        
        final String baseHandle = "00-0A0A1B1B-2C2C-3D3D-4E4E-4E5FF5F6G7G9";
        final URI firstHandle = URI.create(HandleConstants.HDL_LONG_PROXY + prefix + "/" + baseHandle);
        final URI secondHandle = firstHandle;
        
        boolean result = handleParser.areHandlesEquivalent(firstHandle, secondHandle);
        
        assertTrue("Result should have been true", result);
    }
    
    @Test
    public void handlesDifferent() {
        
        final String firstBaseHandle = "00-0A0A1B1B-2C2C-3D3D-4E4E-4E5FF5F6G7G9";
        final String secondBaseHandle = "00-0B0B1C1C-2D2D-3E3E-4F4F-4F5GG5G6H7H9";
        final URI firstHandle = URI.create(HandleConstants.HDL_LONG_PROXY + prefix + "/" + firstBaseHandle);
        final URI secondHandle = URI.create(HandleConstants.HDL_LONG_PROXY + prefix + "/" + secondBaseHandle);
        
        boolean result = handleParser.areHandlesEquivalent(firstHandle, secondHandle);
        
        assertFalse("Result should have been false", result);
    }
    
    @Test
    public void handlesDifferentOneIsEmpty() {
        
        String baseHandle = "/00-0A0A1B1B-2C2C-3D3D-4E4E-4E5FF5F6G7G9";
        URI firstHandle = null;
        URI secondHandle = URI.create(HandleConstants.HDL_SHORT_PROXY + prefix + "/" + baseHandle);
        
        try {
            handleParser.areHandlesEquivalent(firstHandle, secondHandle);
            fail("should have thrown an exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", "Invalid handle(s)", ex.getMessage());
        }
    }
    
    @Test
    public void handlesDifferentOneIsNull() {
        
        String baseHandle = "/00-0A0A1B1B-2C2C-3D3D-4E4E-4E5FF5F6G7G9";
        URI firstHandle = URI.create(HandleConstants.HDL_LONG_PROXY + prefix + "/" + baseHandle);
        URI secondHandle = null;
        
        try {
            handleParser.areHandlesEquivalent(firstHandle, secondHandle);
            fail("should have thrown an exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", "Invalid handle(s)", ex.getMessage());
        }
    }
    
    @Test
    public void prepareHandleContainingNoPrefix() {
        
        final String strippedHandle = "00-0A0A1B1B-2C2C-3D3D-4E4E-4E5FF5F6G7G9";
        final URI initialHandle = URI.create(strippedHandle);
        final URI expectedHandle = URI.create(HandleConstants.HDL_SHORT_PROXY + prefix + "/" + strippedHandle);
        
        URI retrievedHandle = handleParser.prepareHandleWithHdlPrefix(initialHandle);
        
        assertEquals("Retrieved handle different from expected", expectedHandle, retrievedHandle);
    }
    
    @Test
    public void prepareHandleContainingJustPrefix() {
        
        final String strippedHandle = "00-0A0A1B1B-2C2C-3D3D-4E4E-4E5FF5F6G7G9";
        final URI initialHandle = URI.create(prefix + "/" + strippedHandle);
        final URI expectedHandle = URI.create(HandleConstants.HDL_SHORT_PROXY + prefix + "/" + strippedHandle);
        
        URI retrievedHandle = handleParser.prepareHandleWithHdlPrefix(initialHandle);
        
        assertEquals("Retrieved handle different from expected", expectedHandle, retrievedHandle);
    }
    
    @Test
    public void prepareHandleContainingHdlPrefix() {
        
        final String strippedHandle = "00-0A0A1B1B-2C2C-3D3D-4E4E-4E5FF5F6G7G9";
        final URI initialHandle = URI.create(HandleConstants.HDL_SHORT_PROXY + prefix + "/" + strippedHandle);
        final URI expectedHandle = initialHandle;
        
        URI retrievedHandle = handleParser.prepareHandleWithHdlPrefix(initialHandle);
        
        assertEquals("Retrieved handle different from expected", expectedHandle, retrievedHandle);
    }
    
    @Test
    public void prepareHandleContainingFullPrefix() {
        
        final String strippedHandle = "00-0A0A1B1B-2C2C-3D3D-4E4E-4E5FF5F6G7G9";
        final URI initialHandle = URI.create(HandleConstants.HDL_LONG_PROXY + prefix + "/" + strippedHandle);
        final URI expectedHandle = URI.create(HandleConstants.HDL_SHORT_PROXY + prefix + "/" + strippedHandle);
        
        URI retrievedHandle = handleParser.prepareHandleWithHdlPrefix(initialHandle);
        
        assertEquals("Retrieved handle different from expected", expectedHandle, retrievedHandle);
    }
    
    @Test
    public void stripHandleStartingWithHandleLongPrefix() {
        
        final String handleUuid = UUID.randomUUID().toString();
        final String fullHandle = handleLongPrefix + handleUuid;
        
        String result = handleParser.stripHandle(fullHandle);
        
        assertEquals("Stripped handle different from expected", handleUuid, result);
    }
    
    @Test
    public void stripHandleStartingWithHandleShortPrefix() {
        
        final String handleUuid = UUID.randomUUID().toString();
        final String fullHandle = handleShortPrefix + handleUuid;
        
        String result = handleParser.stripHandle(fullHandle);
        
        assertEquals("Stripped handle different from expected", handleUuid, result);
    }
    
    @Test
    public void stripHandleStartingWithJustPrefix() {
        
        final String handleUuid = UUID.randomUUID().toString();
        final String fullHandle = prefixWithSlash + handleUuid;
        
        String result = handleParser.stripHandle(fullHandle);
        
        assertEquals("Stripped handle different from expected", handleUuid, result);
    }
    
    @Test
    public void stripHandleStartingWithHandleSomeOtherLongPrefix() {
        
        // since the handle starts with an unknown prefix (but with a valid proxy), it doesn't strip the prefix,
        //  otherwise it could match one of "our" handles
        
        final String handleUuid = UUID.randomUUID().toString();
        final String handleWithJustPrefix = someOtherPrefixWithSlash + handleUuid;
        final String fullHandle = handleSomeOtherLongPrefix + handleUuid;
        
        String result = handleParser.stripHandle(fullHandle);
        
        assertEquals("Stripped handle different from expected", handleWithJustPrefix, result);
    }
    
    @Test
    public void stripHandleStartingWithHandleSomeOtherShortPrefix() {
        
        // since the handle starts with an unknown prefix (but with a valid proxy), it doesn't strip the prefix,
        //  otherwise it could match one of "our" handles
        
        final String handleUuid = UUID.randomUUID().toString();
        final String handleWithJustPrefix = someOtherPrefixWithSlash + handleUuid;
        final String fullHandle = handleSomeOtherShortPrefix + handleUuid;
        
        String result = handleParser.stripHandle(fullHandle);
        
        assertEquals("Stripped handle different from expected", handleWithJustPrefix, result);
    }
    
    @Test
    public void stripHandleStartingWithJustSomeOtherPrefix() {
        
        // since the handle starts with an unknown prefix (but with a valid proxy), it doesn't strip the prefix,
        //  otherwise it could match one of "our" handles
        
        final String handleUuid = UUID.randomUUID().toString();
        final String fullHandle = "blabla/" + handleUuid;
        
        String result = handleParser.stripHandle(fullHandle);
        
        assertEquals("Stripped handle different from expected", fullHandle, result);
    }
}
