# Initial state:
addi x1, x0, 5      # x1 (n) = 5
addi x2, x0, 1      # x2 (result) = 1
addi x3, x0, 1      # x3 (decrement constant) = 1

loop:
beq  x1, x0, end    # If n == 0, jump to end
mul  x2, x2, x1     # result = result * n (Uses M-Extension)
sub  x1, x1, x3     # n = n - 1
jal  x0, loop       # Jump back to start of loop

end:
beq x0, x0, end     # Infinite loop to stop