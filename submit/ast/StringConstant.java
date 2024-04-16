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
public class StringConstant implements Expression, AbstractNode  {

  private final String value;

  public StringConstant(String value) {
    this.value = value;
  }

  public void toCminus(StringBuilder builder, final String prefix) {
    builder.append("\"").append(value).append("\"");
  }

  @Override
  public MIPSResult toMIPS(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
    String uniqueLabel = symbolTable.getUniqueLabel();
    data.append(uniqueLabel).append(":\t.asciiz ").append(this.value).append("\n");
    return MIPSResult.createAddressResult(uniqueLabel, VarType.CHAR);
  }
}
