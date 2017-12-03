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

#### General encoding
* <math>&sum;<sub>i&in;I</sub> ( x<sub>i</sub> )</math>
```javascript
	cplex.sum(I, i -> x[i])
```

* <math>&sum;<sub>i&in;I</sub> &sum;<sub>j&in;J</sub> ( C<sub>ij</sub> x<sub>ij</sub> ) </math>
```javascript
	cplex.sum(I, i -> cplex.sum(J, j -> cplex.prod(C[i][j], x[i][j]))) )
```

* <math> x<sub>i</sub> &le; b<sub>i</sub> 	&forall;(i&in;I)</math>
```javascript
	cplex.forAll(I, (i)->{
		cplex.addLe(x[i], b[i]);
	});
```

* <math>&sum;<sub>i&in;I</sub> ( A<sub>ji</sub> x<sub>i</sub> ) &ge; B<sub>j</sub>	&forall;(j&in;J)</math>
```javascript
	cplex.forAll(J, (j)->{
		cplex.addGe(cplex.sum(I, i -> cplex.prod(A[j][i], x[i])), B[j]);
	});
```

#### A more easy way to encode (sample of mixture problem)

```javascript
MINLP cplex = new MINLP();
        
//conjunto dos ingredientes I = {0, 1, 2}   <->   {Osso, Soja, Peixe}
Set<Integer> I = cplex.range(3);
//conjunto dos nutrientes   J = {0, 1}      <->   {Proteina, Calcio}
Set<Integer> J = cplex.range(2);

//Ci : custo por kg de ingrediente i
double C[] = {0.56, 0.81, 0.46};     
//Aji: quantia do nutriente j por kg de ingrediente i
double A[][] = {
	{0.2, 0.5, 0.4},
	{0.6, 0.4, 0.4},
};
//Bj: quantia minima de nutriente j na racao
double B[] = {0.3, 0.5};

//xi >= 0
IloNumVar x[] = cplex.numVarArrayPos(I);

//obj = sum_i{Ci * xi}
IloNumExpr obj = cplex.sum(I, i -> cplex.prod(C[i],x[i]));

cplex.addMinimize(obj);

//for all j in J
cplex.forAll(J, (j)->{
	//sum_i{Aji * xi} >= Bj
	cplex.addGe(cplex.sum(I, i -> cplex.prod(A[j][i], x[i])), B[j]);
});

//sum_i{xi} = 1
cplex.addEq(cplex.sum(I, i-> x[i]), 1);

cplex.exportModel("model.lp");

if(cplex.solve()){
	System.out.println(cplex.getStatus());
	System.out.println(cplex.getObjValue());
	cplex.forAll(I, (i)->{
		System.out.printf("x[%d] = %f\n", i, cplex.getValue(x[i]));
	});
}else{
	System.out.println(cplex.getStatus());
}
```

