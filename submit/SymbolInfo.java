/*
 * Code formatter project
 * CS 4481
 */
package submit;

import submit.ast.VarType;

/**
 *
 * @author edwajohn
 */
public class SymbolInfo {

  private final String id;
  // In the case of a function, type is the return type
  private final VarType type;
  private final boolean function;
  private int offset;

  public SymbolInfo(String id, VarType type, boolean function) {
    this.id = id;
    this.type = type;
    this.function = function;
    if (this.type == VarType.BOOL) {
      this.offset = 2;
    }
    else if (this.type == VarType.INT) {
      this.offset = 4;
    }
    else if (this.type == VarType.CHAR) {
      this.offset = 0;
    }
    else {
      this.offset = 0;
    }
  }

  @Override
  public String toString() {
    return "<" + id + ", " + type + '>';
  }

  public int getOffset() {
    return offset;
  }

  public void updateOffset(int offset) {
    this.offset += offset;
  }

  public boolean isFunction() {
    return function;
  }

  public String getId() {
    return id;
  }

  public VarType getType() {
    if (type != null) {
      return type;
    }
    return null;
  }
}
