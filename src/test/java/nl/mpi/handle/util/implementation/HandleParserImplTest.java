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
import java.util.ArrayList;
import java.util.Collection;
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
    private final String handleShortPrefix = HandleConstants.HDL_SHORT_PROXY + ":" + prefixWithSlash;
    private final String handleLongPrefix = HandleConstants.HDL_LONG_PROXY + prefixWithSlash;;
    
    private final String someOtherPrefix = "98765";
    private final String someOtherPrefixWithSlash = someOtherPrefix + "/";
    private final String handleSomeOtherShortPrefix = HandleConstants.HDL_SHORT_PROXY + ":" + someOtherPrefixWithSlash;
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
    public void isHandle() {
        
        Collection<URI> handles = new ArrayList<>();
        handles.add(URI.create(handleShortPrefix + UUID.randomUUID().toString()));
        handles.add(URI.create(handleLongPrefix + UUID.randomUUID().toString()));
        handles.add(URI.create(handleSomeOtherShortPrefix + UUID.randomUUID().toString()));
        handles.add(URI.create(handleSomeOtherLongPrefix + UUID.randomUUID().toString()));
        handles.add(URI.create(prefixWithSlash + UUID.randomUUID().toString()));
        handles.add(URI.create(someOtherPrefixWithSlash + UUID.randomUUID().toString()));
        
        for(URI handle : handles) {
            boolean result = handleParser.isHandleUri(handle);
            assertTrue("Result should be true (" + handle + ")", result);
        }
    }
    
    @Test
    public void isNotHandle() {
        
        Collection<URI> notHandles = new ArrayList<>();
        notHandles.add(URI.create(UUID.randomUUID().toString()));
        notHandles.add(URI.create("/" + UUID.randomUUID().toString()));
        notHandles.add(URI.create("http://some/other/url.cmdi"));
        notHandles.add(URI.create(""));
        notHandles.add(null);
        
        for(URI notHandle : notHandles) {
            boolean result = handleParser.isHandleUri(notHandle);
            assertFalse("Result should be true (" + notHandle + ")", result);
        }
    }
    
    @Test
    public void startsWithKnownHandleProxy() {
        
        URI uri = URI.create(handleShortPrefix + UUID.randomUUID().toString());
        boolean result = handleParser.startsWithKnownHandleProxy(uri);
        assertTrue("Result should be true (short proxy)", result);
        
        uri = URI.create(handleSomeOtherLongPrefix + UUID.randomUUID().toString());
        result = handleParser.startsWithKnownHandleProxy(uri);
        assertTrue("Result should be true (long proxy)", result);
    }
    
    @Test
    public void doesNotStartWithKnownHandleProxy() {
        
        URI uri = URI.create(someOtherPrefixWithSlash + UUID.randomUUID().toString());
        boolean result = handleParser.startsWithKnownHandleProxy(uri);
        assertFalse("Result should be false (no proxy)", result);
        
        uri = URI.create("http://some/other/url.cmdi");
        result = handleParser.startsWithKnownHandleProxy(uri);
        assertFalse("Result should be false (not a handle)", result);
        
        uri = null;
        result = handleParser.startsWithKnownHandleProxy(uri);
        assertFalse("Result should be false (null)", result);
        
        uri = URI.create("");
        result = handleParser.startsWithKnownHandleProxy(uri);
        assertFalse("Result should be false (empty)", result);
    }

    @Test
    public void handlePrefixIsKnown() {
        
        String handleUuid = UUID.randomUUID().toString();
        String fullHandle = handleShortPrefix + handleUuid;
        URI handleUri = URI.create(fullHandle);
        
        boolean result = handleParser.isHandlePrefixKnown(handleUri);
        assertTrue("Result should be true (1)", result);
        
        
        fullHandle = handleLongPrefix + handleUuid;
        handleUri = URI.create(fullHandle);
        
        result = handleParser.isHandlePrefixKnown(handleUri);
        assertTrue("Result should be true (2)", result);
        
        
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
        
        
        fullHandle = handleSomeOtherLongPrefix + handleUuid;
        handleUri = URI.create(fullHandle);
        
        result = handleParser.isHandlePrefixKnown(handleUri);
        assertFalse("Result should be false (2)", result);
        
        
        fullHandle = someOtherPrefixWithSlash + handleUuid;
        handleUri = URI.create(fullHandle);
        
        result = handleParser.isHandlePrefixKnown(handleUri);
        assertFalse("Result should be false (3)", result);
    }
    
    @Test
    public void handleHasNoPrefix() {
        String handleUuid = UUID.randomUUID().toString();
        URI handle = URI.create(handleUuid);
        String expectedExceptionMessage = "Invalid handle (" + handle + ")";
        try {
            handleParser.isHandlePrefixKnown(handle);
            fail("should have thrown exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
    }
    
    @Test
    public void handleIsEmpty() {
        
        URI emptyHandle = URI.create("");
        String expectedExceptionMessage = "Invalid handle (" + emptyHandle + ")";
        try {
            handleParser.isHandlePrefixKnown(emptyHandle);
            fail("should have thrown exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
    }
    
    @Test
    public void handleIsNull() {
        
        String expectedExceptionMessage = "Invalid handle (" + null + ")";
        try {
            handleParser.isHandlePrefixKnown(null);
            fail("should have thrown exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
    }
    
    @Test
    public void handlesEqual() {
        
        final String baseHandle = "00-0A0A1B1B-2C2C-3D3D-4E4E-4E5FF5F6G7G9";
        final URI firstHandle = URI.create(handleLongPrefix + baseHandle);
        final URI secondHandle = firstHandle;
        
        boolean result = handleParser.areHandlesEquivalent(firstHandle, secondHandle);
        
        assertTrue("Result should have been true", result);
    }
    
    @Test
    public void handlesEqual_DifferentProxy() {
        
        final String baseHandle = "00-0A0A1B1B-2C2C-3D3D-4E4E-4E5FF5F6G7G9";
        final URI firstHandle = URI.create(handleLongPrefix + baseHandle);
        final URI secondHandle = URI.create(handleShortPrefix + baseHandle);
        
        boolean result = handleParser.areHandlesEquivalent(firstHandle, secondHandle);
        
        assertTrue("Result should have been true", result);
    }
    
    @Test
    public void handlesDifferent() {
        
        final String firstBaseHandle = "00-0A0A1B1B-2C2C-3D3D-4E4E-4E5FF5F6G7G9";
        final String secondBaseHandle = "00-0B0B1C1C-2D2D-3E3E-4F4F-4F5GG5G6H7H9";
        final URI firstHandle = URI.create(handleLongPrefix + firstBaseHandle);
        final URI secondHandle = URI.create(handleLongPrefix + secondBaseHandle);
        
        boolean result = handleParser.areHandlesEquivalent(firstHandle, secondHandle);
        
        assertFalse("Result should have been false", result);
    }
    
    @Test
    public void handlesSimilar_DifferentPrefixes() {
        
        final String firstBaseHandle = "00-0A0A1B1B-2C2C-3D3D-4E4E-4E5FF5F6G7G9";
        final URI firstHandle = URI.create(handleShortPrefix + firstBaseHandle);
        final URI secondHandle = URI.create(handleSomeOtherShortPrefix + firstBaseHandle);
        
        boolean result = handleParser.areHandlesEquivalent(firstHandle, secondHandle);
        
        assertFalse("Result should have been false", result);
    }
    
    @Test
    public void handlesSimilar_OneIsInvalid() {
        
        final String firstBaseHandle = "00-0A0A1B1B-2C2C-3D3D-4E4E-4E5FF5F6G7G9";
        final URI firstHandle = URI.create(handleShortPrefix + firstBaseHandle);
        final URI secondHandle = URI.create(firstBaseHandle);
        final String expectedExceptionMessage = "Invalid handle (" + secondHandle + ")";
        
        try {
            handleParser.areHandlesEquivalent(firstHandle, secondHandle);
            fail("should have thrown an exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
    }
    
    @Test
    public void handlesDifferentOneIsEmpty() {
        
        final String baseHandle = "/00-0A0A1B1B-2C2C-3D3D-4E4E-4E5FF5F6G7G9";
        final URI firstHandle = URI.create("");
        final URI secondHandle = URI.create(handleShortPrefix + baseHandle);
        final String expectedExceptionMessage = "Invalid handle (" + firstHandle + ")";
        
        try {
            handleParser.areHandlesEquivalent(firstHandle, secondHandle);
            fail("should have thrown an exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
    }
    
    @Test
    public void handlesDifferentOneIsNull() {
        
        final String baseHandle = "00-0A0A1B1B-2C2C-3D3D-4E4E-4E5FF5F6G7G9";
        final URI firstHandle = URI.create(handleLongPrefix + baseHandle);
        final URI secondHandle = null;
        final String expectedExceptionMessage = "Invalid handle (" + secondHandle + ")";
        
        try {
            handleParser.areHandlesEquivalent(firstHandle, secondHandle);
            fail("should have thrown an exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
    }
    
    @Test
    public void prepareEmptyOrNullHandle() {
        
        // empty handle
        
        URI emptyHandle = URI.create("");
        String expectedExceptionMessage = "Invalid handle (" + emptyHandle + ")";
        
        try {
            handleParser.prepareHandleWithHdlPrefix(emptyHandle);
            fail("should have thrown an exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
        
        // null handle
        
        expectedExceptionMessage = "Invalid handle (" + null + ")";
        
        try {
            handleParser.prepareHandleWithHdlPrefix(null);
            fail("should have thrown an exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
    }
    
    @Test
    public void prepareHandleContainingNoPrefix() {
        
        // without prefix
        
        final String strippedHandle = "00-0A0A1B1B-2C2C-3D3D-4E4E-4E5FF5F6G7G9";
        URI initialHandle = URI.create(strippedHandle);
        String expectedExceptionMessage = "Invalid handle (" + initialHandle + ")";
        
        try {
            handleParser.prepareHandleWithHdlPrefix(initialHandle);
            fail("should have thrown an exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
        
        // with just a slash in the beginning
        
        initialHandle = URI.create("/" + strippedHandle);
        expectedExceptionMessage = "Invalid handle (" + initialHandle + ")";
        
        try {
            handleParser.prepareHandleWithHdlPrefix(initialHandle);
            fail("should have thrown an exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
    }
    
    @Test
    public void prepareHandleContainingJustPrefix() {
        
        final String strippedHandle = "00-0A0A1B1B-2C2C-3D3D-4E4E-4E5FF5F6G7G9";
        URI initialHandle = URI.create(prefixWithSlash + strippedHandle);
        URI expectedHandle = URI.create(handleShortPrefix + strippedHandle);
        
        URI retrievedHandle = handleParser.prepareHandleWithHdlPrefix(initialHandle);
        
        assertEquals("Retrieved handle different from expected (normal prefix)", expectedHandle, retrievedHandle);
        
        // with a different prefix
        
        initialHandle = URI.create(someOtherPrefixWithSlash + strippedHandle);
        expectedHandle = URI.create(handleSomeOtherShortPrefix + strippedHandle);
        
        retrievedHandle = handleParser.prepareHandleWithHdlPrefix(initialHandle);
        
        assertEquals("Retrieved handle different from expected (some other prefix)", expectedHandle, retrievedHandle);
    }
    
    @Test
    public void prepareHandleContainingHdlPrefix() {
        
        final String strippedHandle = "00-0A0A1B1B-2C2C-3D3D-4E4E-4E5FF5F6G7G9";
        URI initialHandle = URI.create(handleShortPrefix + strippedHandle);
        URI expectedHandle = initialHandle;
        
        URI retrievedHandle = handleParser.prepareHandleWithHdlPrefix(initialHandle);
        
        assertEquals("Retrieved handle different from expected (normal prefix)", expectedHandle, retrievedHandle);
        
        // with a different prefix
        
        initialHandle = URI.create(handleSomeOtherShortPrefix + strippedHandle);
        expectedHandle = initialHandle;
        
        retrievedHandle = handleParser.prepareHandleWithHdlPrefix(initialHandle);
        
        assertEquals("Retrieved handle different from expected (some other prefix)", expectedHandle, retrievedHandle);
    }
    
    @Test
    public void prepareHandleContainingFullPrefix() {
        
        final String strippedHandle = "00-0A0A1B1B-2C2C-3D3D-4E4E-4E5FF5F6G7G9";
        URI initialHandle = URI.create(handleLongPrefix + strippedHandle);
        URI expectedHandle = URI.create(handleShortPrefix + strippedHandle);
        
        URI retrievedHandle = handleParser.prepareHandleWithHdlPrefix(initialHandle);
        
        assertEquals("Retrieved handle different from expected (normal prefix)", expectedHandle, retrievedHandle);
        
        // with a different prefix
        
        initialHandle = URI.create(handleSomeOtherLongPrefix + strippedHandle);
        expectedHandle = URI.create(handleSomeOtherShortPrefix + strippedHandle);
        
        retrievedHandle = handleParser.prepareHandleWithHdlPrefix(initialHandle);
        
        assertEquals("Retrieved handle different from expected (some other prefix)", expectedHandle, retrievedHandle);
    }
    
    @Test
    public void prepareNotAHandle() {
        
        final URI notAHandle = URI.create("https://some/location/node.cmdi");
        final String expectedExceptionMessage = "Invalid handle (" + notAHandle + ")";
        
        try {
            handleParser.prepareHandleWithHdlPrefix(notAHandle);
            fail("should have thrown an exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
    }
    
    @Test
    public void stripEmptyOrNullHandle() {
        
        // empty handle
        
        final URI emptyHandle = URI.create("");
        String expectedExceptionMessage = "Invalid handle (" + emptyHandle + ")";
        
        try {
            handleParser.stripHandleIfPrefixIsKnown(emptyHandle);
            fail("should have thrown an exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }

        // null handle
        expectedExceptionMessage = "Invalid handle (" + null + ")";
        
        try {
            handleParser.stripHandleIfPrefixIsKnown(null);
            fail("should have thrown an exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
    }
    
    @Test
    public void stripHandleStartingWithHandleLongPrefix() {
        
        final String handleUuid = UUID.randomUUID().toString();
        final URI fullHandle = URI.create(handleLongPrefix + handleUuid);
        
        String result = handleParser.stripHandleIfPrefixIsKnown(fullHandle);
        
        assertEquals("Stripped handle different from expected", handleUuid, result);
    }
    
    @Test
    public void stripHandleStartingWithHandleShortPrefix() {
        
        final String handleUuid = UUID.randomUUID().toString();
        final URI fullHandle = URI.create(handleShortPrefix + handleUuid);
        
        String result = handleParser.stripHandleIfPrefixIsKnown(fullHandle);
        
        assertEquals("Stripped handle different from expected", handleUuid, result);
    }
    
    @Test
    public void stripHandleStartingWithJustPrefix() {
        
        final String handleUuid = UUID.randomUUID().toString();
        final URI fullHandle = URI.create(prefixWithSlash + handleUuid);
        
        String result = handleParser.stripHandleIfPrefixIsKnown(fullHandle);
        
        assertEquals("Stripped handle different from expected", handleUuid, result);
    }
    
    @Test
    public void stripHandleWithoutPrefix() {
        
        // without prefix
        
        final String handleUuid = UUID.randomUUID().toString();
        final URI handle = URI.create(handleUuid);
        String expectedExceptionMessage = "Invalid handle (" + handle + ")";
        
        try {
            handleParser.stripHandleIfPrefixIsKnown(handle);
            fail("should have thrown an exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
        
        // without prefix and starting with a slash
        
        final URI handleWithJustSlash = URI.create("/" + handleUuid);
        expectedExceptionMessage = "Invalid handle (" + handleWithJustSlash + ")";
        
        try {
            handleParser.stripHandleIfPrefixIsKnown(handleWithJustSlash);
            fail("should have thrown an exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
    }
    
    @Test
    public void stripHandleStartingWithHandleSomeOtherLongPrefix() {
        
        // since the handle starts with an unknown prefix (but with a valid proxy), it doesn't strip the prefix,
        //  otherwise it could match one of "our" handles
        
        final String handleUuid = UUID.randomUUID().toString();
        final String handleWithJustPrefix = someOtherPrefixWithSlash + handleUuid;
        final URI fullHandle = URI.create(handleSomeOtherLongPrefix + handleUuid);
        
        String result = handleParser.stripHandleIfPrefixIsKnown(fullHandle);
        
        assertEquals("Stripped handle different from expected", handleWithJustPrefix, result);
    }
    
    @Test
    public void stripHandleStartingWithHandleSomeOtherShortPrefix() {
        
        // since the handle starts with an unknown prefix (but with a valid proxy), it doesn't strip the prefix,
        //  otherwise it could match one of "our" handles
        
        final String handleUuid = UUID.randomUUID().toString();
        final String handleWithJustPrefix = someOtherPrefixWithSlash + handleUuid;
        final URI fullHandle = URI.create(handleSomeOtherShortPrefix + handleUuid);
        
        String result = handleParser.stripHandleIfPrefixIsKnown(fullHandle);
        
        assertEquals("Stripped handle different from expected", handleWithJustPrefix, result);
    }
    
    @Test
    public void stripHandleStartingWithJustSomeOtherPrefix() {
        
        // since the handle starts with an unknown prefix (but with a valid proxy), it doesn't strip the prefix,
        //  otherwise it could match one of "our" handles
        
        final String handleUuid = UUID.randomUUID().toString();
        final URI fullHandle = URI.create("blabla/" + handleUuid);
        
        String result = handleParser.stripHandleIfPrefixIsKnown(fullHandle);
        
        assertEquals("Stripped handle different from expected", fullHandle.toString(), result);
    }
}
