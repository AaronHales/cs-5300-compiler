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
public class BinaryOperator implements Expression, AbstractNode {

  private final Expression lhs, rhs;
  private final BinaryOperatorType type;

  public BinaryOperator(Expression lhs, BinaryOperatorType type, Expression rhs) {
    this.lhs = lhs;
    this.type = type;
    this.rhs = rhs;
  }

  public BinaryOperator(Expression lhs, String type, Expression rhs) {
    this.lhs = lhs;
    this.type = BinaryOperatorType.fromString(type);
    this.rhs = rhs;
  }

  @Override
  public void toCminus(StringBuilder builder, String prefix) {
    lhs.toCminus(builder, prefix);
    builder.append(" ").append(type).append(" ");
    rhs.toCminus(builder, prefix);
  }

  @Override
  public MIPSResult toMIPS(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
    MIPSResult left = lhs.toMIPS(code, data, symbolTable, regAllocator);
    String leftRegister = left.getRegister();
    if (left.getRegister() != null) {
      leftRegister = regAllocator.getAny();
//      # Load the value of a.
      code.append("lw ").append(leftRegister).append(" 0(").append(left.getRegister()).append(")\n");
      regAllocator.clear(left.getRegister());
    }
    MIPSResult right = rhs.toMIPS(code, data, symbolTable, regAllocator);
    String rightRegister = right.getRegister();
    if (right.getRegister() != null) {
      rightRegister = regAllocator.getAny();
      code.append("lw ").append(rightRegister).append(" 0(").append(right.getRegister()).append(")\n");
      regAllocator.clear(right.getRegister());
      regAllocator.clear(leftRegister);
    }
    String resultReg = regAllocator.getAny();
    if (left.getRegister() != null && right.getRegister() != null) {

      if (this.type == BinaryOperatorType.MINUS) {
        regAllocator.clear(leftRegister);
        regAllocator.clear(rightRegister);
        resultReg = regAllocator.getAny();
        code.append("sub ").append(resultReg).append(" ").append(leftRegister).append(" ").append(rightRegister).append("\n");
      }
      else if (this.type == BinaryOperatorType.PLUS) {
        regAllocator.clear(leftRegister);
        regAllocator.clear(rightRegister);
        resultReg = regAllocator.getAny();
        code.append("add ").append(resultReg).append(" ").append(leftRegister).append(" ").append(rightRegister).append("\n");
      }
      else if (this.type == BinaryOperatorType.TIMES) {
        regAllocator.clear(leftRegister);
        regAllocator.clear(rightRegister);
        resultReg = regAllocator.getAny();
        code.append("mult ").append(leftRegister).append(" ").append(rightRegister).append("\n");
        code.append("mflo ").append(resultReg).append("\n");
      }
      else if (this.type == BinaryOperatorType.DIVIDE) {
        code.append("div ").append(leftRegister).append(" ").append(rightRegister).append("\n");
        regAllocator.clear(leftRegister);
        regAllocator.clear(rightRegister);
        resultReg = regAllocator.getAny();
        code.append("mflo ").append(resultReg).append("\n");
      }
    }
    return MIPSResult.createRegisterResult(resultReg, VarType.INT);
  }

}
