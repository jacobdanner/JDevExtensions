package com.jacobd.j1.groovy.installer;

import oracle.ide.net.URLFactory;
import oracle.ide.net.URLFileSystem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Runnable that Downloads a File to the destination file
 * @author jdanner
 */
public class DownloadToFSRunnable implements Runnable
{
  private URL dlUrl;
  private File destFile;

  public DownloadToFSRunnable(URL dlUrl, File destFile)
  {
    this.dlUrl = dlUrl;

    if(destFile.isDirectory())
    {
      throw new IllegalArgumentException("destFile cannot be a directory");
    }
    this.destFile = destFile;
  }

  /**
   * When an object implementing interface <code>Runnable</code> is used
   * to create a thread, starting the thread causes the object's
   * <code>run</code> method to be called in that separately executing
   * thread.
   * <p/>
   * The general contract of the method <code>run</code> is that it may
   * take any action whatsoever.
   *
   * @see Thread#run()
   */
  @Override
  public void run()
  {
    if(destFile.exists())
    {
      // delete and overwrite
      URLFileSystem.delete(URLFactory.newFileURL(destFile));
    }
    destFile.getParentFile().mkdirs();
    downloadUrlToPath(dlUrl, destFile);
  }

  private void downloadUrlToPath(URL groovyDlUrl, File destPath)
  {
    ReadableByteChannel rbc = null;
    try
    {
      rbc = Channels.newChannel(groovyDlUrl.openStream());
      FileOutputStream fos = new FileOutputStream(destPath);
      fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

    } catch (IOException e)
    {
      e.printStackTrace();
    }

  }
}
