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
    size -= symbol.getBaseOffset();
    symbol.updateBaseOffset(-size-symbol.getBaseOffset());
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
      SymbolInfo parentReturn = parent.find(id);
      if (parentReturn != null) {
        parentReturn.updateOffset(parent.size);
      }
      return parentReturn;
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
    return child;
  }

  public SymbolTable getParent() {
    return parent;
  }

  public String getUniqueLabel() {
    SymbolTable current = this;
    while (current.parent != null) {
      current = current.parent;
    }
    int currentUniqueLabel = current.increaseUniqueLabel();
    return "datalabel" + currentUniqueLabel;
  }

  public int increaseUniqueLabel() {
    return uniqueLable++;
  }

  public int getSize() {
    return size;
  }

  public ArrayList<String> inScope() {
    ArrayList<String> varsInScope = new ArrayList<>();
    for (String key : table.keySet()) {
      if (key.equals("newline")) {
        continue;
      }
      else if (table.get(key).getId().equals("println") || !table.get(key).isFunction()) {
        varsInScope.add(key);
      }
      else if (table.get(key).isFunction()) {
        varsInScope.add("return");
      }
    }
    return varsInScope;
  }
}
