package com.jacobd.j1.groovy.runner;

import com.jacobd.j1.groovy.GroovyEnablerBundle;
import com.jacobd.j1.groovy.GroovyNode;
import com.jacobd.j1.groovy.icons.GroovyIcon;
import oracle.ide.Context;
import oracle.ide.model.Node;
import oracle.ide.model.NodeFactory;
import oracle.ide.model.Project;
import oracle.ide.net.URLFileSystem;
import oracle.ide.runner.RunProcess;
import oracle.ide.runner.RunProcessListener;
import oracle.ide.util.Assert;
import oracle.jdeveloper.runner.JRunProcess;

import javax.swing.*;
import java.net.URL;

/**
 * @author jdanner
 */
public class GroovyRunProcess
    extends JRunProcess
{
  protected Object waitLock = new Object();
  protected boolean started = false;
  protected String name;
  protected Node node;


  public GroovyRunProcess(Context context, Node groovyNode, String name)
  {
    super(context);
    this.name = name;
    lockCompiler = false;
    logExit = true;
    logStartDirectory = true;
    logCommandString = true;
    node = groovyNode;
    setLogPageOverride(GroovyRunLogPage.makePage((RunProcess) this, true));
    considerDefaultRunTarget = false;
  }

  public void setRunProcessListener(RunProcessListener runProcessListener)
  {
    this.runProcessListener = runProcessListener;
  }


  protected void determineTargetAndStarter(Class starterFactorySubClass)
  {
    try
    {
      URL groovyScriptUrl = node.getURL();
      if (groovyScriptUrl == null || !URLFileSystem.exists(groovyScriptUrl))
      {
        target = null;
      } else
      {
        target = NodeFactory.findOrCreate(groovyScriptUrl);
      }

      Project project = context == null ? null : context.getProject();
      starter = getStarterForTarget(project, target, starterFactorySubClass);

      if (starter == null)
      {
        starter = new GroovyStarter(this, (GroovyNode) target);
      }
    } catch (Exception e)
    {
      Assert.printStackTrace(e);
      starter = new GroovyStarter(this, (GroovyNode) target);
    }
  }


  public int waitUntilFinished()
      throws InterruptedException
  {
    synchronized (waitLock)
    {
      if (!started)
      {
        waitLock.wait();
      }
    }
    return starter == null ? -1 : starter.waitForProcess();
  }

  protected boolean startTarget()
  {
    final boolean value = super.startTarget();
    synchronized (waitLock)
    {
      started = true;
      waitLock.notifyAll();
    }
    return true;
  }

  protected boolean compile()
  {
    return true;
  }

  protected Class getStarterFactorySubClass()
  {
    return GroovyStarterFactory.class;
  }

  public String getShortLabel()
  {
    return name;
  }

  public String getProcessLabelPrefix()
  {
    return getShortLabel();
  }

  public String getShortLabelWithPrefix()
  {
    return getShortLabel();
  }

  public String getLongLabel()
  {
    return getShortLabel();
  }

  public Icon getIcon()
  {
    return GroovyIcon.getNodeIcon();
  }

  public void terminate()
  {
    super.terminate();
    getLogPage().log("Groovy Script Terminated");
    //GroovyEnablerBundle.get("Groovy")
  }
}