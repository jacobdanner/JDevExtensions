package com.jacobd.j1.groovy.wizard;

import com.jacobd.j1.groovy.GroovyEnablerBundle;
import com.jacobd.j1.groovy.GroovyNode;
import oracle.ide.Context;
import oracle.ide.net.DefaultNameGenerator;
import oracle.ide.net.NameGenerator;
import oracle.jdeveloper.builder.file.FileBuilderModel;

/**
 * @author jdanner
 */
public class GroovyScriptBuilderModel extends FileBuilderModel
{
  public GroovyScriptBuilderModel(Context ctx)
  {
    super(ctx, GroovyNode.EXT);
  }

  // in case someone uses gsp, or some other groovy extension
  public GroovyScriptBuilderModel(Context ctx, String ext)
  {
    super(ctx, ext);
  }

  @Override
  public String getFileType()
  {
    // we can use this if we use
    return GroovyEnablerBundle.get("GROOVY_SCRIPT_BUILDER_TITLE");
  }

  @Override
  public String getBuilderType()
  {
    return GroovyEnablerBundle.get("GROOVY_SCRIPT_BUILDER_TITLE");
  }

  @Override
  protected String getBaseName()
  {
    NameGenerator ng = new DefaultNameGenerator("Unknown", GroovyNode.EXT);
    return ng.nextName();
  }

  @Override
  public String getFileExtension()
  {
    return GroovyNode.EXT;
  }
  
  
}
