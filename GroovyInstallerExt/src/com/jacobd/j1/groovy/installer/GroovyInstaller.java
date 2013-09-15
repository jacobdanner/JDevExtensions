package com.jacobd.j1.groovy.installer;

import com.jacobd.j1.groovy.InstallerConstants;

import oracle.bali.ewt.dialog.JEWTDialog;
import oracle.ide.ExtensionRegistry;
import oracle.ide.Ide;
import oracle.ide.net.URLFactory;
import oracle.ide.net.URLFileSystem;
import oracle.ide.webbrowser.ProxyOptions;
import oracle.ide.webupdate.PostStartupHook;
import oracle.javatools.dialogs.progress.DeterminateProgressMonitor;

import java.io.*;

import java.net.URISyntaxException;
import java.net.URL;

import java.util.Properties;
import java.util.logging.Logger;

public class GroovyInstaller implements PostStartupHook
{
  private static final Logger LOG = Logger.getLogger(GroovyInstaller.class.getName());

  static final String EXTENSION_ID = "com.jacobd.j1.groovy.installer";

  private static final String GROOVY_DL_STRING = "http://dist.groovy.codehaus.org/distributions/groovy-sdk-" + InstallerConstants.GROOVY_VERSION + ".zip";

  @Override
  public void install()
  {
    JEWTDialog ask = JEWTDialog.createDialog(Ide.getMainWindow(), "Install Groovy " + InstallerConstants.GROOVY_VERSION, JEWTDialog.BUTTON_OK | JEWTDialog.BUTTON_CANCEL);

    if (ask.runDialog())
    {
      DeterminateProgressMonitor dpm = new DeterminateProgressMonitor(Ide.getMainWindow(), "Installing Groovy "+InstallerConstants.GROOVY_VERSION);
      dpm.start();

      URL groovyDlUrl = URLFactory.newURL(GROOVY_DL_STRING);
      String prodHome = Ide.getProductHomeDirectory();
      LOG.info("Product Home == "+prodHome); // should == jdeveloper/jdev
      File groovyDir = new File(prodHome, InstallerConstants.GROOVY_DIR);
      URL groovyDirUrl = URLFactory.newFileURL(groovyDir);
      if (URLFileSystem.exists(groovyDirUrl) && verifyDirectory(groovyDirUrl) )
      {
        ExtensionRegistry.getExtensionRegistry().getLogger().warning("Unable to create " + groovyDir.getPath());
      }

      File destZip = new File(prodHome, "groovy-sdk-" + InstallerConstants.GROOVY_VERSION + ".zip");
      

      if(!URLFileSystem.exists(URLFactory.newFileURL(destZip)))
      {
      LOG.info("About to Download from "+groovyDirUrl);
      ProxyOptions.getProxyOptions().doTask(new DownloadToFSRunnable(groovyDlUrl, destZip));
      LOG.info("Done downloading "+destZip.getPath());
      }
      verifyDirectory(groovyDirUrl);

      URL groovyUnzipDir = URLFactory.newDirURL(groovyDirUrl, InstallerConstants.GROOVY_DIR+"_"+InstallerConstants.GROOVY_VERSION);
      LOG.info("GROOVY DIR == "+URLFileSystem.toDisplayString(groovyUnzipDir));
      if(!URLFileSystem.exists(groovyUnzipDir))
      {
      UnzipFileRunnable ufr = new UnzipFileRunnable(dpm, URLFactory.newFileURL(destZip), groovyDirUrl);
      ufr.run();
      LOG.info("Done installing Groovy "+InstallerConstants.GROOVY_VERSION);
      }
      
      LOG.info("adding macro property file");
      
      URL oracleHomeURL = URLFactory.newDirURL(Ide.getOracleHomeDirectory());
      //LOG.info("install dir == "+installDir); // should == jdeveloper
      URL jdevGroovyProps = URLFactory.newURL(oracleHomeURL,"ide/macro/jdev.groovy.home.properties");
      LOG.info("Using property file "+URLFileSystem.toDisplayString(jdevGroovyProps));
      Properties prop = new Properties(); //Ide.getOracleHomeDirectory()
      prop.setProperty("jdev.groovy.home", URLFileSystem.toRelativeSpec(oracleHomeURL, groovyUnzipDir));
      prop.setProperty("jdev.groovy.version", InstallerConstants.GROOVY_VERSION);
      try
      {
        prop.store(new FileWriter(new File(jdevGroovyProps.toURI()), false),
                   "Created by plugin " + EXTENSION_ID + "\n");
      }
      catch (IOException e)
      {
        e.printStackTrace();
        LOG.severe(e.getMessage());
      }
      catch (URISyntaxException e)
      {
        e.printStackTrace();
        LOG.severe(e.getMessage());
      }

    }

  }





  /**
   * Verify that the extract directory exists and is usable.
   *
   * @param extractDir the extract directory
   * @return true if we can extract, false otherwise.
   */
  private boolean verifyDirectory(URL extractDir)
  {
    if (URLFileSystem.exists(extractDir))
    {
      JEWTDialog ask = JEWTDialog.createDialog(Ide.getMainWindow(), "Overwrite Groovy Home", JEWTDialog.BUTTON_OK | JEWTDialog.BUTTON_CANCEL);

      if (ask.runDialog())   // NOTRANS
      {
        if (!removeDirectory(extractDir))
        {
          JEWTDialog overwriteFailed = JEWTDialog.createDialog(Ide.getMainWindow(), "Error removing files", JEWTDialog.BUTTON_OK );
          overwriteFailed.runDialog();

          // We really want to uninstall the extension at this point, but
          // that's currently not possible.
          return false;
        }
      } else
      {
        return false;
      }
    }
    return URLFileSystem.mkdirs(extractDir);
  }




  /**
   * Remove a directory recursively.
   *
   * @param url the URL of a directory.
   */
  private boolean removeDirectory(URL url)
  {
    URL[] files = URLFileSystem.list(url);
    for (int i = 0; i < files.length; i++)
    {
      URL child = files[i];
      if (URLFileSystem.isDirectory(child))
      {
        if (!removeDirectory(child))
        {
          return false;
        }
      }
      if (!URLFileSystem.delete(child))
      {
        return false;
      }
    }
    return true;
  }


}
