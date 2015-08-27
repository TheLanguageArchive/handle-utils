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
        handles.add(URI.create(prefixWithSlash + UUID.randomUUID().toString()));
        
        for(URI handle : handles) {
            boolean result = handleParser.isHandleUriWithKnownPrefix(handle);
            assertTrue("Result should be true (" + handle + ")", result);
        }
    }
    
    @Test
    public void isNotHandle() {
        
        Collection<URI> notHandles = new ArrayList<>();
        
        notHandles.add(URI.create(handleSomeOtherShortPrefix + UUID.randomUUID().toString()));
        notHandles.add(URI.create(handleSomeOtherLongPrefix + UUID.randomUUID().toString()));
        notHandles.add(URI.create(someOtherPrefixWithSlash + UUID.randomUUID().toString()));
        
        notHandles.add(URI.create(UUID.randomUUID().toString()));
        notHandles.add(URI.create("/" + UUID.randomUUID().toString()));
        notHandles.add(URI.create("http://some/other/url.cmdi"));
        notHandles.add(URI.create(""));
        notHandles.add(null);
        
        for(URI notHandle : notHandles) {
            boolean result = handleParser.isHandleUriWithKnownPrefix(notHandle);
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
        final String expectedExceptionMessage = "Invalid handle (" + secondHandle + ")";
        
        try {
            handleParser.areHandlesEquivalent(firstHandle, secondHandle);
            fail("should have thrown an exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
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
            fail("should have thrown an exception (empty handle - 1)");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
        
        // null handle
        
        expectedExceptionMessage = "Invalid handle (" + null + ")";
        
        try {
            handleParser.prepareHandleWithHdlPrefix(null);
            fail("should have thrown an exception (null handle - 1)");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
        

        // PREPARE AND VALIDATE

        // empty handle
        
        expectedExceptionMessage = "Invalid handle (" + emptyHandle + ")";
        
        try {
            handleParser.prepareAndValidateHandleWithHdlPrefix(emptyHandle);
            fail("should have thrown an exception (empty handle - 2)");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
        
        // null handle
        
        expectedExceptionMessage = "Invalid handle (" + null + ")";
        
        try {
            handleParser.prepareAndValidateHandleWithHdlPrefix(null);
            fail("should have thrown an exception (null handle - 2)");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
    }
    
    @Test
    public void prepareHandleContainingNoPrefix() {
        
        // without prefix
        
        String strippedHandle = "00-0A0A1B1B-2C2C-3D3D-4E4E-4E5FF5F6G7G9";
        URI initialHandle = URI.create(strippedHandle);
        String expectedExceptionMessage = "Invalid handle (" + initialHandle + ")";
        
        try {
            handleParser.prepareHandleWithHdlPrefix(initialHandle);
            fail("should have thrown an exception (no prefix - 1)");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
        
        // with just a slash in the beginning
        
        initialHandle = URI.create("/" + strippedHandle);
        expectedExceptionMessage = "Invalid handle (" + initialHandle + ")";
        
        try {
            handleParser.prepareHandleWithHdlPrefix(initialHandle);
            fail("should have thrown an exception (slash - 1)");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
        
        
        //PREPARE AND VALIDATE
        
        // without prefix
        
        initialHandle = URI.create(strippedHandle);
        expectedExceptionMessage = "Invalid handle (" + initialHandle + ")";
        
        try {
            handleParser.prepareAndValidateHandleWithHdlPrefix(initialHandle);
            fail("should have thrown an exception (no prefix - 2)");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
        
        // with just a slash in the beginning
        
        initialHandle = URI.create("/" + strippedHandle);
        expectedExceptionMessage = "Invalid handle (" + initialHandle + ")";
        
        try {
            handleParser.prepareAndValidateHandleWithHdlPrefix(initialHandle);
            fail("should have thrown an exception (slash - 2)");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
    }
    
    @Test
    public void prepareHandleContainingJustPrefix() {
        
        String strippedHandle = "00-0A0A1B1B-2C2C-3D3D-4E4E-4E5FF5F6G7G9";
        URI initialHandle = URI.create(prefixWithSlash + strippedHandle);
        URI expectedHandle = URI.create(handleShortPrefix + strippedHandle);
        
        URI retrievedHandle = handleParser.prepareHandleWithHdlPrefix(initialHandle);
        
        assertEquals("Retrieved handle different from expected (normal prefix)", expectedHandle, retrievedHandle);
        
        // with a different prefix
        
        initialHandle = URI.create(someOtherPrefixWithSlash + strippedHandle);
        expectedHandle = URI.create(handleSomeOtherShortPrefix + strippedHandle);
        
        retrievedHandle = handleParser.prepareHandleWithHdlPrefix(initialHandle);
        
        assertEquals("Retrieved handle different from expected (normal prefix)", expectedHandle, retrievedHandle);
        
        
        // PREPARE AND VALIDATE
        
        initialHandle = URI.create(prefixWithSlash + strippedHandle);
        expectedHandle = URI.create(handleShortPrefix + strippedHandle);
        
        retrievedHandle = handleParser.prepareAndValidateHandleWithHdlPrefix(initialHandle);
        
        assertEquals("Retrieved handle different from expected (normal prefix)", expectedHandle, retrievedHandle);
        
        // with a different prefix
        
        initialHandle = URI.create(someOtherPrefixWithSlash + strippedHandle);
        final String expectedExceptionMessage = "Invalid handle (" + initialHandle + ")";
        
        try {
            handleParser.prepareAndValidateHandleWithHdlPrefix(initialHandle);
            fail("should have thrown an exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
    }
    
    @Test
    public void prepareHandleContainingHdlPrefix() {
        
        String strippedHandle = "00-0A0A1B1B-2C2C-3D3D-4E4E-4E5FF5F6G7G9";
        URI initialHandle = URI.create(handleShortPrefix + strippedHandle);
        URI expectedHandle = initialHandle;
        
        URI retrievedHandle = handleParser.prepareHandleWithHdlPrefix(initialHandle);
        
        assertEquals("Retrieved handle different from expected (normal prefix)", expectedHandle, retrievedHandle);
        
        // with a different prefix
        
        initialHandle = URI.create(handleSomeOtherShortPrefix + strippedHandle);
        expectedHandle = initialHandle;
        
        retrievedHandle = handleParser.prepareHandleWithHdlPrefix(initialHandle);
        
        assertEquals("Retrieved handle different from expected (normal prefix)", expectedHandle, retrievedHandle);
        
        
        // PREPARE AND VALIDATE
        
        initialHandle = URI.create(handleShortPrefix + strippedHandle);
        expectedHandle = initialHandle;
        
        retrievedHandle = handleParser.prepareAndValidateHandleWithHdlPrefix(initialHandle);
        
        assertEquals("Retrieved handle different from expected (normal prefix)", expectedHandle, retrievedHandle);
        
        // with a different prefix
        
        initialHandle = URI.create(handleSomeOtherShortPrefix + strippedHandle);
        final String expectedExceptionMessage = "Invalid handle (" + initialHandle + ")";
        
        try {
            handleParser.prepareAndValidateHandleWithHdlPrefix(initialHandle);
            fail("should have thrown an exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
    }
    
    @Test
    public void prepareHandleContainingFullPrefix() {
        
        String strippedHandle = "00-0A0A1B1B-2C2C-3D3D-4E4E-4E5FF5F6G7G9";
        URI initialHandle = URI.create(handleLongPrefix + strippedHandle);
        URI expectedHandle = URI.create(handleShortPrefix + strippedHandle);
        
        URI retrievedHandle = handleParser.prepareHandleWithHdlPrefix(initialHandle);
        
        assertEquals("Retrieved handle different from expected (normal prefix)", expectedHandle, retrievedHandle);
        
        // with a different prefix
        
        initialHandle = URI.create(handleSomeOtherLongPrefix + strippedHandle);
        expectedHandle = URI.create(handleSomeOtherShortPrefix + strippedHandle);
        
        retrievedHandle = handleParser.prepareHandleWithHdlPrefix(initialHandle);
        
        assertEquals("Retrieved handle different from expected (normal prefix)", expectedHandle, retrievedHandle);
        
        
        // PREPARE AND VALIDATE
        
        initialHandle = URI.create(handleLongPrefix + strippedHandle);
        expectedHandle = URI.create(handleShortPrefix + strippedHandle);
        
        retrievedHandle = handleParser.prepareAndValidateHandleWithHdlPrefix(initialHandle);
        
        assertEquals("Retrieved handle different from expected (normal prefix)", expectedHandle, retrievedHandle);
        
        // with a different prefix
        
        initialHandle = URI.create(handleSomeOtherLongPrefix + strippedHandle);
        final String expectedExceptionMessage = "Invalid handle (" + initialHandle + ")";
        
        try {
            handleParser.prepareAndValidateHandleWithHdlPrefix(initialHandle);
            fail("should have thrown an exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
    }
    
    @Test
    public void prepareNotAHandle() {
        
        URI notAHandle = URI.create("https://some/location/node.cmdi");
        String expectedExceptionMessage = "Invalid handle (" + notAHandle + ")";
        
        try {
            handleParser.prepareHandleWithHdlPrefix(notAHandle);
            fail("should have thrown an exception (not a handle - 1)");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
        
        
        // PREPARE AND VALIDATE
        
        try {
            handleParser.prepareAndValidateHandleWithHdlPrefix(notAHandle);
            fail("should have thrown an exception (not a handle - 2)");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
    }
    
    @Test
    public void prepareAndValidate_Long_EmptyOrNullHandle() {
        
        // empty handle
        
        URI emptyHandle = URI.create("");
        String expectedExceptionMessage = "Invalid handle (" + emptyHandle + ")";
        
        try {
            handleParser.prepareAndValidateHandleWithLongHdlPrefix(emptyHandle);
            fail("should have thrown an exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
        
        // null handle
        
        expectedExceptionMessage = "Invalid handle (" + null + ")";
        
        try {
            handleParser.prepareAndValidateHandleWithLongHdlPrefix(null);
            fail("should have thrown an exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
    }
    
    @Test
    public void prepareAndValidate_Long_HandleContainingNoPrefix() {
        
        // without prefix
        
        final String strippedHandle = "00-0A0A1B1B-2C2C-3D3D-4E4E-4E5FF5F6G7G9";
        URI initialHandle = URI.create(strippedHandle);
        String expectedExceptionMessage = "Invalid handle (" + initialHandle + ")";
        
        try {
            handleParser.prepareAndValidateHandleWithLongHdlPrefix(initialHandle);
            fail("should have thrown an exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
        
        // with just a slash in the beginning
        
        initialHandle = URI.create("/" + strippedHandle);
        expectedExceptionMessage = "Invalid handle (" + initialHandle + ")";
        
        try {
            handleParser.prepareAndValidateHandleWithLongHdlPrefix(initialHandle);
            fail("should have thrown an exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
    }
    
    @Test
    public void prepareAndValidate_Long_HandleContainingJustPrefix() {
        
        final String strippedHandle = "00-0A0A1B1B-2C2C-3D3D-4E4E-4E5FF5F6G7G9";
        URI initialHandle = URI.create(prefixWithSlash + strippedHandle);
        URI expectedHandle = URI.create(handleLongPrefix + strippedHandle);
        
        URI retrievedHandle = handleParser.prepareAndValidateHandleWithLongHdlPrefix(initialHandle);
        
        assertEquals("Retrieved handle different from expected (normal prefix)", expectedHandle, retrievedHandle);
        
        // with a different prefix
        
        initialHandle = URI.create(someOtherPrefixWithSlash + strippedHandle);
        final String expectedExceptionMessage = "Invalid handle (" + initialHandle + ")";
        
        try {
            handleParser.prepareAndValidateHandleWithLongHdlPrefix(initialHandle);
            fail("should have thrown an exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
    }
    
    @Test
    public void prepareAndValidate_Long_HandleContainingHdlPrefix() {
        
        final String strippedHandle = "00-0A0A1B1B-2C2C-3D3D-4E4E-4E5FF5F6G7G9";
        URI initialHandle = URI.create(handleShortPrefix + strippedHandle);
        URI expectedHandle = URI.create(handleLongPrefix + strippedHandle);
        
        URI retrievedHandle = handleParser.prepareAndValidateHandleWithLongHdlPrefix(initialHandle);
        
        assertEquals("Retrieved handle different from expected (normal prefix)", expectedHandle, retrievedHandle);
        
        // with a different prefix
        
        initialHandle = URI.create(handleSomeOtherShortPrefix + strippedHandle);
        final String expectedExceptionMessage = "Invalid handle (" + initialHandle + ")";
        
        try {
            handleParser.prepareAndValidateHandleWithLongHdlPrefix(initialHandle);
            fail("should have thrown an exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
    }
    
    @Test
    public void prepareAndValidate_Long_HandleContainingFullPrefix() {
        
        final String strippedHandle = "00-0A0A1B1B-2C2C-3D3D-4E4E-4E5FF5F6G7G9";
        URI initialHandle = URI.create(handleLongPrefix + strippedHandle);
        URI expectedHandle = initialHandle;
        
        URI retrievedHandle = handleParser.prepareAndValidateHandleWithLongHdlPrefix(initialHandle);
        
        assertEquals("Retrieved handle different from expected (normal prefix)", expectedHandle, retrievedHandle);
        
        // with a different prefix
        
        initialHandle = URI.create(handleSomeOtherLongPrefix + strippedHandle);
        final String expectedExceptionMessage = "Invalid handle (" + initialHandle + ")";
        
        try {
            handleParser.prepareAndValidateHandleWithLongHdlPrefix(initialHandle);
            fail("should have thrown an exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
    }
    
    @Test
    public void prepareAndValidate_Long_NotAHandle() {
        
        final URI notAHandle = URI.create("https://some/location/node.cmdi");
        final String expectedExceptionMessage = "Invalid handle (" + notAHandle + ")";
        
        try {
            handleParser.prepareAndValidateHandleWithLongHdlPrefix(notAHandle);
            fail("should have thrown an exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
    }
    
    @Test
    public void stripAndValidate_EmptyOrNullHandle() {
        
        // empty handle
        
        final URI emptyHandle = URI.create("");
        String expectedExceptionMessage = "Invalid handle (" + emptyHandle + ")";
        
        try {
            handleParser.stripAndValidateHandleIfPrefixIsKnown(emptyHandle);
            fail("should have thrown an exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }

        // null handle
        expectedExceptionMessage = "Invalid handle (" + null + ")";
        
        try {
            handleParser.stripAndValidateHandleIfPrefixIsKnown(null);
            fail("should have thrown an exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
    }
    
    @Test
    public void stripAndValidate_HandleStartingWithHandleLongPrefix() {
        
        final String handleUuid = UUID.randomUUID().toString();
        final URI fullHandle = URI.create(handleLongPrefix + handleUuid);
        
        String result = handleParser.stripAndValidateHandleIfPrefixIsKnown(fullHandle);
        
        assertEquals("Stripped handle different from expected", handleUuid, result);
    }
    
    @Test
    public void stripAndValidate_HandleStartingWithHandleShortPrefix() {
        
        final String handleUuid = UUID.randomUUID().toString();
        final URI fullHandle = URI.create(handleShortPrefix + handleUuid);
        
        String result = handleParser.stripAndValidateHandleIfPrefixIsKnown(fullHandle);
        
        assertEquals("Stripped handle different from expected", handleUuid, result);
    }
    
    @Test
    public void stripAndValidate_HandleStartingWithJustPrefix() {
        
        final String handleUuid = UUID.randomUUID().toString();
        final URI fullHandle = URI.create(prefixWithSlash + handleUuid);
        
        String result = handleParser.stripAndValidateHandleIfPrefixIsKnown(fullHandle);
        
        assertEquals("Stripped handle different from expected", handleUuid, result);
    }
    
    @Test
    public void stripAndValidate_HandleWithoutPrefix() {
        
        // without prefix
        
        final String handleUuid = UUID.randomUUID().toString();
        final URI handle = URI.create(handleUuid);
        String expectedExceptionMessage = "Invalid handle (" + handle + ")";
        
        try {
            handleParser.stripAndValidateHandleIfPrefixIsKnown(handle);
            fail("should have thrown an exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
        
        // without prefix and starting with a slash
        
        final URI handleWithJustSlash = URI.create("/" + handleUuid);
        expectedExceptionMessage = "Invalid handle (" + handleWithJustSlash + ")";
        
        try {
            handleParser.stripAndValidateHandleIfPrefixIsKnown(handleWithJustSlash);
            fail("should have thrown an exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
    }
    
    @Test
    public void stripAndValidate_HandleStartingWithHandleSomeOtherLongPrefix() {
        
        final String handleUuid = UUID.randomUUID().toString();
        final URI fullHandle = URI.create(handleSomeOtherLongPrefix + handleUuid);
        String expectedExceptionMessage = "Invalid handle (" + fullHandle + ")";
        
        try {
            handleParser.stripAndValidateHandleIfPrefixIsKnown(fullHandle);
            fail("should have thrown an exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
    }
    
    @Test
    public void stripAndValidate_HandleStartingWithHandleSomeOtherShortPrefix() {
        
        final String handleUuid = UUID.randomUUID().toString();
        final URI fullHandle = URI.create(handleSomeOtherShortPrefix + handleUuid);
        String expectedExceptionMessage = "Invalid handle (" + fullHandle + ")";
        
        try {
            handleParser.stripAndValidateHandleIfPrefixIsKnown(fullHandle);
            fail("should have thrown an exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
    }
    
    @Test
    public void stripAndValidate_HandleStartingWithJustSomeOtherPrefix() {
        
        final String handleUuid = UUID.randomUUID().toString();
        final URI fullHandle = URI.create("blabla/" + handleUuid);
        String expectedExceptionMessage = "Invalid handle (" + fullHandle + ")";
        
        try {
            handleParser.stripAndValidateHandleIfPrefixIsKnown(fullHandle);
            fail("should have thrown an exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", expectedExceptionMessage, ex.getMessage());
        }
    }

    @Test
    public void getHandleWithoutProxy() {
        assertEquals("hdl scheme", "handle", handleParser.getHandleWithoutProxy("hdl:handle"));
        assertEquals("global proxy", "handle", handleParser.getHandleWithoutProxy("http://hdl.handle.net/handle"));
        assertEquals("no scheme or proxy", "handle", handleParser.getHandleWithoutProxy("handle"));
        assertEquals("starts with slash", "handle", handleParser.getHandleWithoutProxy("/handle"));

        assertEquals("hdl scheme", "1234/5678", handleParser.getHandleWithoutProxy("hdl:1234/5678"));
        assertEquals("global proxy", "1234/5678", handleParser.getHandleWithoutProxy("http://hdl.handle.net/1234/5678"));
        assertEquals("no scheme or proxy", "1234/5678", handleParser.getHandleWithoutProxy("1234/5678"));
        assertEquals("starts with slash", "1234/5678", handleParser.getHandleWithoutProxy("/1234/5678"));
    }
}
