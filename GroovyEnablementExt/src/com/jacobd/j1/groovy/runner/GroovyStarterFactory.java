package com.jacobd.j1.groovy.runner;

import com.jacobd.j1.groovy.GroovyNode;
import oracle.ide.model.Node;
import oracle.ide.runner.AbstractStarterFactory;
import oracle.ide.runner.RunProcess;
import oracle.ide.runner.Starter;
import oracle.jdeveloper.runner.JRunProcess;
import oracle.jdeveloper.runner.JStarterFactory;

import java.util.List;

/**
 * @author jdanner
 */
public class GroovyStarterFactory extends AbstractStarterFactory
    implements JStarterFactory
{

  public GroovyStarterFactory()
  {
  }

  public String getName()
  {
    return this.getClass().getName();
  }

  public Object canStart(RunProcess runProcess, Node node, List list)
  {
    JRunProcess jRunProcess;
    if (runProcess instanceof JRunProcess)
    {
      jRunProcess = (JRunProcess) runProcess;
    }
    return node;
  }

  public Starter createStarter(RunProcess runProcess, Node node, Object object)
  {
    Starter starter = null;
    if (object instanceof GroovyNode)
    {
      starter = new GroovyStarter((JRunProcess) runProcess, (GroovyNode) object);
    }
    return starter;
  }
