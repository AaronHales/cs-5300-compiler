/*
 * Code formatter project
 * CS 4481
 */
package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolInfo;
import submit.SymbolTable;

/**
 *
 * @author edwajohn
 */
public class Mutable implements Expression, Node, AbstractNode  {

  private final String id;
  private final Expression index;

  public Mutable(String id, Expression index) {
    this.id = id;
    this.index = index;
  }

  @Override
  public void toCminus(StringBuilder builder, String prefix) {
    builder.append(id);
    if (index != null) {
      builder.append("[");
      index.toCminus(builder, prefix);
      builder.append("]");
    }
  }

  @Override
  public MIPSResult toMIPS(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
    SymbolInfo info = symbolTable.find(id);
    String register = regAllocator.getAny();
    code.append("# Get ").append(id).append("'s offset from $sp from the symbol table and initialize ").append(id).append("'s address with it. We'll add $sp later.\n");
    code.append("li ").append(register).append(" ").append(symbolTable.find(id).getTotalOffset()).append("\n");
    code.append("# Add the stack pointer address to the offset.\n");
    code.append("add ").append(register).append(" ").append(register).append(" ").append("$sp\n");
//    regAllocator.clear(register);
//    code.append("# Load the value of ").append(id).append("\n");
//    code.append("lw ").append(returnReg).append(" 0(").append(register).append(")\n");

    return MIPSResult.createRegisterResult(register, VarType.INT);
  }

}
