package com.jacobd.j1.groovy.installer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import oracle.ide.Ide;
import oracle.ide.net.URLFactory;
import oracle.ide.net.URLFileSystem;

import oracle.javatools.dialogs.ExceptionDialog;
import oracle.javatools.dialogs.progress.DeterminateProgressMonitor;


/**
 * Runnable that unzips a zip file.
 * @author jdanner
 */
final class UnzipFileRunnable implements Runnable
{
  private static final Logger LOG = Logger.getLogger( UnzipFileRunnable.class.getName() );


  private final URL zipFile;
  private final URL dirToExtract;
  private final DeterminateProgressMonitor _monitor;
  
  // What portion of the overall progress is unzipping? 
  private static final double ZIP_PORTION = 1.0;
  
  // How often do we Thread.yield() in the loop?
  private static final int YIELD_EVERY = 10;


  UnzipFileRunnable(DeterminateProgressMonitor monitor, URL zipFile, URL dir)
  {
    _monitor = monitor;
    this.zipFile = zipFile;
    dirToExtract = dir;
  }

  public void run()
  {
    try
    {
      _monitor.display();
      if ( unzipFile(zipFile, dirToExtract) )
      {
        LOG.info("Finished unzipping "+zipFile.getPath() +" to "+dirToExtract.getPath());
      }
    }
    catch ( IOException ioe )
    {
      ioe.printStackTrace();
    }
    catch ( RuntimeException re )
    {
      re.printStackTrace();
      ExceptionDialog.showExceptionDialog( 
        Ide.getMainWindow(), re 
      );
    }
    finally
    {
      _monitor.finish();
    }
  }
  

  

  private boolean unzipFile(URL zipFile, URL extractDir)
    throws IOException
  {
    ZipFile zip = null;
     
    try
    {
      zip = new ZipFile( URLFileSystem.getPlatformPathName(zipFile) );
      String baseDir = URLFileSystem.getPlatformPathName(extractDir);
      
      int i = 0;
      
      double progressStep =  (ZIP_PORTION * (1.0 / (double)zip.size() )) * 100.0;
      

      double progress = 0.0;
      for ( Enumeration e = zip.entries(); e.hasMoreElements(); )
      {
        i++;
        if ( i % YIELD_EVERY == 0 )
        {
          Thread.yield();
        }
        
        ZipEntry entry = (ZipEntry) e.nextElement(); 

        if ( _monitor.isCanceled() )
        {
          return false;
        }
        
        File outFile = new File( baseDir + File.separator + entry.getName() );
        
        if ( !outFile.exists() ) 
        {
          if ( entry.isDirectory() )
          {
            if ( !outFile.mkdirs() )
            {
              throw new IOException( "Cannot create directory " + outFile );
            }
          }
          else
          {
            if ( !outFile.getParentFile().exists() && 
                !outFile.getParentFile().mkdirs() )
            {
              throw new IOException( "Cannot create directory " + outFile.getParentFile() );
            }
            
            try {
              InputStream inStream = zip.getInputStream( entry );
              URLFileSystem.copy( inStream, URLFactory.newURL( outFile.getPath() ));
              LOG.info( "Unzipped " + outFile );
              inStream.close();  
            }
            catch (IOException ioe) {      
              LOG.log(Level.SEVERE, "closing input stream from zip file", ioe);
            }
          }
        }
        progress += progressStep;
        _monitor.setProgress( (int) progress );
      }
    }
    finally
    {
      if ( zip != null )
      {
        zip.close();
      }
      _monitor.setProgress( (int) (ZIP_PORTION * 100.0) );
    }
    return true;
  }
}