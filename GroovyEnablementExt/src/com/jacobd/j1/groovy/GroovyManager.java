package com.jacobd.j1.groovy;

import com.jacobd.j1.groovy.installer.GroovyInstaller;
import oracle.ide.Context;
import oracle.ide.Ide;
import oracle.ide.model.SingletonProvider;
import oracle.ide.net.URLFactory;
import oracle.ide.net.URLFileSystem;

import java.io.File;
import java.net.URL;

/**
 * @author jdanner
 */
public abstract class GroovyManager
{

  public static GroovyManager getGroovyManager()
  {
    return SingletonProvider.find(GroovyManager.class);
  }


  public abstract URL getGroovyHome();

  public abstract int runGroovy(Context context, URL groovyNodeUrl);
  public abstract void runGroovyNonBlocking(Context context, URL groovyNodeUrl);


}
