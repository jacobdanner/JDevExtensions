package com.jacobd.j1.groovy.runner;

import com.jacobd.j1.groovy.GroovyEnablerBundle;
import com.jacobd.j1.groovy.GroovyManager;
import com.jacobd.j1.groovy.GroovyNode;
import oracle.ide.Context;
import oracle.ide.Ide;
import oracle.ide.externaltools.macro.MacroRegistry;
import oracle.ide.log.LogPage;
import oracle.ide.net.URLFactory;
import oracle.ide.net.URLFileSystem;
import oracle.ide.net.URLPath;
import oracle.ideimpl.externaltools.macro.MacroRegistryImpl;
import oracle.jdeveloper.library.JLibraryManager;
import oracle.jdeveloper.library.Library;
import oracle.jdeveloper.runner.JRunProcess;
import oracle.jdeveloper.runner.JStarter;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * TODO: need to do something about cmd line path length for windows (256 limit)?
 * TODO: replace with super() methods from JStarter if at all possible.
 * {@link oracle.ide.runner.Starter} implementation for Groovy.
 *
 * This starter 'wraps' the groovy script in $GROOVY_HOME/bin/groovy
 *
 * @author <a href="mailto:jacob.danner@oracle.com">Jacob Danner</a>
 */
public class GroovyStarter extends JStarter
{

  protected static final Logger LOG =
      Logger.getLogger(GroovyStarter.class.getName());
  protected GroovyNode node;
  protected MacroRegistry macroRegistry = new MacroRegistryImpl();

  protected GroovyStarter(JRunProcess jrunProcess, GroovyNode node)
  {
    super(jrunProcess, null);
    this.node = node;
  }

  public void addGroovyJavaClasspath(List<String> list)
  {
    List cPathlist = new ArrayList<String>();

    Library simpleGroovy = JLibraryManager.findLibrary(
        GroovyEnablerBundle.get("GROOVY_VERSION_LIBRARY_NAME_SIMPLE"));
    URLPath gClassPath = simpleGroovy.getClassPath();
    List<URL> gClasspathUrls = gClassPath.asList();
    LOG.info("GROOVY LIBRARY");
    StringBuilder sb = new StringBuilder();
    for (URL gC : gClasspathUrls)
    {
      LOG.info(URLFileSystem.getPlatformPathName(gC));
      String jarPath  = URLFileSystem.getPath(gC);
      //sb.append();
      
      String expandedPath = macroRegistry.expand(jarPath, Context.newIdeContext());
      sb.append(expandedPath);

      sb.append(File.pathSeparator);
    }

    String projectClasspath = getClassPath();
    list.add("-classpath");
    list.add(sb.toString()+projectClasspath);
  }

  @Override
  public String[] getStartCommand()
  {
    List list = new ArrayList();
    addJavaExecutableName(list);
    addGroovyJavaClasspath(list);
    //addClassPathOption(list);
    addBootClassPathOption(list);
    addJavaOptions(list);
    addGroovyExecOptions(list);
    LOG.info("Going to Execute: "+list.toString());
    return (String[]) list.toArray(new String[list.size()]);

  }


  private void addGroovyExecOptions(List list)
  {
    // startGroovy runs the following:
    //  set STARTER_CLASSPATH=%GROOVY_HOME%\lib\groovy-2.0.5.jar
   /*

set STARTER_MAIN_CLASS=org.codehaus.groovy.tools.GroovyStarter
set STARTER_CONF=%GROOVY_HOME%\conf\groovy-starter.conf

set GROOVY_OPTS="-Xmx512m"
set GROOVY_OPTS=%GROOVY_OPTS% -Dprogram.name="%PROGNAME%"
set GROOVY_OPTS=%GROOVY_OPTS% -Dgroovy.home="%GROOVY_HOME%"
if not "%TOOLS_JAR%" == "" set GROOVY_OPTS=%GROOVY_OPTS% -Dtools.jar="%TOOLS_JAR%"
set GROOVY_OPTS=%GROOVY_OPTS% -Dgroovy.starter.conf="%STARTER_CONF%"
set GROOVY_OPTS=%GROOVY_OPTS% -Dscript.name="%GROOVY_SCRIPT_NAME%"

if exist "%USERPROFILE%/.groovy/postinit.bat" call "%USERPROFILE%/.groovy/postinit.bat"
@rem Execute Groovy
"%JAVA_EXE%" %GROOVY_OPTS% %JAVA_OPTS%
-classpath "%STARTER_CLASSPATH%" %STARTER_MAIN_CLASS%
--main %CLASS%
--conf "%STARTER_CONF%"
--classpath "%CP%" %CMD_LINE_ARGS%
    */
    list.add("-Dprogram.name=groovy");
    list.add("-Dgroovy.home="+getGroovyHome());
    list.add("org.codehaus.groovy.tools.GroovyStarter");
    list.add("-Dgroovy.starter.conf="+getGrooyStarterConf(getGroovyHome()));


    //groovy runs the following:
    // "%DIRNAME%\startGroovy.bat" "%DIRNAME%" groovy.ui.GroovyMain %*
    list.add("--main");
    list.add("groovy.ui.GroovyMain");

    list.add(node.getLongLabel());
  }

  private String getGrooyStarterConf(String groovyHome)
  {
    URL starterConf =  URLFactory.newFileURL(getGroovyHome()+File.separator+"conf" + File.separator + "groovy-starter.conf");
    return URLFileSystem.getPlatformPathName(starterConf);
  }

  private String getGroovyHome()
  {
    return URLFileSystem.getPath(GroovyManager.getGroovyManager().getGroovyHome());
  }

  @Override
  public File getStartDirectory()
  {

    try
    {
      return new File(URLFileSystem.getParent(node.getURL()).toURI());
    } catch (URISyntaxException e)
    {
      e.printStackTrace();
    }
    return new File(Ide.getActiveProject().getBaseDirectory());
  }

  @Override
  public boolean canStart(List errors)
  {
    if (errors != null && !errors.isEmpty())
    {
      errors.clear();
      return false;
    } else
    {
      return true; //super.canStart(errors);
    }
  }

  public void addLogPageMessage(String msg)
  {
    LogPage logPage = this.jrunProcess.getLogPage();
    if (logPage != null)
    {
      logPage.log(msg + "\n"); // NOTRANS
    }
  }
}
