package nl.mpi.handle.util.implementation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Properties;
import net.handle.api.HSAdapter;
import net.handle.api.HSAdapterFactory;
import net.handle.hdllib.HandleException;
import net.handle.hdllib.HandleResolver;
import net.handle.hdllib.HandleValue;
import net.handle.hdllib.Resolver;
import org.apache.commons.io.IOUtils;
//import nl.mpi.cmdihandleassigner.CMDIHandleAssigner;

/**
 * This class was adapted. The original description follows.
 * @author Guilherme Silva <guilherme.silva@mpi.nl>
 * 
 * This utility provides methods for interacting with the Handle System. It provides simplified functionality to the Handle System api developed by CNRI tailored to the needs of the ADL Registry website.
 * @author Jacob Marks - Joint ADL Co-Lab
 * @version 1.0
 * @license This software is licensed as open source software pursuant to the terms defined in the Joint ADL Co-Lab Open Source Software License.  Use of any part of this software constitutes acceptance of this license. The Joint ADL Co-Lab open source agreement is available at {@link http://adlregistry.adlnet.gov/source_license.html}.
 */
public class HandleUtil
{
    private final String handleAdminKeyFilePath;
    private final int adminUserHandleIndex;
    private final String adminUserHandle;
    private final byte[] handleAdminPassword;
    
    /**
     * The index of an ADL Registry user HS_SECKEY attribute within a user handle.
     */
    private static int CONTRIBUTOR_PRIVATE_KEY_INDEX = 301;
    
    public HandleUtil(String adminKeyFilePath, String adminUserHandleIndex, String adminUserHandle, String adminPassword) {
        
        this.handleAdminKeyFilePath = adminKeyFilePath;
        
        int index = 200;
        try {
            index = Integer.parseInt(adminUserHandleIndex);
        }
        catch (NumberFormatException ex) {
            //Do nothing. Index will be assumed to be the handle system default admin index of 200.
        }
        this.adminUserHandleIndex = index;
        
        this.adminUserHandle = adminUserHandle;
        
        byte[] password;
        if(adminPassword.equalsIgnoreCase("null")) {
            password = null;
        } else {
            password = adminPassword.getBytes();
        }
        this.handleAdminPassword = password;
    }
    
    /**
     * Creates a new Handle.
     * @param iHandle A <CODE>String</CODE> Handle name of the Handle to be acted upon.
     * @param iValues The set of values to be added to the new Handle.
     * @throws java.io.FileNotFoundException Throws <CODE>FileNotFoundException</CODE> if the private key file for the website Handle System service account cannot be found on the local file system.
     * @throws java.io.IOException Throws <CODE>IOException</CODE> if the private key file for the website Handle System service account cannot be accessed.
     * @throws net.handle.hdllib.HandleException Throws <CODE>HandleException</CODE> if the Handle System encounters an error during Handle creation.
     */
     public void createHandle(String iHandle, HandleValue[] iValues)
    throws HandleException, IOException
    {
        HSAdapter api = getHandleApi();
        
        createHandle(iHandle, iValues, api);
    }
    
    /**
     * Creates a new Handle.
     * @param iHandle A <CODE>String</CODE> Handle name of the Handle to be acted upon.
     * @param iValues The set of values to be added to the new Handle.
     * @param iApi An <CODE>HSAdapter</CODE> for accessing the Handle System api.
     * @throws net.handle.hdllib.HandleException Throws <CODE>HandleException</CODE> if the Handle System encounters an error during Handle creation.
     */
    public void createHandle(String iHandle, HandleValue[] iValues, HSAdapter iApi)
    throws HandleException
    {
        iApi.createHandle(iHandle, iValues);
    }
    
    /**
     * Deletes a Handle.
     * @param iHandle A <CODE>String</CODE> Handle name of the Handle to be acted upon.
     * @throws java.io.FileNotFoundException Throws <CODE>FileNotFoundException</CODE> if the private key file for the website Handle System service account cannot be found on the local file system.
     * @throws java.io.IOException Throws <CODE>IOException</CODE> if the private key file for the website Handle System service account cannot be accessed.
     * @throws net.handle.hdllib.HandleException Throws <CODE>HandleException</CODE> if the Handle System encounters an error during Handle deletion.
     */
    public void deleteHandle(String iHandle)
    throws FileNotFoundException, IOException, HandleException
    {
        HSAdapter api = getHandleApi();
        
        deleteHandle(iHandle, api);
    }
    
