# All program code is placed after the
# .text assembler directive
.text

# Declare main as a global function
.globl	main

j main
main:
# Entering a new scope.
# Symbols in symbol table:
# update stack pointer.
addi $sp $sp -0
# println
la $a0 dataLabel0
li $v0 4
syscall
la $a0 newline
li $v0 4
syscall
# exiting scope.
addi $sp $sp 0
li $v0 10
syscall

# All memory structures are placed after the
# .data assembler directive
.data

newline: .asciiz "\n"
dataLabel0: .asciiz "Hello world"
