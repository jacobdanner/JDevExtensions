package com.jacobd.j1.groovy.wizard;

import oracle.ide.Context;
import oracle.jdeveloper.builder.file.FileBuilderModel;
import oracle.jdevimpl.groovy.GroovyBundle;
import oracle.jdevimpl.groovy.model.GroovyNode;

/**
 * @author jdanner
 */
public class GroovyFileBuilderModel extends FileBuilderModel
{
  public GroovyFileBuilderModel(Context ctx)
  {
    super(ctx, GroovyNode.EXT);
  }

  public GroovyFileBuilderModel(Context ctx, String ext)
  {
    super(ctx, ext);
  }

  @Override
  public String getFileType()
  {
    return GroovyBundle.get(GroovyBundle.GROOVY_SCRIPT_LABEL);
  }

  @Override
  public String getBuilderType()
  {
    return GroovyBundle.get(GroovyBundle.GROOVY_SCRIPT_LABEL);
  }

  @Override
  protected String getBaseName()
  {
    return "groovy_is_great";
  }
}
