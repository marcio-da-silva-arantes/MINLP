# MINLP
IBM ILOG CPLEX solver extension for Mixed Integer Nonlinear Programming (MINLP) in Java

#### addProd(x,y)
<pre>
define:
  let x ∈ R
  let y ∈ {0,1}
  v = addProd(x,y)  ↔ v = x*y
dedution: 
  if(y=1){
    v=x
  }else{ 
    v=0 
  }
linear transformation:
  let v ∈ R
  M*(y-1) + x ≤ v ≤ x - M*(y-1)
  -M*y ≤ v ≤ M*y
</pre>
