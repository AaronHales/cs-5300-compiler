/*
 * Code formatter project
 * CS 4481
 */
package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolInfo;
import submit.SymbolTable;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author edwajohn
 */
public class Call implements Expression, AbstractNode  {

  private final String id;
  private final List<Expression> args;

  public Call(String id, List<Expression> args) {
    this.id = id;
    this.args = new ArrayList<>(args);
  }

  @Override
  public void toCminus(StringBuilder builder, String prefix) {
    builder.append(id).append("(");
    for (Expression arg : args) {
      arg.toCminus(builder, prefix);
      builder.append(", ");
    }
    if (!args.isEmpty()) {
      builder.setLength(builder.length() - 2);
    }
    builder.append(")");
  }

  @Override
  public MIPSResult toMIPS(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
    if (this.id.equals("println")) {
      code.append("# println\n");
      symbolTable.addSymbol(this.id, new SymbolInfo(this.id, null, true));

      if (symbolTable.find("newline") == null) {
        data.append("newline:\t.asciiz \"\\n\"\n");
        symbolTable.addSymbol("newline", new SymbolInfo("newline", VarType.CHAR, false));
      }
      for (Expression arg : args) {
        MIPSResult result = arg.toMIPS(code, data, symbolTable, regAllocator);
        if (result.getRegister() == null && result.getAddress() != null) {
          code.append("la $a0 ").append(result.getAddress()).append("\n");
          code.append("li $v0 4\n").append("syscall\n");
        }
        if (result.getRegister() != null && result.getAddress() == null) {
          code.append("move $a0 ").append(result.getRegister()).append("\n");
          regAllocator.clear(result.getRegister());
          code.append("li $v0 1\n").append("syscall\n");
        }
      }

      code.append("la $a0 newline\n").append("li $v0 4\n").append("syscall\n");
    }
    else {
      for (Expression arg: args) {
        arg.toMIPS(code, data, symbolTable, regAllocator);
      }
    }
    return MIPSResult.createVoidResult();
  }

}
