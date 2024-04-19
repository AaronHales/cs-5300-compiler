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
public class Return implements Statement, AbstractNode  {

  private final Expression expr;

  public Return(Expression expr) {
    this.expr = expr;
  }

  @Override
  public void toCminus(StringBuilder builder, String prefix) {
    builder.append(prefix);
    if (expr == null) {
      builder.append("return;\n");
    } else {
      builder.append("return ");
      expr.toCminus(builder, prefix);
      builder.append(";\n");
    }
  }

  @Override
  public MIPSResult toMIPS(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
    if (expr != null) {
      String resultReg = regAllocator.getAny();
      if (!(expr instanceof Mutable)) {
        regAllocator.clear(resultReg);
      }
      MIPSResult exprResult = expr.toMIPS(code, data, symbolTable, regAllocator);
      regAllocator.clear(exprResult.getRegister());
      regAllocator.clear(resultReg);
      if (expr instanceof Mutable) {
        code.append("lw ").append(resultReg).append(" 0(").append(exprResult.getRegister()).append(")\n");
      }
      code.append("sw ").append(resultReg).append(" ").append(-symbolTable.getParent().getParent().getSize()-4).append("($sp)\n");
      code.append("jr $ra\n");
      return MIPSResult.createRegisterResult(resultReg, exprResult.getType());
    }
    return MIPSResult.createVoidResult();
  }

}
