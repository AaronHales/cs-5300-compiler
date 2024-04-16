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
    MIPSResult right = rhs.toMIPS(code, data, symbolTable, regAllocator);
    String resultReg = regAllocator.getAny();
    if (left.getRegister() != null && right.getRegister() != null) {
      if (this.type == BinaryOperatorType.MINUS) {
        regAllocator.clear(left.getRegister());
        regAllocator.clear(right.getRegister());
        resultReg = regAllocator.getAny();
        code.append("minus ").append(resultReg).append(" ").append(left.getRegister()).append(" ").append(right.getRegister()).append("\n");
      }
      else if (this.type == BinaryOperatorType.PLUS) {
        regAllocator.clear(left.getRegister());
        regAllocator.clear(right.getRegister());
        resultReg = regAllocator.getAny();
        code.append("add ").append(resultReg).append(" ").append(left.getRegister()).append(" ").append(right.getRegister()).append("\n");
      }
      else if (this.type == BinaryOperatorType.TIMES) {
        regAllocator.clear(left.getRegister());
        regAllocator.clear(right.getRegister());
        resultReg = regAllocator.getAny();
        code.append("mult ").append(left.getRegister()).append(" ").append(right.getRegister()).append("\n");
        code.append("mflo ").append(resultReg).append("\n");
      }
      else if (this.type == BinaryOperatorType.DIVIDE) {
        code.append("div ").append(left.getRegister()).append(" ").append(right.getRegister()).append("\n");
        regAllocator.clear(left.getRegister());
        regAllocator.clear(right.getRegister());
        resultReg = regAllocator.getAny();
        code.append("mflo ").append(resultReg).append("\n");
      }
    }
    return MIPSResult.createRegisterResult(resultReg, VarType.INT);
  }

}
