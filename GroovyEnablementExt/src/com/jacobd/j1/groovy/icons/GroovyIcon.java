package com.jacobd.j1.groovy.icons;

import com.jacobd.j1.groovy.GroovyEnablerBundle;

import javax.swing.*;

/**
 * @author jdanner
 */
public class GroovyIcon
{
  private static ImageIcon nodeIcon;
  private static ImageIcon logoIcon;

  private GroovyIcon()
  {
  }

  public static ImageIcon getNodeIcon()
  {
    if (nodeIcon == null)
    {
      nodeIcon = new ImageIcon(GroovyIcon.class.getResource(GroovyEnablerBundle.get("GROOVY_ICN_NODE_16X16")));
    }
    return nodeIcon;
  }

  public static ImageIcon getFullNodeIcon()
  {
    if (nodeIcon == null)
    {
      nodeIcon = new ImageIcon(GroovyIcon.class.getResource(GroovyEnablerBundle.get("GROOVY_ICN_NODE")));
    }
    return nodeIcon;
  }


  public static ImageIcon getLogoIcon()
  {
    if (logoIcon == null)
    {
      logoIcon = new ImageIcon(GroovyIcon.class.getResource(GroovyEnablerBundle.get("GROOVY_ICN_LOGO")));
    }
    return logoIcon;
  }



}
