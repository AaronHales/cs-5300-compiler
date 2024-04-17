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
public class UnaryOperator implements Expression, AbstractNode  {

  private final UnaryOperatorType type;
  private final Expression expression;

  public UnaryOperator(String type, Expression expression) {
    this.type = UnaryOperatorType.fromString(type);
    this.expression = expression;
  }

  @Override
  public void toCminus(StringBuilder builder, String prefix) {
    builder.append(type);
    expression.toCminus(builder, prefix);
  }

  @Override
  public MIPSResult toMIPS(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
    MIPSResult expression = this.expression.toMIPS(code, data, symbolTable, regAllocator);
    if (expression.getRegister() != null) {
      if (UnaryOperatorType.NEG == type) {
        code.append("sub ").append(expression.getRegister()).append(" $zero ").append(expression.getRegister()).append("\n");
      }
    }
    regAllocator.clear(expression.getRegister());
    return MIPSResult.createRegisterResult(expression.getRegister(), expression.getType());
  }

}