    /**
     * Deletes a Handle.
     * @param iHandle A <CODE>String</CODE> Handle name of the Handle to be acted upon.
     * @param iApi An <CODE>HSAdapter</CODE> for accessing the Handle System api.
     * @throws net.handle.hdllib.HandleException Throws <CODE>HandleException</CODE> if the Handle System encounters an error during Handle deletion.
     */
    public void deleteHandle(String iHandle, HSAdapter iApi)
    throws HandleException
    {
        iApi.deleteHandle(iHandle);
    }
    
    /**
     * Updates a single <CODE>HandleValue</CODE>.
     * @param iHandle A <CODE>String</CODE> Handle name of the Handle to be acted upon.
     * @param iValue The <CODE>HandleValue</CODE> to be used to replace the current HandleValue target.
     * @throws java.io.FileNotFoundException Throws <CODE>FileNotFoundException</CODE> if the private key file for the website Handle System service account cannot be found on the local file system.
     * @throws java.io.IOException Throws <CODE>IOException</CODE> if the private key file for the website Handle System service account cannot be accessed.
     * @throws net.handle.hdllib.HandleException Throws <CODE>HandleException</CODE> if the Handle System encounters an error during Handle update.
     */
    public void updateHandleValue(String iHandle, HandleValue iValue)
    throws FileNotFoundException, IOException, HandleException
    {
        HSAdapter api = getHandleApi();
        
        HandleValue[] values = new HandleValue[] { iValue };
        
        updateHandleValue(iHandle, values, api);
    }
    
    /**
     * Updates a single <CODE>HandleValue</CODE>.
     * @param iHandle A <CODE>String</CODE> Handle name of the Handle to be acted upon.
     * @param iValue The <CODE>HandleValue</CODE> to be used to replace the current HandleValue target.
     * @param iApi An <CODE>HSAdapter</CODE> for accessing the Handle System api.
     * @throws net.handle.hdllib.HandleException Throws <CODE>HandleException</CODE> if the Handle System encounters an error during Handle update.
     */
    public void updateHandleValue(String iHandle, HandleValue iValue, HSAdapter iApi)
    throws HandleException
    {
        HandleValue[] values = new HandleValue[] { iValue };
        
        updateHandleValue(iHandle, values, iApi);
    }
    
    /**
     * Updates multiple <CODE>HandleValue</CODE>'s.
     * @param iHandle A <CODE>String</CODE> Handle name of the Handle to be acted upon.
     * @param iValues A set of <CODE>HandleValue</CODE>'s to be used to replace the current HandleValue targets.
     * @throws java.io.FileNotFoundException Throws <CODE>FileNotFoundException</CODE> if the private key file for the website Handle System service account cannot be found on the local file system.
     * @throws java.io.IOException Throws <CODE>IOException</CODE> if the private key file for the website Handle System service account cannot be accessed.
     * @throws net.handle.hdllib.HandleException Throws <CODE>HandleException</CODE> if the Handle System encounters an error during Handle update.
     */
    public void updateHandleValue(String iHandle, HandleValue[] iValues)
    throws FileNotFoundException, IOException, HandleException
    {
        HSAdapter api = getHandleApi();
        
        updateHandleValue(iHandle, iValues, api);
    }
    
    /**
     * Updates multiple <CODE>HandleValue</CODE>'s.
     * @param iHandle A <CODE>String</CODE> Handle name of the Handle to be acted upon.
     * @param iValues A set of <CODE>HandleValue</CODE>'s to be used to replace the current HandleValue targets.
     * @param iApi An <CODE>HSAdapter</CODE> for accessing the Handle System api.
     * @throws net.handle.hdllib.HandleException Throws <CODE>HandleException</CODE> if the Handle System encounters an error during Handle update.
     */
    public void updateHandleValue(String iHandle, HandleValue[] iValues, HSAdapter iApi)
    throws HandleException
    {
        iApi.updateHandleValues(iHandle, iValues);
    }
    
