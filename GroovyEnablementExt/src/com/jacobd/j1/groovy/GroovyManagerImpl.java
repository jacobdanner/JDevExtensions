package com.jacobd.j1.groovy;

import com.jacobd.j1.groovy.installer.GroovyInstaller;
import oracle.ide.Context;
import oracle.ide.Ide;
import oracle.ide.model.Node;
import oracle.ide.model.NodeUtil;
import oracle.ide.net.URLFileSystem;
import oracle.ide.runner.RunProcessListener;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jdanner
 */
public class GroovyManagerImpl extends GroovyManager
{

  public  URL getGroovyHome()
  {
    return GroovyInstaller.getGroovyInstallDirUrl(Ide.getProductHomeDirectory());
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
    final MavenRunProcess process = new MavenRunProcess(mavenConfig, context, name);
    if (reloadBuffers)
    {
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
    }
    if (listener != null)
    {
      process.setMavenListener(listener);
    }
    process.start();
    if (block)
    {
      try
      {
        return process.waitUntilFinished();
      } catch (InterruptedException ie)
      {
      }
    }
    return -1;
  }

}
