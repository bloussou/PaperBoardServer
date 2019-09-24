package com.paperboard.server;

import com.paperboard.drawings.Drawing;

import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;

public class PaperBoard {
  private String title;
  private String backgroundColor;
  private java.util.Set<User> drawers = new ConcurrentSkipListSet<User>();
  private java.util.concurrent.CopyOnWriteArrayList<com.paperboard.drawings.Drawing> drawings =
      new CopyOnWriteArrayList<Drawing>();
  private byte[] backgroundImage;
  private String id;

  public PaperBoard(String title) {}

  public PaperBoard(String title, String backgroundColor) {}

  public PaperBoard(String title, byte[] image) {}
}
