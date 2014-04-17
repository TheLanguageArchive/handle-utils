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
package nl.mpi.handle.util;

import nl.mpi.handle.util.implementation.HandleManagerImpl;
import nl.mpi.handle.util.implementation.HandleUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;
import net.handle.hdllib.HandleException;
import net.handle.hdllib.HandleValue;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
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
@PrepareForTest({UUID.class, HandleUtil.class})
public class HandleManagerImplTest {
    
    @Rule public JUnitRuleMockery context = new JUnitRuleMockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
    
    private HandleManagerImpl handleManager;
    
    private final String proxy = "http://hdl.handle.net/";
    private final String hdlPrefix = "hdl:";
    private final String prefix = "11142";
    
    private final HandleInfoRetriever mockHandleInfoRetriever = context.mock(HandleInfoRetriever.class);
    private final HandleUtil mockHandleUtil = context.mock(HandleUtil.class);
    
    @Mock File mockFile;
    
    
    public HandleManagerImplTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws FileNotFoundException, IOException {
        handleManager = new HandleManagerImpl(mockHandleInfoRetriever, mockHandleUtil, prefix, proxy);
    }
    
    @After
    public void tearDown() {
    }

    
    @Test
    public void handlesEqual() throws URISyntaxException {
        
        final String baseHandle = "00-0A0A1B1B-2C2C-3D3D-4E4E-4E5FF5F6G7G9";
        final URI firstHandle = new URI(proxy + prefix + "/" + baseHandle);
        final URI secondHandle = firstHandle;
        final String firstHandleStr = firstHandle.toString();
        final String secondHandleStr = secondHandle.toString();
        
        context.checking(new Expectations() {{
            
            oneOf(mockHandleInfoRetriever).stripHandle(firstHandleStr); will(returnValue(baseHandle));
            oneOf(mockHandleInfoRetriever).stripHandle(secondHandleStr); will(returnValue(baseHandle));
        }});
        
        boolean result = handleManager.areHandlesEquivalent(firstHandle, secondHandle);
        
        assertTrue("Result should have been true", result);
    }
    
    @Test
    public void handlesDifferent() throws URISyntaxException {
        
        final String firstBaseHandle = "00-0A0A1B1B-2C2C-3D3D-4E4E-4E5FF5F6G7G9";
        final String secondBaseHandle = "00-0B0B1C1C-2D2D-3E3E-4F4F-4F5GG5G6H7H9";
        final URI firstHandle = new URI(proxy + prefix + "/" + firstBaseHandle);
        final URI secondHandle = new URI(proxy + prefix + "/" + secondBaseHandle);
        final String firstHandleStr = firstHandle.toString();
        final String secondHandleStr = secondHandle.toString();
        
        context.checking(new Expectations() {{
            
            oneOf(mockHandleInfoRetriever).stripHandle(firstHandleStr); will(returnValue(firstBaseHandle));
            oneOf(mockHandleInfoRetriever).stripHandle(secondHandleStr); will(returnValue(secondBaseHandle));
        }});
        
        boolean result = handleManager.areHandlesEquivalent(firstHandle, secondHandle);
        
        assertFalse("Result should have been false", result);
    }
    
    @Test
    public void handlesDifferentOneIsEmpty() throws URISyntaxException {
        
        String altProxy = "hdl:";
        
        String baseHandle = "/00-0A0A1B1B-2C2C-3D3D-4E4E-4E5FF5F6G7G9";
        URI firstHandle = null;
        URI secondHandle = new URI(altProxy + prefix + "/" + baseHandle);
        
        try {
            handleManager.areHandlesEquivalent(firstHandle, secondHandle);
            fail("should have thrown an exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", "Invalid handle(s)", ex.getMessage());
        }
    }
    
    @Test
    public void handlesDifferentOneIsNull() throws URISyntaxException {
        
        String baseHandle = "/00-0A0A1B1B-2C2C-3D3D-4E4E-4E5FF5F6G7G9";
        URI firstHandle = new URI(proxy + prefix + "/" + baseHandle);
        URI secondHandle = null;
        
        try {
            handleManager.areHandlesEquivalent(firstHandle, secondHandle);
            fail("should have thrown an exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception message different from expected", "Invalid handle(s)", ex.getMessage());
        }
    }
    
