package com.jacobd.j1.groovy.wizard;

import oracle.ide.Context;
import oracle.jdeveloper.builder.AbstractBuilderModel;
import oracle.jdeveloper.builder.file.FileBuilder;
import oracle.jdeveloper.wizard.common.BaliWizardPanel;

import oracle.jdeveloper.wizard.common.BaliWizardState;
import oracle.jdevimpl.groovy.GroovyBundle;

/**
 * @author jdanner
 */
public class GroovyFileBuilder extends FileBuilder
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
    return new com.jacobd.j1.groovy.wizard.GroovyFileBuilderModel(context);
  }

  @Override
  protected BaliWizardPanel buildPanel(Context context, BaliWizardState state)
  {
    return super.buildPanel(context, state);
  }

  @Override
  protected String _getTitleString()
  {
    return GroovyBundle.get(GroovyBundle.GROOVY_SCRIPT_LABEL);
  }

  @Override
  protected String getHeaderDescription()
  {
    // create a groovy script

    return super.getHeaderDescription();
  }

  @Override
  protected String getDialogTitle()
  {
// create a groovy script
    return GroovyBundle.get(GroovyBundle.GROOVY_SCRIPT_LABEL);
  }
}
