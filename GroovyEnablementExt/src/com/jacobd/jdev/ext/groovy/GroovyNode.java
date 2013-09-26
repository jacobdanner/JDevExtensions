package com.jacobd.jdev.ext.groovy;

import com.jacobd.jdev.ext.groovy.icons.GroovyIcon;
import oracle.ide.model.TextNode;

import javax.swing.*;

/**
 * @author jdanner
 */
public class GroovyNode extends TextNode
{
  public static final String EXT = ".groovy";

  public GroovyNode()
  {
  }

  /**
   * {@link oracle.ide.model.Displayable} interface method.  The Node class returns
   * a generic icon.
   */
  @Override
  public Icon getIcon()
  {
    return GroovyIcon.getNodeIcon();
  }


}
