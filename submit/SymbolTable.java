package submit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
 * Code formatter project
 * CS 4481
 */
/**
 *
 */
public class SymbolTable {

  private final HashMap<String, SymbolInfo> table;
  private SymbolTable parent;
  private final List<SymbolTable> children;
  private int size;

  private int uniqueLable = 0;

  public SymbolTable() {
    table = new HashMap<>();
    parent = null;
    children = new ArrayList<>();
    this.size = 0;
  }

  public void addSymbol(String id, SymbolInfo symbol) {
    table.put(id, symbol);
    size += symbol.getOffset();
    symbol.updateOffset(size-symbol.getOffset());
  }

  /**
   * Returns null if no symbol with that id is in this symbol table or an
   * ancestor table.
   *
   * @param id
   * @return
   */
  public SymbolInfo find(String id) {
    if (table.containsKey(id)) {
      return table.get(id);
    }
    if (parent != null) {
      return parent.find(id);
    }
    return null;
  }

  /**
   * Returns the new child.
   *
   * @return
   */
  public SymbolTable createChild() {
    SymbolTable child = new SymbolTable();
    children.add(child);
    child.parent = this;
    child.resetOffset();
    return child;
  }

  public SymbolTable getParent() {
    return parent;
  }

  public String getUniqueLabel() {
    int currentUniqueLabel = uniqueLable++;
    return "datalabel" + currentUniqueLabel;
  }

  public void resetOffset() {
    this.size = 0;
  }

  public int getSize() {
    return size;
  }

  public SymbolInfo findInThisOnly(String id) {
    if (this.table.containsKey(id)) {
      return this.table.get(id);
    }
    else {
      return null;
    }
  }
}
