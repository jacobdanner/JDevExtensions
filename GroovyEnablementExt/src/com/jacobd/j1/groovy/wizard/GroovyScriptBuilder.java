package com.jacobd.j1.groovy.wizard;

import com.jacobd.j1.groovy.GroovyEnablerBundle;
import oracle.ide.Context;
import oracle.jdeveloper.builder.AbstractBuilderModel;
import oracle.jdeveloper.builder.file.FileBuilder;
import oracle.jdeveloper.builder.file.FileBuilderPanel;
import oracle.jdeveloper.wizard.common.BaliWizardPanel;

import oracle.jdeveloper.wizard.common.BaliWizardState;
import oracle.jdevimpl.groovy.GroovyBundle;

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
    return new GroovyScriptBuilderModel(context);
  }

  @Override
  protected BaliWizardPanel buildPanel(Context context, BaliWizardState state)
  {
    return super.buildPanel(context, state);
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
}
