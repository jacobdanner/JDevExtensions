package com.jacobd.j1.groovy.wizard;

import com.jacobd.j1.groovy.GroovyEnablerBundle;

import java.awt.Image;

import javax.swing.Icon;

import com.jacobd.j1.groovy.icons.GroovyIcon;
import oracle.ide.Context;

import oracle.javatools.icons.OracleIcons;

import oracle.jdeveloper.builder.AbstractBuilderModel;
import oracle.jdeveloper.builder.file.FileBuilder;
import oracle.jdeveloper.wizard.common.BaliWizardPanel;

import oracle.jdeveloper.wizard.common.BaliWizardState;

/**
 * @author jdanner
 */
public class GroovyScriptBuilder extends FileBuilder
{

  @Override
  protected String getHelpID()
  {
    // TODO: implement me
    return null;
  }

  @Override
  protected AbstractBuilderModel _buildModel(Context context)
  {
    GroovyScriptBuilderModel gsbm = new GroovyScriptBuilderModel(context);
    return gsbm;
  }

  @Override
  protected BaliWizardPanel buildPanel(Context context, BaliWizardState state)
  {
    BaliWizardPanel bwp =  super.buildPanel(context, state);
    return bwp;
  }

  @Override
  protected String _getTitleString()
  {
    return GroovyEnablerBundle.get("GROOVY_SCRIPT_BUILDER_TITLE");
  }

  @Override
  protected String getHeaderDescription()
  {
    return GroovyEnablerBundle.get("GROOVY_SCRIPT_BUILDER_HEADER_DESCRIPTION");
  }

  @Override
  protected String getDialogTitle()
  {
    return GroovyEnablerBundle.get("GROOVY_SCRIPT_BUILDER_LABEL");
  }
  

  @Override
  public Icon getIcon()
  {
    return GroovyIcon.getNodeIcon();
  }

  @Override
  protected Image getHeaderImage()
  {
    return GroovyIcon.getFullNodeIcon().getImage();
  }
}
