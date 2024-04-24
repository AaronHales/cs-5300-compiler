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
  private int baseOffset;
  private int arraySize;

  public SymbolInfo(String id, VarType type, boolean function) {
    this.id = id;
    this.type = type;
    this.function = function;
    this.offset = 0;
    this.arraySize = 0;
    if (function) {
      this.baseOffset = 0;
    }
    else {
      if (this.type == VarType.BOOL) {
        this.baseOffset = -2;
      } else if (this.type == VarType.INT) {
        this.baseOffset = -4;
      } else if (this.type == VarType.CHAR) {
        this.baseOffset = 0;
      } else {
        this.baseOffset = 0;
      }
    }
  }

  @Override
  public String toString() {
    return "<" + id + ", " + type + '>';
  }

  public void setArraySize(int size) {
    this.arraySize = size;
  }

  public int getArraySize() {
    return arraySize;
  }

  public int getTotalOffset() {
    int temp = baseOffset+offset;
    offset = 0;
    return temp;
  }

  public int getBaseOffset() {
    return baseOffset;
  }

  public void updateOffset(int offset) {
    this.offset = offset;
  }

  public void updateBaseOffset(int offset) {
    this.baseOffset += offset;
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
