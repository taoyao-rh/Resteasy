package org.jboss.resteasy.test.providers.sse;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * A ControllableProxy.
 *
 * @author <a href="pslavice@jboss.com">Pavel Slavicek</a>
 * @version $Revision: 1.1 $
 */
public interface ControllableProxy extends Remote
{
   /**
    * Stops proxy, disconnects all connection and destroys
    * ports
    */
   void stop() throws RemoteException;

   /**
    * Starts proxy, disconnects all connection and destroys
    * ports
    */
   void start() throws RemoteException;

   /**
    * Sets flag for proxy server termination
    */
   void setTerminateRequest() throws RemoteException;

   /**
    * Return flag of termination request
    *
    * @return true is there request for termination
    */
   boolean isTerminateRequest() throws RemoteException;

}