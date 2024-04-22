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
public class While implements Statement, AbstractNode  {

  private final Expression expression;
  private final Statement statement;

  public While(Expression expression, Statement statement) {
    this.expression = expression;
    this.statement = statement;
  }

  @Override
  public void toCminus(StringBuilder builder, String prefix) {
    builder.append(prefix).append("while (");
    expression.toCminus(builder, prefix);
    builder.append(")\n");
    if (statement instanceof CompoundStatement) {
      statement.toCminus(builder, prefix);
    } else {
      statement.toCminus(builder, prefix + " ");
    }

  }

  @Override
  public MIPSResult toMIPS(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
    String entry = symbolTable.getUniqueLabel();
    String exit = symbolTable.getUniqueLabel();
    if (expression != null) {
      code.append(String.format("%s:\n", entry));
      MIPSResult exprResult = expression.toMIPS(code, data, symbolTable, regAllocator);
      if (exprResult.getRegister() != null) {
        code.append(String.format("bne %s $zero %s\n", exprResult.getRegister(), exit));
//        regAllocator.clear(exprResult.getRegister());

      }
    }
    if (statement != null) {
      MIPSResult stmtResult = statement.toMIPS(code, data, symbolTable, regAllocator);
      if (stmtResult.getRegister() != null) {
        regAllocator.clear(stmtResult.getRegister());
      }
      code.append(String.format("j %s\n", entry));
      code.append(String.format("%s:\n", exit));
    }
    return MIPSResult.createVoidResult();
  }
}