    @Test
    public void prepareHandleContainingNoPrefix() throws URISyntaxException {
        
        final String strippedHandle = "00-0A0A1B1B-2C2C-3D3D-4E4E-4E5FF5F6G7G9";
        final URI initialHandle = new URI(strippedHandle);
        final URI expectedHandle = new URI(hdlPrefix + prefix + "/" + strippedHandle);
        
        context.checking(new Expectations() {{
            oneOf(mockHandleInfoRetriever).stripHandle(initialHandle.toString()); will(returnValue(strippedHandle));
        }});
        
        URI retrievedHandle = handleManager.prepareHandleWithHdlPrefix(initialHandle);
        
        assertEquals("Retrieved handle different from expected", expectedHandle, retrievedHandle);
    }
    
    @Test
    public void prepareHandleContainingJustPrefix() throws URISyntaxException {
        
        final String strippedHandle = "00-0A0A1B1B-2C2C-3D3D-4E4E-4E5FF5F6G7G9";
        final URI initialHandle = new URI(prefix + "/" + strippedHandle);
        final URI expectedHandle = new URI(hdlPrefix + prefix + "/" + strippedHandle);
        
        context.checking(new Expectations() {{
            oneOf(mockHandleInfoRetriever).stripHandle(initialHandle.toString()); will(returnValue(strippedHandle));
        }});
        
        URI retrievedHandle = handleManager.prepareHandleWithHdlPrefix(initialHandle);
        
        assertEquals("Retrieved handle different from expected", expectedHandle, retrievedHandle);
    }
    
    @Test
    public void prepareHandleContainingHdlPrefix() throws URISyntaxException {
        
        final String strippedHandle = "00-0A0A1B1B-2C2C-3D3D-4E4E-4E5FF5F6G7G9";
        final URI initialHandle = new URI(hdlPrefix + prefix + "/" + strippedHandle);
        final URI expectedHandle = initialHandle;
        
        context.checking(new Expectations() {{
            oneOf(mockHandleInfoRetriever).stripHandle(initialHandle.toString()); will(returnValue(strippedHandle));
        }});
        
        URI retrievedHandle = handleManager.prepareHandleWithHdlPrefix(initialHandle);
        
        assertEquals("Retrieved handle different from expected", expectedHandle, retrievedHandle);
    }
    
    @Test
    public void prepareHandleContainingFullPrefix() throws URISyntaxException {
        
        final String strippedHandle = "00-0A0A1B1B-2C2C-3D3D-4E4E-4E5FF5F6G7G9";
        final URI initialHandle = new URI(proxy + prefix + "/" + strippedHandle);
        final URI expectedHandle = new URI(hdlPrefix + prefix + "/" + strippedHandle);
        
        context.checking(new Expectations() {{
            oneOf(mockHandleInfoRetriever).stripHandle(initialHandle.toString()); will(returnValue(strippedHandle));
        }});
        
        URI retrievedHandle = handleManager.prepareHandleWithHdlPrefix(initialHandle);
        
        assertEquals("Retrieved handle different from expected", expectedHandle, retrievedHandle);
    }
    
