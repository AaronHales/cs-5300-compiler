/*
 * Code formatter project
 * CS 4481
 */
package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolTable;

/**
 *
 * @author edwajohn
 */
public class Assignment implements Expression, Node, AbstractNode {

  private final Mutable mutable;
  private final AssignmentType type;
  private final Expression rhs;

  public Assignment(Mutable mutable, String assign, Expression rhs) {
    this.mutable = mutable;
    this.type = AssignmentType.fromString(assign);
    this.rhs = rhs;
  }

  public void toCminus(StringBuilder builder, final String prefix) {
    mutable.toCminus(builder, prefix);
    if (rhs != null) {
      builder.append(" ").append(type.toString()).append(" ");
      rhs.toCminus(builder, prefix);
    } else {
      builder.append(type.toString());

    }
  }

  public MIPSResult toMIPS(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
    MIPSResult leftSide = mutable.toMIPS(code, data, symbolTable, regAllocator);
    code.append("# Compute rhs for assignment ").append(type.toString()).append("\n");
    MIPSResult rightSide = rhs.toMIPS(code, data, symbolTable, regAllocator);
    if (leftSide.getRegister() != null) {
      regAllocator.clear(leftSide.getRegister());
    }
    if (rightSide.getRegister() != null) {
      regAllocator.clear(rightSide.getRegister());
    }
    if (rightSide.getRegister() != null && rightSide.getAddress() == null) {
      if (leftSide.getAddress() == null && leftSide.getRegister() != null) {
        code.append("# complete assignment statement with store\n");
        code.append("sw ").append(rightSide.getRegister()).append(" 0(").append(leftSide.getRegister()).append(")\n");
//        regAllocator.clear(leftSide.getRegister());
        regAllocator.clear(rightSide.getRegister());
      }

    }
    return MIPSResult.createRegisterResult(leftSide.getRegister(), leftSide.getType());
  }

}
