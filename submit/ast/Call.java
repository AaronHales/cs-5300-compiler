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
      for (Expression arg : args) {
        MIPSResult result = arg.toMIPS(code, data, symbolTable, regAllocator);
        if (result.getRegister() == null && result.getAddress() != null) {
          code.append("la $a0 ").append(result.getAddress()).append("\n");
          code.append("li $v0 4\n").append("syscall\n");
        }
        if (result.getRegister() != null && result.getAddress() == null) {
          regAllocator.clear(result.getRegister());
          code.append("move $a0 ").append(result.getRegister()).append("\n");
          code.append("li $v0 1\n").append("syscall\n");
        }
      }

      code.append("la $a0 newline\n").append("li $v0 4\n").append("syscall\n");
    }
    else {
      code.append("# Calling function ").append(id).append("\n");
      code.append("# Save $ra to a register\n");
      String raReg = regAllocator.getAny();
      code.append("move ").append(raReg).append(" $ra\n");
      code.append("# Save $t0-9 registers\n");
      int offset = 4 + symbolTable.getSize();
      regAllocator.saveT(code, offset-4);
      code.append("# Evaluate parameters and save to stack\n");
      for (Expression arg: args) {
        MIPSResult result = arg.toMIPS(code, data, symbolTable, regAllocator);
        if (result.getRegister() != null) {
          regAllocator.clear(result.getRegister());
        }
      }
      code.append("# Update the stack pointer\n");

      code.append("add $sp $sp ").append(-offset).append("\n");
      code.append("# Calling the function\n");
      code.append("jal ").append(id).append("\n");
      code.append("# Restore the stack pointer\n");
      code.append("add $sp $sp ").append(offset).append("\n");

      code.append("# Restore $t0-9 registers\n");
      regAllocator.restoreT(code, offset-4);
      code.append("# Restore $ra\n");
      code.append("move $ra ").append(raReg).append("\n");
      regAllocator.clear(raReg);
    }
    return MIPSResult.createVoidResult();
  }

}
