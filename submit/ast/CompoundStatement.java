/*
 * Code formatter project
 * CS 4481
 */
package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolTable;

import java.util.List;

/**
 *
 * @author edwajohn
 */
public class CompoundStatement implements Statement, AbstractNode  {

  private final List<Statement> statements;
  private SymbolTable symbolTable;

  public CompoundStatement(List<Statement> statements) {
    this.statements = statements;
    this.symbolTable = new SymbolTable();
  }

  @Override
  public void toCminus(StringBuilder builder, String prefix) {
    builder.append(prefix).append("{\n");
    for (Statement s : statements) {
      s.toCminus(builder, prefix + "  ");
    }
    builder.append(prefix).append("}\n");
  }

  @Override
  public MIPSResult toMIPS(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
    this.symbolTable = symbolTable.createChild();
    regAllocator.clearAll();
    int parentsize = symbolTable.getSize();
    code.append("# Entering a new scope.\n");
    code.append("# Symbols in symbol table:\n");
    StringBuilder compoundStmt = new StringBuilder();
    compoundStmt.append("# Update the stack pointer.\n");
    compoundStmt.append("addi $sp $sp ");
    if (parentsize != 0) {
      compoundStmt.append(-this.symbolTable.getParent().getSize());
    }
    else {
      compoundStmt.append(parentsize);
    }
    compoundStmt.append("\n");
    for (Statement statement : statements) {
      MIPSResult result =  statement.toMIPS(compoundStmt, data, this.symbolTable, regAllocator);
      if (result.getRegister() != null) {
        regAllocator.clear(result.getRegister());
      }
    }
    for (String var : this.symbolTable.inScope()) {
      code.append("#  ").append(var).append("\n");
    }
    code.append(compoundStmt);
    code.append("# Exiting scope.\n");
    code.append("addi $sp $sp ");
    if (parentsize != 0) {
      code.append(this.symbolTable.getParent().getSize());
    }
    else {
      code.append(-parentsize);
    }
    code.append("\n");
    return MIPSResult.createVoidResult();
  }

}
