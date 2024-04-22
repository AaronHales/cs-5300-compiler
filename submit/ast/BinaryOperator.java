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
    String resultReg = regAllocator.getAny();
    MIPSResult left = lhs.toMIPS(code, data, symbolTable, regAllocator);
    regAllocator.clear(resultReg);
    String leftRegister = left.getRegister();
    if (left.getRegister() != null) {
      if (lhs instanceof Mutable) {
        leftRegister = regAllocator.getAny();
//      # Load the value of a.
        regAllocator.clear(left.getRegister());
        code.append("lw ").append(leftRegister).append(" 0(").append(left.getRegister()).append(")\n");
//        regAllocator.clear(leftRegister);
      }
    }
    resultReg = regAllocator.getAny();
    MIPSResult right = rhs.toMIPS(code, data, symbolTable, regAllocator);
    String rightRegister = right.getRegister();
    if (right.getRegister() != null) {
      regAllocator.clear(resultReg);
      if (rhs instanceof Mutable) {
        rightRegister = regAllocator.getAny();
        code.append("lw ").append(rightRegister).append(" 0(").append(right.getRegister()).append(")\n");
        regAllocator.clear(right.getRegister());
      }
//      regAllocator.clear(leftRegister);
    }
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
      else if (this.type == BinaryOperatorType.EQ) {
        regAllocator.clear(leftRegister);
        regAllocator.clear(rightRegister);
        resultReg = regAllocator.getAny();
        code.append(String.format("sub %s %s %s\n", resultReg, leftRegister, rightRegister));
      }
      else if (this.type == BinaryOperatorType.NE) {
        regAllocator.clear(leftRegister);
        regAllocator.clear(rightRegister);
      }
      else if (this.type == BinaryOperatorType.GE) {
        regAllocator.clear(leftRegister);
        regAllocator.clear(rightRegister);
        resultReg = regAllocator.getAny();
        code.append(String.format("slt %s %s %s\n", resultReg, leftRegister, rightRegister));
      }
      else if (this.type == BinaryOperatorType.GT) {
        regAllocator.clear(leftRegister);
        regAllocator.clear(rightRegister);
        resultReg = regAllocator.getAny();
        code.append(String.format("slt %s %s %s\n", resultReg, rightRegister, leftRegister));
        code.append(String.format("subi %s %s 1\n", resultReg, resultReg));
      }
      else if (this.type == BinaryOperatorType.LE) {
        regAllocator.clear(leftRegister);
        regAllocator.clear(rightRegister);
        resultReg = regAllocator.getAny();
        code.append(String.format("slt %s %s %s\n", resultReg, rightRegister, leftRegister));

      }
      else if (this.type == BinaryOperatorType.LT) {
        regAllocator.clear(leftRegister);
        regAllocator.clear(rightRegister);
        resultReg = regAllocator.getAny();
        code.append(String.format("slt %s %s %s\n", resultReg, leftRegister, rightRegister));
        code.append(String.format("subi %s %s 1\n", resultReg, resultReg));
      }

    }
//    regAllocator.clear(resultReg);
    return MIPSResult.createRegisterResult(resultReg, VarType.INT);
  }

}
