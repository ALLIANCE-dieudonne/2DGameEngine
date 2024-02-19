package com.alliance.DGameEngine.jade;

import com.alliance.DGameEngine.util.Time;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
  private final int width;
  private final int height;
  private final String title;
  private long glfwWindow;
  public float r, g, b, a;
  private boolean fadeToBlack = false;

  private static Window window = null;

  private static Scene currentScene;

  private Window() {
    this.width = 1920;
    this.height = 1080;
    this.title = "Alliance";
    r = 1;
    g = 1;
    b = 1;
    a = 1;
  }

  //changing scenes
  public static void changeScene(int newScene) {
    switch (newScene) {
      case 0 -> currentScene = new LevelEditorScene();
      //currentScene.init();
      case 1 -> currentScene = new LevelScene();
      default -> {
        assert false : "Invalid scene";
      }
    }
  }

  public static Window get() {
    if (Window.window == null) {
      Window.window = new Window();
    }
    return Window.window;
  }

  public void run() {
    System.out.println("Hello LWJGL " + Version.getVersion() + "!");

    init();
    loop();

    // Free the window callbacks and destroy the window
    glfwFreeCallbacks(glfwWindow);
    glfwDestroyWindow(glfwWindow);

    // Terminate GLFW and free the error callback
    glfwTerminate();
    Objects.requireNonNull(glfwSetErrorCallback(null)).free();
  }

  private void init() {
    // Setup an error callback. The default implementation
    // will print the error message in System.err.
    GLFWErrorCallback.createPrint(System.err).set();

    // Initialize GLFW. Most GLFW functions will not work before doing this.
    if (!glfwInit())
      throw new IllegalStateException("Unable to initialize GLFW");

    // Configure GLFW
    glfwDefaultWindowHints(); // optional, the current window hints are already the default
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

    // Create the window
    glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
    if (glfwWindow == NULL)
      throw new RuntimeException("Failed to create the GLFW window");

    //setup cursor callback
    glfwSetCursorPosCallback(glfwWindow, MouseListener::cursor_position_callback);
    glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouse_button_callback);
    glfwSetScrollCallback(glfwWindow, MouseListener::mouse_scroll_callback);

    // Setup a key callback. It will be called every time a key is pressed, repeated or released.
    glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

    // Get the thread stack and push a new frame
    try (MemoryStack stack = stackPush()) {
      IntBuffer pWidth = stack.mallocInt(1); // int*
      IntBuffer pHeight = stack.mallocInt(1); // int*

      // Get the window size passed to glfwCreateWindow
      glfwGetWindowSize(glfwWindow, pWidth, pHeight);

      // Get the resolution of the primary monitor
      GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

      // Center the window
      assert vidmode != null;
      glfwSetWindowPos(
        glfwWindow,
        (vidmode.width() - pWidth.get(0)) / 2,
        (vidmode.height() - pHeight.get(0)) / 2
      );
    } // the stack frame is popped automatically

    // Make the OpenGL context current
    glfwMakeContextCurrent(glfwWindow);
    // Enable v-sync
    glfwSwapInterval(1);

    // Make the window visible
    glfwShowWindow(glfwWindow);
    // This line is critical for LWJGL's interoperation with GLFW's
    // OpenGL context, or any context that is managed externally.
    // LWJGL detects the context that is current in the current thread,
    // creates the GLCapabilities instance and makes the OpenGL
    // bindings available for use.
    GL.createCapabilities();

    Window.changeScene(0);
  }

  private void loop() {
    float beginTime = Time.getTime();
    float endTime;
    float dt = -1.0f;


    // Run the rendering loop until the user has attempted to close
    // the window or has pressed the ESCAPE key.
    while (!glfwWindowShouldClose(glfwWindow)) {
      // Poll for window events. The key callback above will only be
      // invoked during this call.
      glfwPollEvents();
      glClearColor(r,g,b,a);
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

      if (dt >= 0)
        currentScene.update(dt);
      // Swap the color buffers
      glfwSwapBuffers(glfwWindow);

      // Poll for events and wait for a short duration
      glfwWaitEventsTimeout(0.01);

      endTime = Time.getTime();
      dt = endTime - beginTime;
      beginTime = endTime;
    }
  }


}
