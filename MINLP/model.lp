\ LP format - for model browsing. Use MPS format to capture full model detail.
Minimize
  - 5.5 x + 1.714285714285714 y.prod(0,0) + 3.428571428571428 y.prod(0,1)
   + 6.857142857142857 y.prod(0,2)
Subject To
 r1: - 1.714285714285714 y.bits(0) - 3.428571428571428 y.bits(1)
   - 6.857142857142857 y.bits(2) + y = -5.5
 r2: - x + 100000 y.bits(0) + y.prod(0,0) <= 100000
 r3: - x - 100000 y.bits(0) + y.prod(0,0) >= -100000
 r4: - 100000 y.bits(0) + y.prod(0,0) <= 0
 r5: 100000 y.bits(0) + y.prod(0,0) >= 0
 r6: - x + 100000 y.bits(1) + y.prod(0,1) <= 100000
 r7: - x - 100000 y.bits(1) + y.prod(0,1) >= -100000
 r8: - 100000 y.bits(1) + y.prod(0,1) <= 0
 r9: 100000 y.bits(1) + y.prod(0,1) >= 0
 r10: - x + 100000 y.bits(2) + y.prod(0,2) <= 100000
 r11: - x - 100000 y.bits(2) + y.prod(0,2) >= -100000
 r12: - 100000 y.bits(2) + y.prod(0,2) <= 0
 r13: 100000 y.bits(2) + y.prod(0,2) >= 0
Bounds
 -3 <= x <= 2
 y free
 y.prod(0,0) free
 y.prod(0,1) free
 y.prod(0,2) free
Binaries
 y.bits(0) y.bits(1) y.bits(2)
End
