package com.jacobd.jdev.ext.groovy.installer;

import com.jacobd.jdev.ext.groovy.InstallerConstants;

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
      DeterminateProgressMonitor dpm = new DeterminateProgressMonitor(Ide.getMainWindow(), "Installing Groovy " + InstallerConstants.GROOVY_VERSION);
      dpm.start();
      dpm.setCancellable(true);
      dpm.setCloseOnFinish(true);
      dpm.setProgress(5);
      URL groovyDlUrl = getGroovyDLURL();
      String prodHome = getIdeProductHomeDirectory();
      LOG.info("Product Home == " + prodHome); // should == jdeveloper/jdev
      File groovyDir = getGroovyInstallDir(prodHome);
      URL groovyDirUrl = getGroovyInstallDirUrl(prodHome);
      File destZip = getGroovyZipDestFile(prodHome);
      URL groovyUnzipDir = getGroovyUnzipDirectory(groovyDirUrl);
      URL oracleHomeURL = getIdeOracleHomeDirectory();
      URL jdevGroovyProps = getGroovyMacroPropertiesFile(oracleHomeURL);

      // make prerequisite directories
      makePrequisiteDirectories(getGroovyInstallDirUrl(prodHome));

      if (!URLFileSystem.exists(getGroovyZipDestUrl(prodHome)))
      {
        doDownloadtoFile(getGroovyDLURL(), getGroovyInstallDirUrl(prodHome), getGroovyZipDestFile(prodHome));
        verifyDirectory(getGroovyDLURL());
      }  else {
        LOG.info("Groovy Zip "+ getGroovyZipDestUrl(prodHome)+" using that value");
      }
      dpm.incProgress();

      if (!URLFileSystem.exists(getGroovyUnzipDirectory(groovyDirUrl)))
      {
        LOG.info("GROOVY DIR == " + URLFileSystem.toDisplayString(getGroovyUnzipDirectory(groovyDirUrl)));
        unzipFileToDir(dpm, getGroovyInstallDirUrl(prodHome), getGroovyZipDestFile(prodHome));
      } else {
        LOG.info("Groovy looks to be unzipped already at: "+ getGroovyUnzipDirectory(groovyDirUrl));
      }

      dpm.incProgress();
      if (!URLFileSystem.exists(jdevGroovyProps))
      {
        //LOG.info("install dir == "+installDir); // should == jdeveloper
        createMacroPropertyFile(getGroovyUnzipDirectory(getGroovyInstallDirUrl(prodHome)),
            getIdeOracleHomeDirectory(),
            getGroovyMacroPropertiesFile(getIdeOracleHomeDirectory()));
      }  else {
        LOG.info("jdev macro properties already exists at: " +URLFileSystem.getPath(jdevGroovyProps));
      }
      dpm.incProgress();
      dpm.finish();
    }

  }

  private URL getGroovyMacroPropertiesFile(URL oracleHomeURL)
  {
    return URLFactory.newURL(oracleHomeURL, "ide/macros/jdev.groovy.home.properties");
  }

  private URL getIdeOracleHomeDirectory()
  {
    return URLFactory.newDirURL(Ide.getOracleHomeDirectory());
  }

  private URL getGroovyUnzipDirectory(URL groovyDirUrl)
  {
    return URLFactory.newDirURL(groovyDirUrl, InstallerConstants.GROOVY_DIR + "-" + InstallerConstants.GROOVY_VERSION);
  }

  private URL getGroovyZipDestUrl(String prodHome)
  {
    return URLFactory.newFileURL(getGroovyZipDestFile(prodHome));
  }

  private File getGroovyZipDestFile(String prodHome)
  {
    return new File(prodHome, "groovy-sdk-" + InstallerConstants.GROOVY_VERSION + ".zip");
  }

  private String getIdeProductHomeDirectory()
  {
    return Ide.getProductHomeDirectory();
  }

  public static URL getGroovyInstallDirUrl(String prodHome)
  {
    return URLFactory.newFileURL(getGroovyInstallDir(prodHome));
  }

  public static File getGroovyInstallDir(String prodHome)
  {
    return new File(prodHome, InstallerConstants.GROOVY_DIR);
  }

  private URL getGroovyDLURL()
  {
    return URLFactory.newURL(GROOVY_DL_STRING);
  }

  protected void makePrequisiteDirectories(URL groovyDirUrl)
  {
    if (!URLFileSystem.exists(groovyDirUrl))
    {
      if( verifyDirectory(groovyDirUrl))
      {
        ExtensionRegistry.getExtensionRegistry().getLogger().warning("Unable to create " + URLFileSystem.getPath(groovyDirUrl));
      }
    }
  }

  protected void createMacroPropertyFile(URL groovyUnzipDir, URL oracleHomeURL, URL propertyFile)
  {
    LOG.info("adding macro property file");
    LOG.info("Using property file " + URLFileSystem.toDisplayString(propertyFile));
    Properties prop = new Properties(); //Ide.getOracleHomeDirectory()
    String relativePathToGroovy = URLFileSystem.toRelativeSpec(groovyUnzipDir, oracleHomeURL);
    LOG.info("groovyDirPath == "+relativePathToGroovy);
    prop.setProperty("jdev.groovy.home", "file\\:../../../../../"+relativePathToGroovy);
    prop.setProperty("jdev.groovy.version", InstallerConstants.GROOVY_VERSION);
    
    
    
    try
    {
      prop.store(new FileWriter(new File(propertyFile.toURI()), false),
          "Created by plugin " + EXTENSION_ID + "\n");
    } catch (IOException e)
    {
      e.printStackTrace();
      LOG.severe(e.getMessage());
    } catch (URISyntaxException e)
    {
      e.printStackTrace();
      LOG.severe(e.getMessage());
    }
  }

  protected void unzipFileToDir(DeterminateProgressMonitor dpm, URL groovyDirUrl, File destZip)
  {
    UnzipFileRunnable ufr = new UnzipFileRunnable(dpm, URLFactory.newFileURL(destZip), groovyDirUrl);
    ufr.run();
    LOG.info("Done installing Groovy " + InstallerConstants.GROOVY_VERSION);
  }

  protected void doDownloadtoFile(URL groovyDlUrl, URL groovyDirUrl, File destZip)
  {
    LOG.info("About to Download from " + groovyDirUrl);
    ProxyOptions.getProxyOptions().doTask(new DownloadToFSRunnable(groovyDlUrl, destZip));
    LOG.info("Done downloading " + destZip.getPath());
  }


  /**
   * Verify that the extract directory exists and is usable.
   *
   * @param extractDir the extract directory
   * @return true if we can extract, false otherwise.
   */
  protected boolean verifyDirectory(URL extractDir)
  {
    if (URLFileSystem.exists(extractDir))
    {
      JEWTDialog ask = JEWTDialog.createDialog(Ide.getMainWindow(), "Overwrite Groovy Home", JEWTDialog.BUTTON_OK | JEWTDialog.BUTTON_CANCEL);

      if (ask.runDialog())   // NOTRANS
      {
        if (!removeDirectory(extractDir))
        {
          JEWTDialog overwriteFailed = JEWTDialog.createDialog(Ide.getMainWindow(), "Error removing files", JEWTDialog.BUTTON_OK);
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
  protected boolean removeDirectory(URL url)
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
