# MINLP
MINLP is a high level abstraction to encode Mixed Integer Nonlinear Programming (MINLP) models in Java. You can easy donwload the last version here: [MINLP.jar](https://github.com/marcio-da-silva-arantes/MINLP/raw/master/MINLP/dist/MINLP.jar).
This library encode the models using the solvers Cplex and Glpk, so you must install boths to full to use this library, to install this dependences see:
* [Cplex](https://www.ibm.com/br-pt/marketplace/ibm-ilog-cplex) 
* [Glpk](http://ftp.gnu.org/gnu/glpk/) 

#### Main idea of how it is done
<pre>
define:
  let x ∈ R
  let y ∈ {0,1}
  v = cplex.addProd(x,y)  ↔ v = x*y
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

#### General easy encoding
* <math>&sum;<sub>i&in;I</sub> ( x<sub>i</sub> )</math>
```javascript
	mip.sum(I, i -> x[i])
```

* <math>&sum;<sub>i&in;I</sub> &sum;<sub>j&in;J</sub> ( C<sub>ij</sub> x<sub>ij</sub> ) </math>
```javascript
	mip.sum(I, i -> mip.sum(J, j -> mip.prod(C[i][j], x[i][j]))) )
```

* <math> x<sub>i</sub> &le; b<sub>i</sub> 	&forall;(i&in;I)</math>
```javascript
	mip.forAll(I, (i)->{
		mip.addLe(x[i], b[i]);
	});
```

* <math>&sum;<sub>i&in;I</sub> ( A<sub>ji</sub> x<sub>i</sub> ) &ge; B<sub>j</sub>	&forall;(j&in;J)</math>
```javascript
	mip.forAll(J, (j)->{
		mip.addGe(mip.sum(I, i -> mip.prod(A[j][i], x[i])), B[j]);
	});
```

#### A easy way to encode (sample of mixture problem)

```javascript
MINLP mip = new GLPK();  //or new CPLEX(); to use cplex solver
        
//conjunto dos ingredientes I = {0, 1, 2}   <->   {Osso, Soja, Peixe}
Set<Integer I = mip.range(3);
//conjunto dos nutrientes   J = {0, 1}      <->   {Proteina, Calcio}
Set J = mip.range(2);

//Ci : cost per kg of ingredient i
double C[] = {0.56, 0.81, 0.46};     
//Aji: amount of nutrient j per kg of ingredient i
double A[][] = {
	{0.2, 0.5, 0.4},
	{0.6, 0.4, 0.4},
};
//Bj: minimum amount of nutrient j already
double B[] = {0.3, 0.5};

//xi >= 0
Var x[] = mip.numVarArrayPos(I);

//obj = sum_i{Ci * xi}
Expr obj = mip.sum(I, i -> mip.prod(C[i],x[i]));

mip.addMinimize(obj);

//for all j in J
mip.forAll(J, (j)->{
	//sum_i{Aji * xi} >= Bj
	mip.addGe(mip.sum(I, i -> mip.prod(A[j][i], x[i])), B[j]);
});

//sum_i{xi} = 1
mip.addEq(mip.sum(I, i-> x[i]), 1);

mip.exportModel("model.lp");

if(mip.solve()){
	System.out.println(mip.getStatus());
	System.out.println(mip.getObjValue());
	mip.forAll(I, (i)->{
		System.out.printf("x[%d] = %f\n", i, mip.getValue(x[i]));
	});
}else{
	System.out.println(mip.getStatus());
}
```

