package com.paperboard.drawings;

import com.paperboard.server.User;

public abstract class Drawing implements IDrawing {
  private final User owner;
  private Position position;

  public Drawing(User user) {
    this.owner = user;
  }

  public void move() {}

  public void delete() {}
}
