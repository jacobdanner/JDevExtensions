package com.jacobd.j1.groovy;

import oracle.ide.Ide;
import oracle.ide.net.URLFactory;

import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * @author jdanner
 */
public class GroovyEnablerBundle
{

  private GroovyEnablerBundle()
  {
  }

  private static final String RES_BUNDLE_PROP_FILE = "com.jacobd.j1.groovy.EnablerRes";
  private static ResourceBundle instance;

  public static final ResourceBundle getBundle()
  {
    if (instance == null)
    {
      instance = ResourceBundle.getBundle(RES_BUNDLE_PROP_FILE);
    }
    return instance;
  }

  public static String get(String key)
  {
    return getBundle().getString(key);
  }

  public static String res(String key)
  {
    return get(key);
  }

  public static String format(String key, Object arguments[])
  {
    return MessageFormat.format(get(key), arguments);
  }


  public static ImageIcon getGroovyNodeIcon()
  {
    return getImageIcon(getGroovyNodeIconFile());
  }

  public static ImageIcon getGroovyLogoIcon()
  {
    return getImageIcon(getGroovyLogoIconFile());
  }

  private static ImageIcon getImageIcon(File f)
  {
    return new ImageIcon(fileToURL(f));
  }

  private static URL fileToURL(File f)
  {
    return URLFactory.newFileURL(f);
  }

  private static File getGroovyNodeIconFile()
  {
    final String iconNodeText = get("GROOVY_ICN_NODE");
    File iconNodeFile = new File(Ide.getProductHomeDirectory(), iconNodeText);
    return iconNodeFile;
  }


  private static File getGroovyLogoIconFile()
  {
    final String logoFileText = get("GROOVY_ICN_LOGO");
    File logoFile = new File(Ide.getProductHomeDirectory(), logoFileText);
    return logoFile;
  }


}
