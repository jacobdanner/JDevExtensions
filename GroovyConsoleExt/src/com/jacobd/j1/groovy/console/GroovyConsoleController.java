package com.jacobd.j1.groovy.console;

import java.util.Collection;

import oracle.ide.Context;
import oracle.ide.controller.Controller;
import oracle.ide.controller.IdeAction;
import oracle.ide.externaltools.Availability;
import oracle.ide.externaltools.ExternalProgramToolProperties;
import oracle.ide.externaltools.ExternalTool;
import oracle.ide.externaltools.ExternalToolBaseProperties;
import oracle.ide.externaltools.ExternalToolFactory;
import oracle.ide.externaltools.ExternalToolManager;
import oracle.ide.externaltools.IntegrationPoint;

public class GroovyConsoleController
  implements Controller
{
  private static final String TOOL_ID = "com.jacobdanner.j1.groovy.Groovy";
  private static final String SYSTEM_GROOVY_HOME = System.getenv("GROOVY_HOME");

  public boolean handleEvent(IdeAction ideAction, Context context)
  {
    createGroovyConsoleTool();
    return true;
  }

  public boolean update(IdeAction ideAction, Context context)
  {
    return true;
  }

  private void createGroovyConsoleTool()
  {
    if (toolExistsAlready())
      return;

    ExternalToolManager.getExternalToolManager().addExternalTool(createTool());
  }

  private boolean toolExistsAlready()
  {
    return ExternalToolManager.scannerTags(allTools()).contains(TOOL_ID);
  }

  private Collection<ExternalTool> allTools()
  {
    return ExternalToolManager.getExternalToolManager().tools();
  }

  private ExternalTool createTool()
  {
    ExternalTool tool = ExternalToolFactory.getInstance().createProgramTool();

    configureBaseProperties(tool);
    configureProgramProperties(tool);

    return tool;
  }


  private void configureBaseProperties(ExternalTool tool)
  {
    ExternalToolBaseProperties props = ExternalToolBaseProperties.getInstance(tool);

    props.setScannerTag(TOOL_ID); // Important, this is how we avoid creating duplicates.
    props.setCaption("Sample Tool (from the ESDK ExternalToolCreation sample)");
    props.setAvailability(Availability.ALWAYS);
    props.setIntegrated(IntegrationPoint.TOOLS_MENU, true);
    props.setIntegrated(IntegrationPoint.NAVIGATOR_MENU, true);
  }

  private void configureProgramProperties(ExternalTool tool)
  {
    ExternalProgramToolProperties props = ExternalProgramToolProperties.getInstance(tool);
    
    props.setExecutable("c:\\path\\to\\some\\program.exe");
    props.setRunDirectory("c:\\somepath");
    props.setArguments("${file.path}");
  }
}


