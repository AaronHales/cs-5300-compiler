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
public class ParenExpression implements Expression, AbstractNode  {

  private final Expression expression;

  public ParenExpression(Expression expression) {
    this.expression = expression;
  }

  @Override
  public void toCminus(StringBuilder builder, String prefix) {
    builder.append("(");
    expression.toCminus(builder, prefix);
    builder.append(")");
  }

  public MIPSResult toMIPS(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
    return expression.toMIPS(code, data, symbolTable, regAllocator);
  }

}
