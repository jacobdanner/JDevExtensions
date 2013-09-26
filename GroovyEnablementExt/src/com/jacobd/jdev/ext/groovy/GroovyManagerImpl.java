package com.jacobd.jdev.ext.groovy;

import com.jacobd.jdev.ext.groovy.installer.GroovyInstaller;
import com.jacobd.jdev.ext.groovy.runner.GroovyRunProcess;

import oracle.ide.Context;
import oracle.ide.Ide;
import oracle.ide.model.Node;
import oracle.ide.model.NodeFactory;
import oracle.ide.model.NodeUtil;
import oracle.ide.net.URLFactory;
import oracle.ide.net.URLFileSystem;
import oracle.ide.runner.RunProcessListener;

import java.io.File;

import java.net.URL;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author jdanner
 */
public class GroovyManagerImpl
  extends GroovyManager
{
  public static final Logger LOGGER = Logger.getLogger(GroovyManagerImpl.class.getName());
  private static URL GROOVY_HOME;

  public URL getGroovyHome()
  {
    if (GROOVY_HOME == null)
    {
      URL installDir =
        URLFactory.newDirURL(Ide.getProductHomeDirectory()+"/" + InstallerConstants.GROOVY_DIR + "/" +
                             InstallerConstants.GROOVY_DIR + "-" + InstallerConstants.GROOVY_VERSION);
      LOGGER.info("installDir == " + URLFileSystem.getPlatformPathName(installDir));
      GROOVY_HOME = installDir;
    }
    return GROOVY_HOME;
  }

  public int runGroovy(Context context, URL groovyNodeUrl)
  {
    return runGroovyImpl(context, groovyNodeUrl, true);

  }

  public void runGroovyNonBlocking(Context context, URL groovyNodeUrl)
  {
    runGroovyImpl(context, groovyNodeUrl, false);
  }

  /**
   * TODO: add a binding mechanism
   * @param context
   * @param groovyNodeUrl
   * @param block
   * @return
   */
  private int runGroovyImpl(Context context, URL groovyNodeUrl, boolean block)
  {
    final String name = URLFileSystem.getName(groovyNodeUrl);
    final GroovyRunProcess process;
    try
    {
      process = new GroovyRunProcess(context, NodeFactory.findOrCreate(GroovyNode.class, groovyNodeUrl), name);
      final Map<Node, Long> timestampMap = new HashMap<Node, Long>();
      NodeUtil.storeAllTimestamps(timestampMap);
      process.setRunProcessListener(new RunProcessListener()
      {
        public void processFinished(int exitCode)
        {
          NodeUtil.reloadBuffers(timestampMap);
          process.setRunProcessListener(null);
        }
      });
      //if (listener != null)
      //{
      //  process.setMavenListener(listener);
      //}
      process.start();
      if (block)
      {
        try
        {
          return process.waitUntilFinished();
        }
        catch (InterruptedException ie)
        {
        }
      }

    }
    catch (IllegalAccessException e)
    {
      e.printStackTrace();
    }
    catch (InstantiationException e)
    {
      e.printStackTrace();
    }

    return -1;
  }

}
