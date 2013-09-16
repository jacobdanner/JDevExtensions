package com.jacobd.j1.groovy;

import com.jacobd.j1.groovy.icons.GroovyIcon;
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
