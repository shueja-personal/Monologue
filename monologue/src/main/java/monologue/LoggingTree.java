package monologue;

import java.lang.invoke.VarHandle;
import java.util.ArrayList;

public class LoggingTree {
  public abstract static class LoggingNode {
    private final String path;
    protected final ArrayList<LoggingNode> children = new ArrayList<>();

    public LoggingNode(String path) {
      this.path = path;
    }

    public String getPath() {
      return path;
    }

    public void addChild(LoggingNode child) {
      for (LoggingNode node : children) {
        if (node.path.equals(child.path)) {
          RuntimeLog.warn("Duplicate path: " + child.path);
          return;
        }
      }
      children.add(child);
      System.out.println(children.stream().map(it -> it.path).toList());
    }

    public abstract void log(Object obj);
  }

  public static class ObjectNode extends LoggingNode {
    protected final VarHandle handle;
    private final String err;

    public ObjectNode(String path, VarHandle handle) {
      super(path);
      this.handle = handle;
      this.err = path + " is null";
    }

    public void log(Object obj) {
      Object o = handle.get(obj);
      if (o == null) {
        RuntimeLog.warn(err);
        return;
      }
      if (o instanceof LogScope && !LogScope.getNodes(o).contains(this)) {
        LogScope.addNode(o, this);
      }
      for (LoggingNode child : children) {
        child.log(o);
      }
    }
  }

  public static class StaticObjectNode extends LoggingNode {
    private final Object object;

    public StaticObjectNode(String path, Object object) {
      super(path);
      this.object = object;
    }

    public void log(Object obj) {
      for (LoggingNode child : children) {
        child.log(object);
      }
    }
  }
}
