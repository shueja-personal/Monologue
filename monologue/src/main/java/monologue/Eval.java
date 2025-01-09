package monologue;

import edu.wpi.first.epilogue.Logged;
import edu.wpi.first.epilogue.NotLogged;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import monologue.LoggingTree.LoggingNode;
import monologue.LoggingTree.ObjectNode;

class Eval {
  static List<Class<?>> getLoggedInHierarchy(Class<?> type, Class<?> stop) {
    ArrayList<Class<?>> result = new ArrayList<Class<?>>();

    Class<?> i = type;
    while (i != stop && LogScope.class.isAssignableFrom(i)) {
      result.add(i);
      i = i.getSuperclass();
    }

    return result;
  }

  static List<Class<?>> getLoggedClasses(Class<?> type) {
    return getLoggedInHierarchy(type, Object.class);
  }

  static VarHandle getHandle(Field field, MethodHandles.Lookup lookup) {
    try {
      var privateLookup = MethodHandles.privateLookupIn(field.getDeclaringClass(), lookup);
      return privateLookup.unreflectVarHandle(field);
    } catch (IllegalAccessException e) {
      RuntimeLog.warn(
          "Could not access field "
              + field.getName()
              + " of type "
              + field.getType().getSimpleName()
              + " in "
              + field.getDeclaringClass().getSimpleName()
              + ": "
              + e.getMessage());
      return null;
    }
  }

  static List<Field> getAllFields(List<Class<?>> classes) {
    ArrayList<Field> result = new ArrayList<>();
    for (Class<?> clazz : classes) {
      Collections.addAll(result, clazz.getDeclaredFields());
    }
    return result;
  }

  static <LN extends LoggingNode> LN exploreNodes(List<Class<?>> types, final LN rootNode) {
    final var fields = getAllFields(types);
    final MethodHandles.Lookup lookup = MethodHandles.lookup();
    final String rootPath = rootNode.getPath();

    for (final Field field : fields) {
      if (field.getAnnotation(NotLogged.class) == null
          && LogScope.class.isAssignableFrom(field.getType())) {
        final Logged logAnno = field.getAnnotation(Logged.class);
        final VarHandle handle = getHandle(field, lookup);
        if (handle == null) {
          continue;
        }
        String suffix =
            "/" + (logAnno == null || logAnno.name().isEmpty() ? field.getName() : logAnno.name());
        LoggingNode node = new ObjectNode(rootPath + suffix, handle);
        rootNode.addChild(exploreNodes(getLoggedClasses(field.getType()), node));
      }
    }

    return rootNode;
  }
}
