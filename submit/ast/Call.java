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
//    symbolTable.addSymbol(this.id, new SymbolInfo(this.id, null, true));
    if (this.id.equals("println")) {
      code.append("# println\n");
      for (Expression arg : args) {
        MIPSResult result = arg.toMIPS(code, data, symbolTable, regAllocator);
        if (result.getRegister() == null && result.getAddress() != null) {
          code.append("la $a0 ").append(result.getAddress()).append("\n");
          code.append("li $v0 4\n").append("syscall\n");
        }
        if (result.getRegister() != null && result.getAddress() == null) {
          String resultReg = regAllocator.getAny();
          regAllocator.clear(result.getRegister());
          if (arg instanceof Mutable) {
            code.append(String.format("lw %s 0(%s)\n", resultReg, result.getRegister()));
            code.append("move $a0 ").append(resultReg).append("\n");
            regAllocator.clear(resultReg);
          }
          else {
            code.append("move $a0 ").append(result.getRegister()).append("\n");
          }
          code.append("li $v0 1\n").append("syscall\n");
        }
      }
      code.append("la $a0 newline\n").append("li $v0 4\n").append("syscall\n");
      return MIPSResult.createVoidResult();
    }
    else {
      code.append("# Calling function ").append(id).append("\n");
      code.append("# Save $ra to a register\n");
      String raReg = regAllocator.getAny();
      code.append("move ").append(raReg).append(" $ra\n");
      code.append("# Save $t0-9 registers\n");
      int offset = 4 + symbolTable.getSize();
      int numParams = 4;
//      for (Expression arg: args) {
//        numParams += 4;
//      }
      code.append(String.format("# offset: %d, numParams: %d, paramCount: %d\n", offset, numParams, 0));
      regAllocator.saveT(code, offset-4);
      code.append("# Evaluate parameters and save to stack\n");
      int paramCount = -4;
      for (String regInUse : regAllocator.getUsed()) {
        paramCount += 4;
      }
      for (Expression arg: args) {
        MIPSResult result = arg.toMIPS(code, data, symbolTable, regAllocator);
        String resultReg = regAllocator.getAny();
        if (result.getRegister() != null) {
          regAllocator.clear(result.getRegister());
          if (arg instanceof Mutable) {
            code.append("lw ").append(resultReg).append(" 0(").append(result.getRegister()).append(")\n");
          }
          else {
            regAllocator.clear(resultReg);
            resultReg = result.getRegister();
          }
          code.append(String.format("# offset: %d, numParams: %d, paramCount: %d\n", offset, numParams, paramCount));
          code.append("sw ").append(resultReg).append(" ");
          code.append(-(offset + numParams)).append("($sp)\n");

          numParams += 4;
//          paramCount += 4;
        }
        regAllocator.clear(resultReg);
        code.append(String.format("# symbol table size: %d, offset: %d, paramCount: %d, numParams: %d\n", symbolTable.getSize(), offset, paramCount, numParams));
      }
      code.append("# Update the stack pointer\n");

      code.append("add $sp $sp ").append(-offset).append("\n");
      code.append("# Call the function\n");
      code.append("jal ").append(id).append("\n");
      code.append("# Restore the stack pointer\n");
      code.append("add $sp $sp ").append(offset).append("\n");

      code.append("# Restore $t0-9 registers\n");
      regAllocator.restoreT(code, offset-4);
      code.append("# Restore $ra\n");
      code.append("move $ra ").append(raReg).append("\n");
      regAllocator.clear(raReg);
      String returnRa = regAllocator.getAny();
//      code.append("return symbol table: ").append(symbolTable.find(id)).append("\n");
//      data.append(id).append(" return val type: ").append(symbolTable.find(id).getType()).append("\n");
      if (symbolTable.find(id).getType() != null) {
        code.append("# Get return value off stack\n");
        code.append("lw ").append(returnRa).append(" ").append(-(offset + numParams)).append("($sp)\n");
      }
      regAllocator.clear(returnRa);
      regAllocator.clear(raReg);
      return MIPSResult.createRegisterResult(returnRa, symbolTable.find(id).getType());
    }
  }

}