    /**
     * Add's a new <CODE>HandleValue</CODE> to an existing Handle.
     * @param iHandle A <CODE>String</CODE> Handle name of the Handle to be acted upon.
     * @param iValue The <CODE>HandleValue</CODE> to be added.
     * @throws java.io.FileNotFoundException Throws <CODE>FileNotFoundException</CODE> if the private key file for the website Handle System service account cannot be found on the local file system.
     * @throws java.io.IOException Throws <CODE>IOException</CODE> if the private key file for the website Handle System service account cannot be accessed.
     * @throws net.handle.hdllib.HandleException Throws <CODE>HandleException</CODE> if the Handle System encounters an error during Handle value addition.
     */
    public void addHandleValue(String iHandle, HandleValue iValue)
    throws FileNotFoundException, IOException, HandleException
    {
        HSAdapter api = getHandleApi();
        
        HandleValue[] values = new HandleValue[] { iValue };
        
        addHandleValue(iHandle, values, api);
    }
    
    /**
     * Add's multiple new <CODE>HandleValue</CODE>'s to an existing Handle.
     * @param iHandle A <CODE>String</CODE> Handle name of the Handle to be acted upon.
     * @param iValues The <CODE>HandleValue</CODE>'s to be added.
     * @param iApi An <CODE>HSAdapter</CODE> for accessing the Handle System api.
     * @throws net.handle.hdllib.HandleException Throws <CODE>HandleException</CODE> if the Handle System encounters an error during Handle value addition.
     */
    public void addHandleValue(String iHandle, HandleValue[] iValues, HSAdapter iApi)
    throws HandleException
    {
        iApi.addHandleValues(iHandle, iValues);
    }
    
    /**
     * Resolves the specified Handle within the Handle System.
     * @param iHandle A <CODE>String</CODE> Handle name of the Handle to be acted upon.
     * @param iRequestedValues A set of <CODE>HandleValue</CODE> types to be retreived during resolution.
     * @param iRequestedIndexes A set of requested HandleValue indeces to be retrieved during resolution.
     * @return Returns a set of <CODE>HandleValue</CODE>'s retrieved during resolution.
     * @throws java.io.FileNotFoundException Throws <CODE>FileNotFoundException</CODE> if the private key file for the website Handle System service account cannot be found on the local file system.
     * @throws java.io.IOException Throws <CODE>IOException</CODE> if the private key file for the website Handle System service account cannot be accessed.
     * @throws net.handle.hdllib.HandleException Throws <CODE>HandleException</CODE> if the Handle System encounters an error during Handle resolution.
     */
    public HandleValue[] resolveHandle(String iHandle, String[] iRequestedValues, int[] iRequestedIndexes)
    throws FileNotFoundException, IOException, HandleException
    {
        HSAdapter api = getHandleApi();
        
        HandleValue[] values = resolveHandle(iHandle, iRequestedValues, iRequestedIndexes, api);
        
        return values;
    }
    
    /**
     * Resolves the specified Handle within the Handle System.
     * @param iHandle A <CODE>String</CODE> Handle name of the Handle to be acted upon.
     * @param iRequestedValues A set of <CODE>HandleValue</CODE> types to be retreived during resolution.
     * @param iRequestedIndexes A set of requested HandleValue indeces to be retrieved during resolution.
     * @param iApi An <CODE>HSAdapter</CODE> for accessing the Handle System api.
     * @return Returns a set of <CODE>HandleValue</CODE>'s retrieved during resolution.
     * @throws net.handle.hdllib.HandleException Throws <CODE>HandleException</CODE> if the Handle System encounters an error during Handle resolution.
     */
    public HandleValue[] resolveHandle(String iHandle, String[] iRequestedValues, int[] iRequestedIndexes, HSAdapter iApi)
    throws HandleException
    {
        HandleValue[] values = iApi.resolveHandle(iHandle, null, null);
        
        return values;
    }
    
