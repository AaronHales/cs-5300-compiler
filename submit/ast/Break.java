/*
 * Code formatter project
 * CS 4481
 */
package submit.ast;

/**
 *
 * @author edwajohn
 */
public class Break implements Statement, AbstractNode  {

  @Override
  public void toCminus(StringBuilder builder, String prefix) {
    builder.append(prefix).append("break;\n");
  }

}
