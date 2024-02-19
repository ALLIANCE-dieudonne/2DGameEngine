package com.alliance.DGameEngine.jade;

import static org.lwjgl.glfw.GLFW.*;

public class MouseListener {
  private static MouseListener instance;
  private double scrollX, scrollY;
  private double xpos, ypos, lastX, lastY;
  private boolean mouseButtonPress[] = new boolean[3];
  private boolean isDragging;

  public MouseListener() {
    this.scrollX = 0.0;
    this.scrollY = 0.0;
    this.xpos = 0.0;
    this.ypos = 0.0;
    this.lastX = 0.0;
    this.lastY = 0.0;
  }

  public static MouseListener get() {
    if (MouseListener.instance == null) {
      MouseListener.instance = new MouseListener();
    }
    return MouseListener.instance;
  }

  public static void cursor_position_callback(long window, double xpos, double ypos) {
    get().lastX = get().xpos;
    get().lastY = get().ypos;
    get().xpos = xpos;
    get().ypos = ypos;
    get().isDragging = get().mouseButtonPress[0] || get().mouseButtonPress[1] || get().mouseButtonPress[2];
  }

  public static void mouse_button_callback(long window, int button, int action, int mods) {
    if (button >= get().mouseButtonPress.length) {
      return; // Ignore if button index is out of range
    }

    if (action == GLFW_PRESS) {
      get().mouseButtonPress[button] = true;
    } else if (action == GLFW_RELEASE) {
      get().mouseButtonPress[button] = false;
      get().isDragging = false;
    }
  }

  public static void mouse_scroll_callback(long window, double xoffset, double yoffset) {
    get().scrollY = yoffset;
    get().scrollX = xoffset;
  }

  public static void endFrame() {
    get().scrollX = 0;
    get().scrollY = 0;
    get().lastX = get().xpos;
    get().lastY = get().ypos;
  }

  public static float getX() {
    return (float) get().xpos;
  }

  public static float getY() {
    return (float) get().ypos;
  }

  public static float getDx() {
    return (float) (get().lastX - get().xpos);
  }

  public static float getDy() {
    return (float) (get().lastY - get().ypos);
  }

  public static float getScrollX() {
    return (float) (get().scrollX);
  }

  public static float getScrollY() {
    return (float) (get().scrollY);
  }

  public static boolean isDragging() {
    return (get().isDragging);
  }

  public static boolean mouseButtonDown(int button) {
    if (button < get().mouseButtonPress.length) {
      return (get().mouseButtonPress[button]);
    } else {
      return false;
    }
  }

}