    @Test
    public void prepareInvalidHandle() throws URISyntaxException {
        
        final String strippedHandle = "1";
        final URI initialHandle = new URI(prefix + "/" + strippedHandle);
        final IllegalArgumentException expectedException = new IllegalArgumentException("some exception message");
        
        context.checking(new Expectations() {{
            oneOf(mockHandleInfoRetriever).stripHandle(initialHandle.toString()); will(throwException(expectedException));
        }});
        
        try {
            handleManager.prepareHandleWithHdlPrefix(initialHandle);
            fail("should have thrown an exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("exception different from expected", expectedException, ex);
        }
    }
    
    @Test
    public void assignNewHandle() throws URISyntaxException, HandleException, IOException {
        
        final URI targetURI = new URI("http://server/archive/target,cmdi");
        final UUID generatedUUID = UUID.randomUUID();
        final String generatedUUIDStr = generatedUUID.toString().toUpperCase();
        final String generatedHandleStr = prefix + "/00-" + generatedUUIDStr;
        final URI generatedHandleUri = new URI(generatedHandleStr);
        
        final HandleValue[] fakeHandleValues = {new HandleValue()};
        
        context.checking(new Expectations() {{
            
            oneOf(mockHandleInfoRetriever).generateUuidHandle(); will(returnValue(generatedHandleStr));
            oneOf(mockHandleInfoRetriever).createHandleInformation(mockFile, targetURI); will(returnValue(fakeHandleValues));
            oneOf(mockHandleUtil).createHandle(generatedHandleStr, fakeHandleValues);
        }});
        
        stub(method(UUID.class, "randomUUID")).toReturn(generatedUUID);
        
        URI retrievedHandle = handleManager.assignNewHandle(mockFile, targetURI);
        
        assertEquals("Retrieved handle different from expected", generatedHandleUri, retrievedHandle);
    }
    
    @Test
    public void assignNewHandleThrowsHandleException() throws URISyntaxException, HandleException, IOException {
        
        final URI targetURI = new URI("http://server/archive/target,cmdi");
        final UUID generatedUUID = UUID.randomUUID();
        final String generatedUUIDStr = generatedUUID.toString().toUpperCase();
        final String generatedHandleStr = prefix + "/00-" + generatedUUIDStr;
        
        final HandleValue[] fakeHandleValues = {new HandleValue()};
        
        final HandleException expectedException = new HandleException(1, "some exception message");
        
        context.checking(new Expectations() {{
            
            oneOf(mockHandleInfoRetriever).generateUuidHandle(); will(returnValue(generatedHandleStr));
            oneOf(mockHandleInfoRetriever).createHandleInformation(mockFile, targetURI); will(returnValue(fakeHandleValues));
            oneOf(mockHandleUtil).createHandle(generatedHandleStr, fakeHandleValues); will(throwException(expectedException));
        }});
        
        stub(method(UUID.class, "randomUUID")).toReturn(generatedUUID);
        
        try {
            handleManager.assignNewHandle(mockFile, targetURI);
            fail("should have thrown an exception");
        } catch(HandleException ex) {
            assertEquals("Exception different from expected", expectedException, ex);
        }
    }
    
    @Test
    public void assignNewHandleThrowsIOException() throws URISyntaxException, HandleException, IOException {
        
        final URI targetURI = new URI("http://server/archive/target,cmdi");
        final UUID generatedUUID = UUID.randomUUID();
        final String generatedUUIDStr = generatedUUID.toString().toUpperCase();
        final String generatedHandleStr = prefix + "/00-" + generatedUUIDStr;
        
        final HandleValue[] fakeHandleValues = {new HandleValue()};
        
        final IOException expectedException = new IOException("some exception message");
        
        context.checking(new Expectations() {{
            
            oneOf(mockHandleInfoRetriever).generateUuidHandle(); will(returnValue(generatedHandleStr));
            oneOf(mockHandleInfoRetriever).createHandleInformation(mockFile, targetURI); will(returnValue(fakeHandleValues));
            oneOf(mockHandleUtil).createHandle(generatedHandleStr, fakeHandleValues); will(throwException(expectedException));
        }});
        
        stub(method(UUID.class, "randomUUID")).toReturn(generatedUUID);
        
        try {
            handleManager.assignNewHandle(mockFile, targetURI);
            fail("should have thrown an exception");
        } catch(IOException ex) {
            assertEquals("Exception different from expected", expectedException, ex);
        }
    }
    
    @Test
    public void assignNewHandleThrowsURISyntaxException() throws URISyntaxException, HandleException, IOException {
        
        final URI targetURI = new URI("http://server/archive/target,cmdi");
        final UUID generatedUUID = UUID.randomUUID();
        final String generatedUUIDStr = generatedUUID.toString().toUpperCase();
        final String invalidUriHandleStr = ":00-" + generatedUUIDStr;
        
        final HandleValue[] fakeHandleValues = {new HandleValue()};
        
        context.checking(new Expectations() {{
            
            oneOf(mockHandleInfoRetriever).generateUuidHandle(); will(returnValue(invalidUriHandleStr));
            oneOf(mockHandleInfoRetriever).createHandleInformation(mockFile, targetURI); will(returnValue(fakeHandleValues));
            oneOf(mockHandleUtil).createHandle(invalidUriHandleStr, fakeHandleValues);
        }});
        
        stub(method(UUID.class, "randomUUID")).toReturn(generatedUUID);
        
        try {
            handleManager.assignNewHandle(mockFile, targetURI);
            fail("should have thrown an exception");
        } catch(URISyntaxException ex) {
            assertNotNull("Something", ex);
        }
    }
    
    @Test
    public void updateHandle() throws URISyntaxException, FileNotFoundException, IOException, HandleException {
        
        final UUID handleUUID = UUID.randomUUID();
        final String handleUUIDStr = handleUUID.toString().toUpperCase();
        final String handleStr = prefix + "/00-" + handleUUIDStr;
        final URI handle = new URI(handleStr);
        final URI newTarget = new URI("http://server/archive/target,cmdi");
        
        final HandleValue[] fakeHandleValues = {new HandleValue()};
        
        context.checking(new Expectations() {{
            
            oneOf(mockHandleInfoRetriever).createHandleInformation(mockFile, newTarget); will(returnValue(fakeHandleValues));
            oneOf(mockHandleUtil).updateHandleValue(handleStr, fakeHandleValues);
        }});
        
        handleManager.updateHandle(mockFile, handle, newTarget);
    }
    
    @Test
    public void updateHandleThrowsHandleException() throws URISyntaxException, FileNotFoundException, IOException, HandleException {
        
        final UUID handleUUID = UUID.randomUUID();
        final String handleUUIDStr = handleUUID.toString().toUpperCase();
        final String handleStr = prefix + "/00-" + handleUUIDStr;
        final URI handle = new URI(handleStr);
        final URI newTarget = new URI("http://server/archive/target,cmdi");
        
        final HandleValue[] fakeHandleValues = {new HandleValue()};
        
        final HandleException expectedException = new HandleException(1, "some exception message");
        
        context.checking(new Expectations() {{
            
            oneOf(mockHandleInfoRetriever).createHandleInformation(mockFile, newTarget); will(returnValue(fakeHandleValues));
            oneOf(mockHandleUtil).updateHandleValue(handleStr, fakeHandleValues); will(throwException(expectedException));
        }});
        
        try {
            handleManager.updateHandle(mockFile, handle, newTarget);
            fail("should have thrown exception");
        } catch(HandleException ex) {
            assertEquals("Exception different from exception", expectedException, ex);
        }
    }
    
    @Test
    public void updateHandleThrowsIOException() throws URISyntaxException, FileNotFoundException, IOException, HandleException {
        
        final UUID handleUUID = UUID.randomUUID();
        final String handleUUIDStr = handleUUID.toString().toUpperCase();
        final String handleStr = prefix + "/00-" + handleUUIDStr;
        final URI handle = new URI(handleStr);
        final URI newTarget = new URI("http://server/archive/target,cmdi");
        
        final IOException expectedException = new IOException("some exception message");
        
        final HandleValue[] fakeHandleValues = {new HandleValue()};
        
        context.checking(new Expectations() {{
            
            oneOf(mockHandleInfoRetriever).createHandleInformation(mockFile, newTarget); will(returnValue(fakeHandleValues));
            oneOf(mockHandleUtil).updateHandleValue(handleStr, fakeHandleValues); will(throwException(expectedException));
        }});
        
        try {
            handleManager.updateHandle(mockFile, handle, newTarget);
            fail("should have thrown exception");
        } catch(IOException ex) {
            assertEquals("Exception different from expected", expectedException, ex);
        }
    }
    
    @Test
    public void deleteHandle() throws URISyntaxException, FileNotFoundException, IOException, HandleException {
        
        final UUID handleUUID = UUID.randomUUID();
        final String handleUUIDStr = handleUUID.toString().toUpperCase();
        final String handleStr = prefix + "/00-" + handleUUIDStr;
        final URI handle = new URI(handleStr);
        
        context.checking(new Expectations() {{
            
            oneOf(mockHandleUtil).deleteHandle(handleStr);
        }});
        
        handleManager.deleteHandle(handle);
    }
    
    @Test
    public void deleteHandleThrowsHandleException() throws URISyntaxException, FileNotFoundException, IOException, HandleException {
        
        final UUID handleUUID = UUID.randomUUID();
        final String handleUUIDStr = handleUUID.toString().toUpperCase();
        final String handleStr = prefix + "/00-" + handleUUIDStr;
        final URI handle = new URI(handleStr);
        
        final HandleException expectedException = new HandleException(1, "some exception message");
        
        context.checking(new Expectations() {{
            
            oneOf(mockHandleUtil).deleteHandle(handleStr); will(throwException(expectedException));
        }});
        
        try {
            handleManager.deleteHandle(handle);
            fail("should have thrown exception");
        } catch(HandleException ex) {
            assertEquals("Exception different from expected", expectedException, ex);
        }
    }
    
    @Test
    public void deleteHandleThrowsIOException() throws URISyntaxException, FileNotFoundException, IOException, HandleException {
        
        final UUID handleUUID = UUID.randomUUID();
        final String handleUUIDStr = handleUUID.toString().toUpperCase();
        final String handleStr = prefix + "/00-" + handleUUIDStr;
        final URI handle = new URI(handleStr);
        
        final IOException expectedException = new IOException("some exception message");
        
        context.checking(new Expectations() {{
            
            oneOf(mockHandleUtil).deleteHandle(handleStr); will(throwException(expectedException));
        }});
        
        try {
            handleManager.deleteHandle(handle);
            fail("should have thrown exception");
        } catch(IOException ex) {
            assertEquals("Exception different from expected", expectedException, ex);
        }
    }
}