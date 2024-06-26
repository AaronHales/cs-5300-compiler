/*
 * Code formatter project
 * CS 4481
 */
package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolInfo;
import submit.SymbolTable;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author edwajohn
 */
public class Program implements Node, AbstractNode  {

  private ArrayList<Declaration> declarations;

  public Program(List<Declaration> declarations) {
    this.declarations = new ArrayList<>(declarations);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    toCminus(builder, "");
    return builder.toString();
  }

  @Override
  public void toCminus(StringBuilder builder, String prefix) {
    for (Declaration declaration : declarations) {
      declaration.toCminus(builder, "");
    }
  }

  @Override
  public MIPSResult toMIPS(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
    if (symbolTable.find("newline") == null) {
      data.append("newline:\t.asciiz \"\\n\"\n");
      symbolTable.addSymbol("newline", new SymbolInfo("newline", VarType.CHAR, false));
    }
    for (Declaration declaration : declarations) {
      declaration.toMIPS(code, data, symbolTable, regAllocator);
    }
    code.append("li $v0 10\n");
    code.append("syscall\n");
    return MIPSResult.createVoidResult();
  }

}
