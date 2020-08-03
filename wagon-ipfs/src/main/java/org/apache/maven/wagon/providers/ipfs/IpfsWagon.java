package org.apache.maven.wagon.providers.ipfs;

import org.apache.maven.wagon.providers.http.HttpWagon;
import org.apache.maven.wagon.repository.Repository;

public class IpfsWagon
    extends HttpWagon
{
    /**
     * getUrl
     * Implementors can override this to remove unwanted parts of the url such as role-hints
     *
     * @param repository
     * @return
     */
    @Override
    protected String getURL( Repository repository )
    {
        return repository.getUrl().replace("ipfs://", "https://ipfs.io/ipfs/");
    }

}
