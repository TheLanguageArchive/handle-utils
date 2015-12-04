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
import java.util.UUID;
import net.handle.hdllib.HandleException;
import net.handle.hdllib.HandleValue;
import nl.mpi.handle.util.HandleInfoProvider;
import nl.mpi.handle.util.HandleParser;
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
    
    private final String prefix = "11142";
    
    private final HandleInfoProvider mockHandleInfoProvider = context.mock(HandleInfoProvider.class);
    private final HandleParser mockHandleParser = context.mock(HandleParser.class);
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
        handleManager = new HandleManagerImpl(mockHandleInfoProvider, mockHandleParser, mockHandleUtil, prefix);
    }
    
    @After
    public void tearDown() {
    }

    
    @Test
    public void assignNewHandle() throws HandleException, IOException {
        
        final URI targetURI = URI.create("http://server/archive/target,cmdi");
        final UUID generatedUUID = UUID.randomUUID();
        final String generatedUUIDStr = generatedUUID.toString().toUpperCase();
        final String generatedHandleStr = prefix + "/00-" + generatedUUIDStr;
        final URI generatedHandleUri = URI.create(generatedHandleStr);
        
        final HandleValue[] fakeHandleValues = {new HandleValue()};
        
        context.checking(new Expectations() {{
            
            oneOf(mockHandleInfoProvider).generateUuidHandle(); will(returnValue(generatedHandleStr));
            oneOf(mockHandleInfoProvider).createHandleInformation(mockFile, targetURI); will(returnValue(fakeHandleValues));
            oneOf(mockHandleUtil).createHandle(generatedHandleStr, fakeHandleValues);
        }});
        
        stub(method(UUID.class, "randomUUID")).toReturn(generatedUUID);
        
        URI retrievedHandle = handleManager.assignNewHandle(mockFile, targetURI);
        
        assertEquals("Retrieved handle different from expected", generatedHandleUri, retrievedHandle);
    }
    
    @Test
    public void assignNewHandleThrowsHandleException() throws HandleException, IOException {
        
        final URI targetURI = URI.create("http://server/archive/target,cmdi");
        final UUID generatedUUID = UUID.randomUUID();
        final String generatedUUIDStr = generatedUUID.toString().toUpperCase();
        final String generatedHandleStr = prefix + "/00-" + generatedUUIDStr;
        
        final HandleValue[] fakeHandleValues = {new HandleValue()};
        
        final HandleException expectedException = new HandleException(1, "some exception message");
        
        context.checking(new Expectations() {{
            
            oneOf(mockHandleInfoProvider).generateUuidHandle(); will(returnValue(generatedHandleStr));
            oneOf(mockHandleInfoProvider).createHandleInformation(mockFile, targetURI); will(returnValue(fakeHandleValues));
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
    public void assignNewHandleThrowsIOException() throws HandleException, IOException {
        
        final URI targetURI = URI.create("http://server/archive/target,cmdi");
        final UUID generatedUUID = UUID.randomUUID();
        final String generatedUUIDStr = generatedUUID.toString().toUpperCase();
        final String generatedHandleStr = prefix + "/00-" + generatedUUIDStr;
        
        final HandleValue[] fakeHandleValues = {new HandleValue()};
        
        final IOException expectedException = new IOException("some exception message");
        
        context.checking(new Expectations() {{
            
            oneOf(mockHandleInfoProvider).generateUuidHandle(); will(returnValue(generatedHandleStr));
            oneOf(mockHandleInfoProvider).createHandleInformation(mockFile, targetURI); will(returnValue(fakeHandleValues));
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
    public void assignHandle_WithHdlProxy() throws HandleException, IOException, URISyntaxException {

        final URI targetURI = URI.create("http://server/archive/target,cmdi");
        final String uuidStr = UUID.randomUUID().toString().toUpperCase();
        final String handleStr = prefix + "/00-" + uuidStr;
        final URI handleUri = URI.create(handleStr);
        final URI completeHandleUri = URI.create("hdl:" + handleStr);
        
        final HandleValue[] fakeHandleValues = {new HandleValue()};
        
        context.checking(new Expectations() {{
            
            oneOf(mockHandleParser).prepareAndValidateHandleWithoutProxy(completeHandleUri); will(returnValue(handleUri));
            oneOf(mockHandleInfoProvider).createHandleInformation(mockFile, targetURI); will(returnValue(fakeHandleValues));
            oneOf(mockHandleUtil).createHandle(handleStr, fakeHandleValues);
        }});
        
        URI retrievedHandle = handleManager.assignHandle(mockFile, completeHandleUri, targetURI);
        
        assertEquals("Retrieved handle different from expected", handleUri, retrievedHandle);
    }
    
    @Test
    public void assignHandle_WithoutHdlProxy() throws HandleException, IOException, URISyntaxException {

        final URI targetURI = URI.create("http://server/archive/target,cmdi");
        final String uuidStr = UUID.randomUUID().toString().toUpperCase();
        final String handleStr = prefix + "/00-" + uuidStr;
        final URI handleUri = URI.create(handleStr);
        
        final HandleValue[] fakeHandleValues = {new HandleValue()};
        
        context.checking(new Expectations() {{
            
            oneOf(mockHandleParser).prepareAndValidateHandleWithoutProxy(handleUri); will(returnValue(handleUri));
            oneOf(mockHandleInfoProvider).createHandleInformation(mockFile, targetURI); will(returnValue(fakeHandleValues));
            oneOf(mockHandleUtil).createHandle(handleStr, fakeHandleValues);
        }});
        
        URI retrievedHandle = handleManager.assignHandle(mockFile, handleUri, targetURI);
        
        assertEquals("Retrieved handle different from expected", handleUri, retrievedHandle);
    }
    
    @Test
    public void assignHandle_invalid() throws IOException, URISyntaxException, HandleException {
        
        final URI targetURI = URI.create("http://server/archive/target,cmdi");
        final String handleStr = "http://12345";
        final URI handleUri = URI.create(handleStr);
        
        final IllegalArgumentException expectedException = new IllegalArgumentException("Invalid handle (" + handleStr + ")");
        
        context.checking(new Expectations() {{
            
            oneOf(mockHandleParser).prepareAndValidateHandleWithoutProxy(handleUri); will(throwException(expectedException));
        }});
        
        try {
            handleManager.assignHandle(mockFile, handleUri, targetURI);
            fail("should have thrown exception");
        } catch(IllegalArgumentException ex) {
            assertEquals("Exception different from expected", expectedException, ex);
        }
    }
    
    @Test
    public void assignHandleThrowsHandleException() throws HandleException, IOException, URISyntaxException {

        final URI targetURI = URI.create("http://server/archive/target,cmdi");
        final String uuidStr = UUID.randomUUID().toString().toUpperCase();
        final String handleStr = prefix + "/00-" + uuidStr;
        final URI handleUri = URI.create(handleStr);
        final URI completeHandleUri = URI.create("hdl:" + handleStr);
        
        final HandleValue[] fakeHandleValues = {new HandleValue()};
        
        final HandleException expectedException = new HandleException(1, "some exception message");
        
        context.checking(new Expectations() {{
            
            oneOf(mockHandleParser).prepareAndValidateHandleWithoutProxy(completeHandleUri); will(returnValue(handleUri));
            oneOf(mockHandleInfoProvider).createHandleInformation(mockFile, targetURI); will(returnValue(fakeHandleValues));
            oneOf(mockHandleUtil).createHandle(handleStr, fakeHandleValues); will(throwException(expectedException));
        }});
        
        try {
            handleManager.assignHandle(mockFile, completeHandleUri, targetURI);
            fail("should have thrown an exception");
        } catch(HandleException ex) {
            assertEquals("Exception different from expected", expectedException, ex);
        }
    }
    
    @Test
    public void assignHandleThrowsIOException() throws HandleException, IOException, URISyntaxException {

        final URI targetURI = URI.create("http://server/archive/target,cmdi");
        final String uuidStr = UUID.randomUUID().toString().toUpperCase();
        final String handleStr = prefix + "/00-" + uuidStr;
        final URI handleUri = URI.create(handleStr);
        final URI completeHandleUri = URI.create("hdl:" + handleStr);
        
        final HandleValue[] fakeHandleValues = {new HandleValue()};
        
        final IOException expectedException = new IOException("some exception message");
        
        context.checking(new Expectations() {{
            
            oneOf(mockHandleParser).prepareAndValidateHandleWithoutProxy(completeHandleUri); will(returnValue(handleUri));
            oneOf(mockHandleInfoProvider).createHandleInformation(mockFile, targetURI); will(returnValue(fakeHandleValues));
            oneOf(mockHandleUtil).createHandle(handleStr, fakeHandleValues); will(throwException(expectedException));
        }});
        
        try {
            handleManager.assignHandle(mockFile, completeHandleUri, targetURI);
            fail("should have thrown an exception");
        } catch(IOException ex) {
            assertEquals("Exception different from expected", expectedException, ex);
        }
    }
    
    @Test
    public void updateHandle() throws FileNotFoundException, IOException, HandleException {
        
        final UUID handleUUID = UUID.randomUUID();
        final String handleUUIDStr = handleUUID.toString().toUpperCase();
        final String handleStr = prefix + "/00-" + handleUUIDStr;
        final URI handle = URI.create(handleStr);
        final URI newTarget = URI.create("http://server/archive/target,cmdi");
        
        final HandleValue[] fakeHandleValues = {new HandleValue()};
        
        context.checking(new Expectations() {{
            
            oneOf(mockHandleInfoProvider).createHandleInformation(mockFile, newTarget); will(returnValue(fakeHandleValues));
            oneOf(mockHandleUtil).updateHandleValue(handleStr, fakeHandleValues);
        }});
        
        handleManager.updateHandle(mockFile, handle, newTarget);
    }
    
    @Test
    public void updateHandleThrowsHandleException() throws FileNotFoundException, IOException, HandleException {
        
        final UUID handleUUID = UUID.randomUUID();
        final String handleUUIDStr = handleUUID.toString().toUpperCase();
        final String handleStr = prefix + "/00-" + handleUUIDStr;
        final URI handle = URI.create(handleStr);
        final URI newTarget = URI.create("http://server/archive/target,cmdi");
        
        final HandleValue[] fakeHandleValues = {new HandleValue()};
        
        final HandleException expectedException = new HandleException(1, "some exception message");
        
        context.checking(new Expectations() {{
            
            oneOf(mockHandleInfoProvider).createHandleInformation(mockFile, newTarget); will(returnValue(fakeHandleValues));
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
    public void updateHandleThrowsIOException() throws FileNotFoundException, IOException, HandleException {
        
        final UUID handleUUID = UUID.randomUUID();
        final String handleUUIDStr = handleUUID.toString().toUpperCase();
        final String handleStr = prefix + "/00-" + handleUUIDStr;
        final URI handle = URI.create(handleStr);
        final URI newTarget = URI.create("http://server/archive/target,cmdi");
        
        final IOException expectedException = new IOException("some exception message");
        
        final HandleValue[] fakeHandleValues = {new HandleValue()};
        
        context.checking(new Expectations() {{
            
            oneOf(mockHandleInfoProvider).createHandleInformation(mockFile, newTarget); will(returnValue(fakeHandleValues));
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
    public void deleteHandle() throws FileNotFoundException, IOException, HandleException {
        
        final UUID handleUUID = UUID.randomUUID();
        final String handleUUIDStr = handleUUID.toString().toUpperCase();
        final String handleStr = prefix + "/00-" + handleUUIDStr;
        final URI handle = URI.create(handleStr);
        
        context.checking(new Expectations() {{
            
            oneOf(mockHandleUtil).deleteHandle(handleStr);
        }});
        
        handleManager.deleteHandle(handle);
    }
    
    @Test
    public void deleteHandleThrowsHandleException() throws FileNotFoundException, IOException, HandleException {
        
        final UUID handleUUID = UUID.randomUUID();
        final String handleUUIDStr = handleUUID.toString().toUpperCase();
        final String handleStr = prefix + "/00-" + handleUUIDStr;
        final URI handle = URI.create(handleStr);
        
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
    public void deleteHandleThrowsIOException() throws FileNotFoundException, IOException, HandleException {
        
        final UUID handleUUID = UUID.randomUUID();
        final String handleUUIDStr = handleUUID.toString().toUpperCase();
        final String handleStr = prefix + "/00-" + handleUUIDStr;
        final URI handle = URI.create(handleStr);
        
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