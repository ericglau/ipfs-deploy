package org.apache.maven.wagon.providers.ipfs;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.maven.wagon.AbstractWagon;
import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.InputData;
import org.apache.maven.wagon.OutputData;
import org.apache.maven.wagon.PathUtils;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.StreamWagon;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.WagonConstants;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authentication.AuthenticationInfo;
import org.apache.maven.wagon.authorization.AuthorizationException;
import org.apache.maven.wagon.repository.RepositoryPermissions;
import org.apache.maven.wagon.resource.Resource;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * IpfsWagon
 *
 *
 * @plexus.component role="org.apache.maven.wagon.Wagon"
 * role-hint="ipfs"
 * instantiation-strategy="per-lookup"
 */
public class IpfsWagon
    extends AbstractWagon
{

    private IPFS ipfs;


    @Override
    protected void openConnectionInternal()
        throws ConnectionException, AuthenticationException
    {
        ipfs = new IPFS("ipfs.infura.io", 5001, "/api/v0/", true);
    }

    @Override
    public void get(String resourceName, File destination)
            throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException {
                System.out.println("GETTING FOR REPO " + repository + " resource " + resourceName + " at Dest " + destination);
                Multihash filePointer = Multihash.fromBase58("QmPZ9gcCEpqKTo6aq61g2nXGUhM4iCL3ewB6LDXZCtioEB");
                try {
                    byte[] fileContents = ipfs.cat(filePointer);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

    }

    @Override
    public boolean getIfNewer(String resourceName, File destination, long timestamp)
            throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException {
        // TODO Auto-generated method stub
        System.out.println("GETTING IF NEWER FOR " + resourceName + " at Dest " + destination);
        return false;
    }

    @Override
    public void put(File source, String destination)
            throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException {
                NamedStreamable.FileWrapper nsfile = new NamedStreamable.FileWrapper(source);
                try {
                    MerkleNode addResult = ipfs.add(nsfile).get(0);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
    }

    @Override
    protected void closeConnection() throws ConnectionException {
        // TODO Auto-generated method stub
    }


}
