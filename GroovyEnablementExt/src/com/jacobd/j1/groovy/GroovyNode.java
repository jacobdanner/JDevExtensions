package com.jacobd.j1.groovy;

import oracle.ide.model.TextNode;

import javax.swing.*;

/**
 * @author jdanner
 */
public class GroovyNode extends TextNode
{
  private static Icon largeIcon;
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
    if (largeIcon == null)
    {
      // groovy icon path from install
      largeIcon = GroovyEnablerBundle.getGroovyNodeIcon();
    }
    return largeIcon;
  }


}