    /**
     * Gets the website service account private key file from the local file system.
     * @return Returns the private key file as a byte array.
     * @throws java.io.FileNotFoundException Throws <CODE>FileNotFoundException</CODE> if the private key file for the website Handle System service account cannot be found on the local file system.
     * @throws java.io.IOException Throws <CODE>IOException</CODE> if the private key file for the website Handle System service account cannot be accessed.
     */
    public byte[] getPrivateKeyFile() throws IOException 
    {
        byte[] fileBytes = null;
        
        File privKeyFile = new File(handleAdminKeyFilePath);
        
        if (privKeyFile.exists() == false)
        {
            throw new IOException("The admin private key file could not be found.");
        }
        
        if (privKeyFile.canRead() == false)
        {
            throw new IOException("The admin private key file cannot be read.");
        }
        
        RandomAccessFile privateKeyContents = new RandomAccessFile(privKeyFile, "r");
        
        int length = (int) privateKeyContents.length();

        if(length > 0)
        {
            fileBytes = new byte[length];
            privateKeyContents.read(fileBytes);
            privateKeyContents.close();
        }
        else
        {
            throw new IOException("The private key file is empty.");
        }
        
        return fileBytes;
    }
    
    public byte[] getPrivateKeyFileAsStream() throws IOException {
        
        byte[] fileBytes = null;
        
//        InputStream privKeyInputStream = HandleUtil.class.getClassLoader().getResourceAsStream(handleAdminKeyFilePath);
        
        // now using an absolute path
        InputStream privKeyInputStream = new FileInputStream(handleAdminKeyFilePath);

        fileBytes = IOUtils.toByteArray(privKeyInputStream);
        privKeyInputStream.close();
        
        return fileBytes;
    }
    
    /**
     * Gets <CODE>HandleValue</CODE> containing the HS_SECKEY at index 301 of the specified user Handle.
     * @param iHandle A <CODE>String</CODE> Handle name of the Handle to be acted upon.
     * @return Returns the <CODE>HandleValue</CODE> containing the HS_SECKEY at index 301 of the specified user Handle.
     * @throws java.io.FileNotFoundException Throws <CODE>FileNotFoundException</CODE> if the private key file for the website Handle System service account cannot be found on the local file system.
     * @throws java.io.IOException Throws <CODE>IOException</CODE> if the private key file for the website Handle System service account cannot be accessed.
     * @throws net.handle.hdllib.HandleException Throws <CODE>HandleException</CODE> if the Handle System encounters an error during Handle resolution.
     */
    public HandleValue getSecretKey(String iHandle)
    throws FileNotFoundException, IOException, HandleException
    {
        HSAdapter api = getHandleApi();
        
        HandleValue key = getSecretKey(iHandle, api);
        
        return key;
    }
    
    /**
     * Gets <CODE>HandleValue</CODE> containing the HS_SECKEY at index 301 of the specified user Handle.
     * @param iHandle A <CODE>String</CODE> Handle name of the Handle to be acted upon.
     * @param iApi An <CODE>HSAdapter</CODE> for accessing the Handle System api.
     * @return Returns the <CODE>HandleValue</CODE> containing the HS_SECKEY at index 301 of the specified user Handle.
     * @throws net.handle.hdllib.HandleException Throws <CODE>HandleException</CODE> if the Handle System encounters an error during Handle resolution.
     */
    public HandleValue getSecretKey(String iHandle, HSAdapter iApi)
    throws HandleException
    {
        HandleValue key = null;
        
        HandleValue[] values = resolveHandle(iHandle, null, null, iApi);
        
        for (int i = 0; i < values.length; i++)
        {
            HandleValue value = values[i];
            
            if (value.getIndex() == HandleUtil.CONTRIBUTOR_PRIVATE_KEY_INDEX)
            {
                key = value;
                
                break;
            }
        }
        
        return key;
    }
    
    /**
     * Gets a new <CODE>HSAdapter</CODE> Handle System api object.
     * @return Returns a new <CODE>HSAdapter</CODE> Handle System api object.
     * @throws java.io.FileNotFoundException Throws <CODE>FileNotFoundException</CODE> if the private key file for the website Handle System service account cannot be found on the local file system.
     * @throws java.io.IOException Throws <CODE>IOException</CODE> if the private key file for the website Handle System service account cannot be accessed.
     * @throws net.handle.hdllib.HandleException Throws <CODE>HandleException</CODE> if a new <CODE>HSAdapter</CODE> Handle System api object cannot be instantiated.
     */
    public HSAdapter getHandleApi()
    throws HandleException, IOException
    {
        byte[] privateKeyFile = getPrivateKeyFileAsStream();
        
        final HSAdapter api = HSAdapterFactory.newInstance(adminUserHandle, adminUserHandleIndex, privateKeyFile, handleAdminPassword);
        return api;
    }
}

