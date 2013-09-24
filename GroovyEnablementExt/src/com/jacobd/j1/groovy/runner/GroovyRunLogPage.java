package com.jacobd.j1.groovy.runner;

import oracle.ide.log.StyledMessage;
import oracle.ide.runner.RunLogPage;
import oracle.ide.runner.RunProcess;
import oracle.ide.util.Assert;
import oracle.javatools.util.ModelUtil;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jdanner
 */
public class GroovyRunLogPage
    extends RunLogPage
{
  private static WeakHashMap<GroovyRunLogPage, Object> allPages =
      new WeakHashMap<GroovyRunLogPage, Object>();

  public static GroovyRunLogPage makePage(RunProcess runProcess, boolean create)
  {
    synchronized (allPages)
    {
      GroovyRunLogPage page = null;

      for (Iterator<GroovyRunLogPage> i = allPages.keySet().iterator(); i.hasNext(); )
      {
        GroovyRunLogPage p = i.next();
        if (reuseLogPage(p, runProcess))
        {
          page = p;
          break;
        }
      }

      if (page != null)
      {
        // If we found a matching page, reset the page for this runProcess.
        page.setStuff(runProcess);
      } else
      {
        // If we didn't find a matching page, create a new page.
        if (create)
        {
          GroovyRunLogPage page1 = new GroovyRunLogPage(runProcess, runProcess.getShortLabel(), runProcess.getIcon());
          page = page1;
          allPages.put(page, null);
        }
      }
      return page;
    }
  }

  protected GroovyRunLogPage(RunProcess runProcess, String name, Icon icon)
  {
    super(runProcess, name, icon);
  }

  protected void appendToTextComponent(String msg, int maxLogLines)
  {
    super.appendToTextComponent(msg, maxLogLines);
    lookForMavenLogLevels(msg.length());
  }

  private void lookForMavenLogLevels(int appendedLength)
  {
    if (appendedLength > 0)
    {
      String documentText = getText();
      int documentLength = documentText.length();

      try
      {
        int index = documentLength - appendedLength;
        if (index < 0)
          index = 0;
        String line = documentText.substring(index, documentLength);

        TreeMap<Integer, SimpleAttributeSet> patternMatches = new TreeMap<Integer, SimpleAttributeSet>();
        // I don't think there is standard groovy, maybe we should look for
        // standard java logging levels
        // Search for the patterns [INFO] [ERROR][FATAL][WARNING][DEBUG]
        lookForInfoLevel(line, patternMatches);
        lookForErrorLevel(line, patternMatches);
        lookForFatalLevel(line, patternMatches);
        lookForWarningLevel(line, patternMatches);
        lookForDebugLevel(line, patternMatches);

        NavigableSet<Integer> keySet = patternMatches.descendingKeySet();
        Iterator<Integer> descendingIterator = keySet.descendingIterator();
        if (descendingIterator.hasNext())
        {
          Integer startPos = descendingIterator.next();
          SimpleAttributeSet as = patternMatches.get(startPos);

          while (descendingIterator.hasNext())
          {
            Integer endOffset = descendingIterator.next();
            StyledMessage sm = new StyledMessage(as, line.substring(startPos.intValue(), endOffset.intValue() - 1));
            sm.setStartOffset(documentLength - appendedLength + startPos.intValue());
            sm.setEndOffset(documentLength - appendedLength + endOffset.intValue() - 1);
            addStyle(sm);
            startPos = endOffset;
            as = patternMatches.get(startPos);
          }
          StyledMessage sm = new StyledMessage(as, line.substring(startPos.intValue()));
          sm.setStartOffset(documentLength - appendedLength + startPos.intValue());
          sm.setEndOffset(documentLength - appendedLength + line.length());
          addStyle(sm);
        }
        // Once sorted set end points on the styledMessages.
        // The endpoint of the  SM will be the start point of the next SM

      } catch (Exception e)
      {
        Assert.printStackTrace(e);
      }
    }
  }

  private void lookForInfoLevel(String line, Map<Integer, SimpleAttributeSet> matches)
  {
    Pattern errorNumber = Pattern.compile("\\[INFO\\]");
    Matcher matcher = errorNumber.matcher(line);
    int index = 0;
    while (matcher.find(index))
    {
      int startpos = matcher.start();
      SimpleAttributeSet as = new SimpleAttributeSet();
      StyleConstants.setForeground(as, Color.BLACK);
      matches.put(Integer.valueOf(startpos), as);
      index = matcher.end();
    }
  }

  private void lookForErrorLevel(String line, Map<Integer, SimpleAttributeSet> matches)
  {
    Pattern errorNumber = Pattern.compile("\\[ERROR\\]");
    Matcher matcher = errorNumber.matcher(line);
    int index = 0;
    while (matcher.find(index))
    {
      int startpos = matcher.start();
      SimpleAttributeSet as = new SimpleAttributeSet();
      StyleConstants.setForeground(as, Color.RED);
      matches.put(Integer.valueOf(startpos), as);
      index = matcher.end();
    }
  }

  private void lookForFatalLevel(String line, Map<Integer, SimpleAttributeSet> matches)
  {
    Pattern errorNumber = Pattern.compile("\\[FATAL\\]");
    Matcher matcher = errorNumber.matcher(line);
    int index = 0;
    while (matcher.find(index))
    {
      int startpos = matcher.start();
      SimpleAttributeSet as = new SimpleAttributeSet();
      StyleConstants.setForeground(as, Color.RED);
      matches.put(Integer.valueOf(startpos), as);
      index = matcher.end();
    }
  }

  private void lookForWarningLevel(String line, Map<Integer, SimpleAttributeSet> matches)
  {
    Pattern errorNumber = Pattern.compile("\\[WARNING\\]");
    Matcher matcher = errorNumber.matcher(line);
    int index = 0;
    while (matcher.find(index))
    {
      int startpos = matcher.start();
      SimpleAttributeSet as = new SimpleAttributeSet();
      StyleConstants.setForeground(as, Color.ORANGE);
      matches.put(Integer.valueOf(startpos), as);
      index = matcher.end();
    }
  }

  private void lookForDebugLevel(String line, Map<Integer, SimpleAttributeSet> matches)
  {
    Pattern errorNumber = Pattern.compile("\\[DEBUG\\]");
    Matcher matcher = errorNumber.matcher(line);
    int index = 0;
    while (matcher.find(index))
    {
      int startpos = matcher.start();
      SimpleAttributeSet as = new SimpleAttributeSet();
      StyleConstants.setForeground(as, Color.GRAY);
      matches.put(Integer.valueOf(startpos), as);
      index = matcher.end();
    }
  }


  private static boolean reuseLogPage(GroovyRunLogPage groovyRunLogPage, RunProcess runProcess)
  {
    if (!groovyRunLogPage.isProcessAlive())
    {
      if (ModelUtil.areEqual(runProcess.getShortLabel(), groovyRunLogPage.getName()))
      {
        if (runProcess.getWorkspace() == groovyRunLogPage.getWorkspace())
        {
          if (runProcess.getProject() == groovyRunLogPage.getProject())
          {
            if (runProcess.getIcon() == groovyRunLogPage.getTabIcon())
            {
              if (ModelUtil.areEqual(runProcess.getToolTipText(), groovyRunLogPage.getToolTipText()))
              {
                if (ModelUtil.areEqual(runProcess.getShortLabelWithPrefix(), groovyRunLogPage.getTitleName()))
                {
                  if (runProcess.getHelpInfo() == null ||
                      ModelUtil.areEqual(runProcess.getHelpInfo(), groovyRunLogPage.getHelpInfo()))
                  {
                    return true;
                  }
                }
              }
            }
          }
        }
      }
    }

    return false;
  }
}

