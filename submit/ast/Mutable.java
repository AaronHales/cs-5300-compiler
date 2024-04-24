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
    String register = regAllocator.getAny();
    code.append("# Get ").append(id).append("'s offset from $sp from the symbol table and initialize ").append(id).append("'s address with it. We'll add $sp later.\n");
    code.append("li ").append(register).append(" ");
    if (index != null) {
      SymbolInfo info = symbolTable.find(id);
      if (info.getArraySize() > 0) {
        code.append(info.getTotalOffset() * info.getArraySize());
      }
      else {
        code.append(info.getTotalOffset());
      }
    }
    code.append("\n");
    code.append("# Add the stack pointer address to the offset.\n");
    code.append("add ").append(register).append(" ").append(register).append(" ").append("$sp\n");
    if (index != null) {
      code.append("# Evaluate the index expression and store in a register.\n");
      MIPSResult indexMIPSResult = index.toMIPS(code, data, symbolTable, regAllocator);
      code.append("# Multiply the index by -4.\n");
      String multIndexReg = regAllocator.getAny();
      code.append(String.format("li %s %d\n", multIndexReg, 4));
      if (indexMIPSResult != null && indexMIPSResult.getRegister() != null) {
        regAllocator.clear(indexMIPSResult.getRegister());
      }
      regAllocator.clear(multIndexReg);
      String resultReg = regAllocator.getAny();
      code.append(String.format("mul %s %s %s\n", resultReg, indexMIPSResult.getRegister(), multIndexReg));
      code.append("# Add the index offset to the offset.\n");
      regAllocator.clear(resultReg);
      code.append(String.format("add %s %s %s\n", register, register, resultReg));
    }

//    regAllocator.clear(register);
//    code.append("# Load the value of ").append(id).append("\n");
//    code.append("lw ").append(returnReg).append(" 0(").append(register).append(")\n");

    return MIPSResult.createRegisterResult(register, VarType.INT);
  }

}
