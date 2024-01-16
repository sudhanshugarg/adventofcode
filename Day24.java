import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Collections;
import java.lang.Enum;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.Set;
import java.util.HashSet;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.apache.commons.math4.legacy.linear.*;
import org.apache.commons.math4.legacy.exception.*;
import org.apache.commons.math4.core.jdkmath.*;
import org.apache.commons.math4.legacy.core.Pair;
import org.apache.commons.math4.legacy.fitting.leastsquares.*;
import org.apache.commons.math4.legacy.optim.*;

//javac -classpath ".:/Users/sugarg/coding/adventofcode/apache-commons/commons-math-4.0-beta1/commons-math4-legacy-4.0-beta1.jar:" Day24.java
class Day24 {

  public static void main(String[] args) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(args[0]));
      String line = reader.readLine();
      int id = 0;

      List<String> input = new ArrayList<>();
      while (line != null) {
        input.add(line);
        line = reader.readLine();
      }
      reader.close();

      int totalVariables = input.size() + 3;
      List<Hailstone> hs = new ArrayList<>(input.size());
      for (int i = 0; i < input.size(); i++) {
        Hailstone h = new Hailstone(input.get(i), i, input.size());
        hs.add(h);
        //System.out.println(h);
      }

      BigDecimal low = new BigDecimal("200000000000000");
      BigDecimal high = new BigDecimal("400000000000000");
      //BigDecimal low = new BigDecimal("7");
      //BigDecimal high = new BigDecimal("27");
      int total = part1(hs, low, high);
      System.out.println("part 1: " + String.valueOf(total));
      //part2Test();

      //long p2 = part2ls(hs, totalVariables, Integer.parseInt(args[1]), new BigDecimal(args[2]), new BigDecimal(args[3]), Double.parseDouble(args[4]));
      long p2 = part2Reference(hs, Integer.parseInt(args[1]), Integer.parseInt(args[2]));
      System.out.println("part 2: " + String.valueOf(p2));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void part2LinearEquations(List<Hailstone> hs, int totalVariables, int margin) {
      List<BigDecimal> velocities = new ArrayList<>();
      int vlow = -margin, vhigh = margin;
      for (int xi = vlow; xi <= vhigh; xi++)
      for (int xj = vlow; xj <= vhigh; xj++)
      for (int xk = vlow; xk <= vhigh; xk++) {
        List<BigDecimal> velo = new ArrayList<>();
        velo.add(new BigDecimal(xi));
        velo.add(new BigDecimal(xj));
        velo.add(new BigDecimal(xk));

        Long p2 = part2(hs, totalVariables, velo);
        if (p2 != null) {
          System.out.println(velo);
          System.out.println("part 2: " + String.valueOf(p2));
          xi = xj = xk = vhigh + 1;
        }
      }
  }

  public static void populateEquationsAll(SystemOfEquations soe, List<Hailstone> hs, boolean isJacobian) {
    for (int i = 0; i < hs.size(); i++) {
      if (isJacobian) {
        soe.equations.addAll(hs.get(i).jacobians3);
      }
      else {
        soe.equations.addAll(hs.get(i).linearEquations3);
      }
    }
  }

  public static RealMatrix createIdentityMatrix(int n) {
    double[][] id = new double[n][n];
    for (int i = 0; i < n; i++) id[i][i] = 1.0;
    return MatrixUtils.createRealMatrix(id);
  }

  public static void part2Test() {
    RealMatrix coefficients =
    new Array2DRowRealMatrix(new double[][] { { 2, 3, -2 }, { -1, 7, 6 }, { 4, -3, -5 } },
                       false);
    DecompositionSolver solver = new LUDecomposition(coefficients).getSolver();
    RealVector constants = new ArrayRealVector(new double[] { 1, -2, 1 }, false);
    RealVector solution = solver.solve(constants);
    System.out.println(solution);

        double[][] matrixData = new double[][]{
                {1, 0, 0, 0},
                {1, 0, 1, 0},
                {1, 1, 0, 0},
                {1, 1, 1, 1}
        };
        RealMatrix matrix = MatrixUtils.createRealMatrix(matrixData);
        RealMatrix inverse = MatrixUtils.inverse(matrix);
        System.out.println(inverse);
  }

  public static boolean doesRockMeetAllStones(List<Hailstone> hs, int n, List<BigDecimal> velocities, List<BigDecimal> point) {
    //for each hailstone, reduce its velocity by the rocks velocity
    for (int i = 0; i < n; i++) {
      Hailstone h = hs.get(i);
      h.vx = h.vx.add(velocities.get(0).negate());
      h.vy = h.vy.add(velocities.get(1).negate());
      h.vz = h.vz.add(velocities.get(2).negate());
      h.resetLine();
      //System.out.println(h);
    }
    //System.out.println();
    //System.out.println();
    //System.out.println();

    boolean meets = true;
    BigDecimal low = new BigDecimal("-10000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
    BigDecimal high = new BigDecimal("10000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
    BigDecimal[] ts = new BigDecimal[2];

    //now, for each pair of hailstones, check if they meet in the future
    Hailstone s1, s0;
    for (int i = 0; meets && i < n; i++)
    for (int j = i+1; meets && j < n; j++) {
      s0 = hs.get(i);
      s1 = hs.get(j);
      meets = s0.meetsXY(s1, low, high, ts);
      //System.out.println(s0);
      //System.out.println(s1);
      if (meets) {
        //System.out.println("meets");
        if (BigDecimal.ZERO.equals(ts[0]) && BigDecimal.ZERO.equals(ts[1])) continue;

        List<BigDecimal> p0 = s0.where(ts[0]);
        List<BigDecimal> p1 = s1.where(ts[1]);
        for (int k = 0; meets && k < p0.size(); k++) {
          meets = p0.get(k).equals(p1.get(k));
        }

        if (meets) {
          point.clear();
          point.addAll(p0);
        }
      } else {
        //System.out.println("no meets");
      }
    }

    for (int i = 0; i < n; i++) {
      Hailstone h = hs.get(i);
      h.vx = h.vx.add(velocities.get(0));
      h.vy = h.vy.add(velocities.get(1));
      h.vz = h.vz.add(velocities.get(2));
      h.resetLine();
    }
    return meets;
  }

  public static long part2Reference(List<Hailstone> hs, int subset, int margin) {
    List<BigDecimal> velocities = new ArrayList<>();
    int vlow1 = -margin, vhigh1 = margin;
    int vlow2 = -margin, vhigh2 = margin;
    int vlow3 = -margin, vhigh3 = margin;
    boolean flag = true;
    int ct = 1;

    List<Hailstone> hs2 = new ArrayList<>();
    for (int i = 0; i < subset && i < hs.size(); i++) {
      hs2.add(hs.get(i));
    }

    List<BigDecimal> point = new ArrayList<>();
    BigDecimal result = BigDecimal.ONE;
    for (int xi = vlow1; xi <= vhigh1; xi++)
    for (int xj = vlow2; xj <= vhigh2; xj++)
    for (int xk = vlow3; xk <= vhigh3; xk++) {
      List<BigDecimal> velo = new ArrayList<>();
      velo.add(new BigDecimal(xi));
      velo.add(new BigDecimal(xj));
      velo.add(new BigDecimal(xk));

      flag = doesRockMeetAllStones(hs2, subset, velo, point);
      if (flag) {
        ct++;
        System.out.println(String.format("With velocities %s, subset of size %d works at point %s", velo, subset, point));
        flag = doesRockMeetAllStones(hs, hs.size(), velo, point);
        if (flag) {
          System.out.println(String.format("result number %d", ct));
          System.out.println(velo);
          System.out.println(point);
          result = point.get(0).add(point.get(1)).add(point.get(2));
          xi = vhigh1 + 1;
          xj = vhigh2 + 1;
          xk = vhigh3 + 1;
        } else {
          System.out.println(String.format("However, doesn't work with all %d stones", hs.size()));
        }
      }
    }
    return result.longValueExact();
  }

  public static void initialize2(BigDecimal initpos, BigDecimal initvelocity, List<BigDecimal> x0) {
    //initialize x,y,z
    x0.add(initpos);
    x0.add(initpos);
    x0.add(initpos);
    //initialize vx,vy,vz
    x0.add(initvelocity);
    x0.add(initvelocity);
    x0.add(initvelocity);
  }

  public static void initialize(List<BigDecimal> prev, List<Hailstone> hs) {
      BigDecimal x = BigDecimal.ZERO;
      BigDecimal y = BigDecimal.ZERO;
      BigDecimal z = BigDecimal.ZERO;
      BigDecimal a = BigDecimal.ZERO;
      BigDecimal b = BigDecimal.ZERO;
      BigDecimal c = BigDecimal.ZERO;

      for (int i = 0; i < hs.size(); i++) {
        Hailstone h = hs.get(i);
        x = x.add(h.px);
        y = y.add(h.py);
        z = z.add(h.pz);
        a = a.add(h.vx);
        b = b.add(h.vy);
        c = c.add(h.vz);
      }
      BigDecimal hssize = new BigDecimal(hs.size());
      x = x.divideToIntegralValue(hssize);
      y = y.divideToIntegralValue(hssize);
      z = z.divideToIntegralValue(hssize);
      a = a.divideToIntegralValue(hssize);
      b = b.divideToIntegralValue(hssize);
      c = c.divideToIntegralValue(hssize);
      prev.clear(); prev.add(x); prev.add(y); prev.add(z); prev.add(a); prev.add(b); prev.add(c);

  }

  public static RealVector buildInitialVector(List<Hailstone> hs, BigDecimal initpos, BigDecimal initvelocity) {
    List<BigDecimal> init = new ArrayList<>();
    //initialize(init, hs);
    initialize2(initpos, initvelocity, init);

    double[] initval = new double[init.size() + hs.size()];
    for (int i = 0; i < init.size(); i++) initval[i] = init.get(i).doubleValue();
    for (int i = 0; i < hs.size(); i++) initval[i+init.size()] = 1.0;
    return new ArrayRealVector(initval);
  }

  public static RealVector buildObservedVector(List<Hailstone> hs) {
    List<BigDecimal> init = new ArrayList<>();
    for (int i = 0; i < hs.size(); i++) {
      init.addAll(hs.get(i).equations3Value);
    }
    double[] initval = new double[init.size()];
    for (int i = 0; i < init.size(); i++) initval[i] = init.get(i).doubleValue();
    return new ArrayRealVector(initval);
  }

  public static ConvergenceChecker<LeastSquaresProblem.Evaluation> buildChecker() {
    return null;
  }


  public static long part2ls(List<Hailstone> hs, int totalVariables, int maxIter, BigDecimal initpos, BigDecimal initvelocity, double lambda) {
    /*
    LeastSquaresProblem lsp = LeastSquaresFactory.create(
      MultivariateVectorFunction model, //todo
      RealVector observed, //actual values seen, like 19, 20 etc.
      RealVector start, //initial guess, x0
      ConvergenceChecker<LeastSquaresProblem.Evaluation> checker, //todo
      int maxEvaluations, //100000
      int maxIterations //100
    );
    */

    final RealVector start = buildInitialVector(hs, initpos, initvelocity);
    final RealVector observed = buildObservedVector(hs);
    //System.out.println(observed);
    int maxEvaluations = maxIter * 1000;
    LeastSquaresProblem lsp = LeastSquaresFactory.create(
      new HailstoneJacobian(hs),
      observed, //actual values seen, like 19, 20 etc.
      start, //initial guess, x0
      buildChecker(), //todo
      maxEvaluations, //1000
      maxIter //100
    );

    double eps = lambda;
    LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer(100.0, eps, eps, eps, 1e-100);
    final LeastSquaresOptimizer.Optimum result = optimizer.optimize(lsp);
    RealVector ans = result.getPoint();
    System.out.println(ans);
    String info = "Evals = " + String.valueOf(result.getEvaluations()) + ", Iters = " + String.valueOf(result.getIterations());
    System.out.println(info);
    System.out.println(ans.getEntry(0));
    System.out.println(ans.getEntry(1));
    System.out.println(ans.getEntry(2));
    long ans2 = Math.round(ans.getEntry(0)) + Math.round(ans.getEntry(1)) + Math.round(ans.getEntry(2));
    return ans2;
  }

  public static Long part2(List<Hailstone> hs, int totalVariables, List<BigDecimal> velocities) {
    SystemOfEquations F = new SystemOfEquations();
    populateEquationsAll(F, hs, false);

    boolean hasChanged = true;
    //set x0
    List<BigDecimal> x0 = new ArrayList<>(totalVariables);

    for (int i = 0; i < hs.size(); i++) {
      //intitalize timestamps
      x0.add(BigDecimal.ONE);
    }
    //initialize x,y,z
    x0.add(BigDecimal.ONE);
    x0.add(BigDecimal.ONE);
    x0.add(BigDecimal.ONE);
    //initialize vx,vy,vz
    x0.addAll(velocities);
    //System.out.println(x0);
    //Ax = B
    //At . Ax = At. B
    //x = (At.A)-1 . At. B

    //x1 = x0 minus jacobian.inverse(x0) * F.eval(x0);
    double[][] F_eval = F.eval2(x0, totalVariables); //15 x 11 
    //printArr2D(F_eval);

    RealMatrix A = MatrixUtils.createRealMatrix(F_eval);
    //System.out.println(getDim(F_eval_m));
    //System.out.println(F_eval_m);

    RealMatrix At = A.transpose();
    RealMatrix AtA = At.multiply(A);

    RealMatrix identity = createIdentityMatrix(AtA.getRowDimension()).scalarMultiply(0.0001);
    RealMatrix AtA2 = AtA.add(identity);
   
    RealMatrix AtA_inverse = MatrixUtils.inverse(AtA2);

    RealMatrix Amult = AtA_inverse.multiply(At);
    RealVector B = buildObservedVector(hs);
    RealVector ans = Amult.operate(B);

    List<BigDecimal> ans2 = createFromVector(ans);
    ans2.addAll(velocities);

    double[][] check = F.eval(ans2);
    boolean found = true;
    double eps = 1e-3;
    for (int i = 0; i < B.getDimension(); i++) {
      String msg = String.format("checking %4f against %4f", check[i][0], B.getEntry(i));
      //System.out.println(msg);
      if (Math.abs(check[i][0] - B.getEntry(i)) > eps) {
        found = false;
        break;
      }
    }
    if (!found) return null;
    System.out.println(ans);
    return new Long(Math.round(ans.getEntry(totalVariables-3)) + Math.round(ans.getEntry(totalVariables-2)) + Math.round(ans.getEntry(totalVariables-1)));
  }

  public static List<BigDecimal> createFromVector(RealVector v) {
    int n = v.getDimension();
    List<BigDecimal> res = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      res.add(new BigDecimal(v.getEntry(i)));
    }

    return res;
  }

  public static void printVec(List<BigDecimal> x0, int len, int scale) {
    System.out.println("printing vec");
    for (int i = 0; i < len; i++) {
      System.out.print(x0.get(i).setScale(scale, RoundingMode.HALF_UP));
      System.out.print(",");
    }
    System.out.println();
  }
  
  public static void printArr2D(double[][] F_eval) {
    for (int i = 0; i < F_eval.length; i++) {
      for (int j = 0; j < F_eval[0].length; j++) {
        System.out.print(F_eval[i][j]);
        System.out.print(",");
      }
      System.out.println();
    }
    System.out.println();
  }

  public static String getDim(RealMatrix m) {
    return String.valueOf(m.getRowDimension()) + "," + String.valueOf(m.getColumnDimension());
  }

  public static List<BigDecimal> subtract(List<BigDecimal> x0, RealMatrix m) {
    List<BigDecimal> result = new ArrayList<>(x0.size());
    for (int i = 0; i < x0.size(); i++) {
      BigDecimal b = new BigDecimal(m.getEntry(i,0));
      result.add(x0.get(i).add(b.negate()));
    }
    return result;
  }

  public static boolean isZeros(RealMatrix m) {
    boolean isZeros = true;
    double eps = 1e-3;
    double[] col = m.getColumn(0);
    for (int i = 0; isZeros && i < col.length; i++) {
      isZeros = Math.abs(col[i]) <= eps;
    }
    return isZeros;
  }

  public static boolean isZerosDecimal(List<BigDecimal> arr) {
    boolean isZeros = true;
    double eps = 1e-3;
    for (int i = 0; isZeros && i < arr.size(); i++) {
      isZeros = Math.abs(arr.get(i).doubleValue()) <= eps;
    }
    return isZeros;
  }

  public static int part1(List<Hailstone> hs, BigDecimal low, BigDecimal high) {
    int n = hs.size();
    int ans = 0;
    BigDecimal[] ts = new BigDecimal[2];
    for (int i = 0; i < n; i++) {
      Hailstone hi = hs.get(i);
      for (int j = i+1; j < n; j++) {
        Hailstone hj = hs.get(j);
        //System.out.println(hi);
        //System.out.println(hj);
        if (hi.meetsXY(hj, low, high, ts)) {
          //System.out.println("YES");
          ans++;
        } else {
          //System.out.println("NO");
        }
        //System.out.println();
      }
    }
    return ans;
  }
}

class Hailstone {
  public BigDecimal px, py, pz;
  public BigDecimal vx, vy, vz;
  public BigDecimal a, b, c;
  public List<Equation> equations3;
  public List<Equation> linearEquations3;
  public List<Equation> jacobians3;
  public List<BigDecimal> equations3Value;
  public int id;

  Hailstone(String s, int identity, int numHailStones) {
    int pos = 0;
    for (int i = 0; i < s.length(); i++) 
      if (s.charAt(i) == '@') {
        pos = i;
        break;
      }

    String[] init = s.substring(0, pos-1).split(",");
    px = new BigDecimal(init[0].trim());
    py = new BigDecimal(init[1].trim());
    pz = new BigDecimal(init[2].trim());

    String[] velocities = s.substring(pos+1).split(",");
    vx = new BigDecimal(velocities[0].trim());
    vy = new BigDecimal(velocities[1].trim());
    vz = new BigDecimal(velocities[2].trim());

    a = vy;
    b = vx.multiply(new BigDecimal("-1"));
    c = vx.multiply(py).add(vy.multiply(px).negate());

    //create the x equation
    //each hailstone introduces a new timestamp. x,y,z,a,b,c are assumed to be already the first 6
    //lets pass in hailstones id also.

    equations3Value = new ArrayList<>();
    id = identity;
    Term xt = new Term(Arrays.asList(0), BigDecimal.ONE, 0);
    Term at = new Term(Arrays.asList(3, id), BigDecimal.ONE, 3);
    Term xts = new Term(Arrays.asList(id), vx.negate(), id);
    equations3Value.add(px);
    //Term xc = new Term(Arrays.asList(), px.negate(), totalVariables);
    //Equation xeq = new Equation(Arrays.asList(xt, at, xts, xc));
    Equation xeq = new Equation(Arrays.asList(xt, at, xts));

    Term yt = new Term(Arrays.asList(1), BigDecimal.ONE, 1);
    Term bt = new Term(Arrays.asList(4, id), BigDecimal.ONE, 4);
    Term yts = new Term(Arrays.asList(id), vy.negate(), id);
    equations3Value.add(py);
    //Term yc = new Term(Arrays.asList(), py.negate(), totalVariables);
    //Equation yeq = new Equation(Arrays.asList(yt, bt, yts, yc));
    Equation yeq = new Equation(Arrays.asList(yt, bt, yts));

    Term zt = new Term(Arrays.asList(2), BigDecimal.ONE, 2);
    Term ct = new Term(Arrays.asList(5, id), BigDecimal.ONE, 5);
    Term zts = new Term(Arrays.asList(id), vz.negate(), id);
    equations3Value.add(pz);
    //Term zc = new Term(Arrays.asList(), pz.negate(), totalVariables);
    //Equation zeq = new Equation(Arrays.asList(zt, ct, zts, zc));
    Equation zeq = new Equation(Arrays.asList(zt, ct, zts));

    equations3 = Arrays.asList(xeq, yeq, zeq);

    Term jxt = new Term(Arrays.asList(), BigDecimal.ONE, 0);
    Term jat = new Term(Arrays.asList(id), BigDecimal.ONE, 3);
    Term jxts1 = new Term(Arrays.asList(3), BigDecimal.ONE, id);
    Term jxts2 = new Term(Arrays.asList(), vx.negate(), id);
    Equation jxeq = new Equation(Arrays.asList(jxt, jat, jxts1, jxts2));

    Term jyt = new Term(Arrays.asList(), BigDecimal.ONE, 1);
    Term jbt = new Term(Arrays.asList(id), BigDecimal.ONE, 4);
    Term jyts1 = new Term(Arrays.asList(4), BigDecimal.ONE, id);
    Term jyts2 = new Term(Arrays.asList(), vy.negate(), id);
    Equation jyeq = new Equation(Arrays.asList(jyt, jbt, jyts1, jyts2));

    Term jzt = new Term(Arrays.asList(), BigDecimal.ONE, 2);
    Term jct = new Term(Arrays.asList(id), BigDecimal.ONE, 5);
    Term jzts1 = new Term(Arrays.asList(5), BigDecimal.ONE, id);
    Term jzts2 = new Term(Arrays.asList(), vz.negate(), id);
    Equation jzeq = new Equation(Arrays.asList(jzt, jct, jzts1, jzts2));

    jacobians3 = Arrays.asList(jxeq, jyeq, jzeq);

    Term ljxt = new Term(Arrays.asList(numHailStones), BigDecimal.ONE, numHailStones);
    Term ljxts1 = new Term(Arrays.asList(id, numHailStones + 3), BigDecimal.ONE, id);
    Term ljxts2 = new Term(Arrays.asList(id), vx.negate(), id);
    Equation ljxeq = new Equation(Arrays.asList(ljxt, ljxts1, ljxts2));

    Term ljyt = new Term(Arrays.asList(numHailStones+1), BigDecimal.ONE, numHailStones+1);
    Term ljyts1 = new Term(Arrays.asList(id, numHailStones+4), BigDecimal.ONE, id);
    Term ljyts2 = new Term(Arrays.asList(id), vy.negate(), id);
    Equation ljyeq = new Equation(Arrays.asList(ljyt, ljyts1, ljyts2));

    Term ljzt = new Term(Arrays.asList(numHailStones+2), BigDecimal.ONE, numHailStones+2);
    Term ljzts1 = new Term(Arrays.asList(id, numHailStones+5), BigDecimal.ONE, id);
    Term ljzts2 = new Term(Arrays.asList(id), vz.negate(), id);
    Equation ljzeq = new Equation(Arrays.asList(ljzt, ljzts1, ljzts2));

    linearEquations3 = Arrays.asList(ljxeq, ljyeq, ljzeq);
  }
  
  public void resetLine() {
    a = vy;
    b = vx.multiply(new BigDecimal("-1"));
    c = vx.multiply(py).add(vy.multiply(px).negate());
  }

  private static boolean inRange(BigDecimal x, BigDecimal low, BigDecimal high) {
    return (x.compareTo(low) >= 0) && (x.compareTo(high) <= 0);
  }

  public static BigDecimal intersection(BigDecimal p1, BigDecimal p2, BigDecimal q1, BigDecimal q2) {
    return p2.multiply(q1).add(p1.multiply(q2).negate());
  }

  public boolean isVelocityZero() {
    return this.vx.equals(BigDecimal.ZERO) && this.vy.equals(BigDecimal.ZERO);
  }

  public boolean meetsXY(Hailstone h, BigDecimal low, BigDecimal high, BigDecimal[] timestamp) {
    if (this.px.equals(h.px) && this.py.equals(h.py)) {
      //check if the point lies within the range
      return Hailstone.inRange(px, low, high) && Hailstone.inRange(py, low, high);
    }

    if (this.isVelocityZero() || h.isVelocityZero()) {
      System.out.println("sugarg tell me both velocities are not zero");
      return false;
    }

    //check if parallel
    if (this.a.multiply(h.b).equals(this.b.multiply(h.a))) {
      //if intercepts are not equal, then they can never intersect
      if (!this.c.multiply(h.a).equals(h.c.multiply(this.a))) return false;

      //since the lines are identical, its possible the stones will intersect.
      //System.out.println("sugarg Lets see if this is a case");
      timestamp[0] = timestamp[1] = BigDecimal.ZERO;
      return true;
    }

    BigDecimal x = BigDecimal.ONE;
    BigDecimal y = BigDecimal.ONE;
    if (!this.a.equals(BigDecimal.ZERO)) {
      y = Hailstone.intersection(this.a, h.a, this.c, h.c);
      y = y.divide(Hailstone.intersection(this.b, h.b, this.a, h.a), 2, RoundingMode.HALF_UP);
      x = y.negate().multiply(this.b).add(this.c.negate()).divide(this.a, 2, RoundingMode.HALF_UP);
    } else {
      x = Hailstone.intersection(this.b, h.b, this.c, h.c);
      x = x.divide(Hailstone.intersection(this.a, h.a, this.b, h.b), 2, RoundingMode.HALF_UP);
      y = x.negate().multiply(this.a).add(this.c.negate()).divide(this.b, 2, RoundingMode.HALF_UP);
    }

    if (!inRange(x, low, high) || !inRange(y, low, high)) return false;

    //now that we have the point of intersection, for each, find whether its in the future
    //or in the past
    BigDecimal t1 = BigDecimal.ONE;
    BigDecimal t2 = BigDecimal.ONE;
    if (!this.vx.equals(BigDecimal.ZERO)) {
      t1 = x.add(this.px.negate()).divide(this.vx, 2, RoundingMode.HALF_UP);
    } else {
      t1 = y.add(this.py.negate()).divide(this.vy, 2, RoundingMode.HALF_UP);
    }

    if (!h.vx.equals(BigDecimal.ZERO)) {
      t2 = x.add(h.px.negate()).divide(h.vx, 2, RoundingMode.HALF_UP);
    } else {
      t2 = y.add(h.py.negate()).divide(h.vy, 2, RoundingMode.HALF_UP);
    }

    if (t1.compareTo(BigDecimal.ZERO) >= 0 && t2.compareTo(BigDecimal.ZERO) >= 0) {
      //System.out.println(x + "," + y);
      timestamp[0] = t1;
      timestamp[1] = t2;
      return true;
    }
    return false;
  }

  public List<BigDecimal> where(BigDecimal t) {
    BigDecimal x = px.add(vx.multiply(t));
    BigDecimal y = py.add(vy.multiply(t));
    BigDecimal z = pz.add(vz.multiply(t));
    List<BigDecimal> point = Arrays.asList(x,y,z);
    return point;
  }

  @Override
  public String toString() {
    return "pos = " + px + "," + py + "," + pz + " velocity = " + vx + "," + vy + "," + vz + " line = " + a + "," + b + "," + c;
  }
}


class SystemOfEquations {
  public List<Equation> equations;
  SystemOfEquations() {
    equations = new ArrayList<>();
  }

  public double[][] eval(List<BigDecimal> x0) {
    double[][] result = new double[equations.size()][1];
    for (int i = 0; i < equations.size(); i++) {
      result[i][0] = equations.get(i).eval(x0).doubleValue();
    }
    return result;
  }

  public double[][] eval2(List<BigDecimal> x0, int numVariables) {
    double[][] result = new double[equations.size()][numVariables];
    for (int i = 0; i < equations.size(); i++) {
      Equation e = equations.get(i);
      double[] eresult = e.eval2(x0, numVariables);
      for (int j = 0; j < numVariables; j++)
        result[i][j] = eresult[j];
    }
    return result;
  }
}

class Equation {
  public List<Term> terms;
  Equation() {}
  Equation(List<Term> inputTerms) {
    terms = new ArrayList<>();
    terms.addAll(inputTerms);
  }

  public BigDecimal eval(List<BigDecimal> x0) {
    //it has a list of terms
    BigDecimal result = BigDecimal.ZERO;
    for (int i = 0; i < terms.size(); i++) {
      result = result.add(terms.get(i).eval(x0));
    }
    return result;
  }

  //gives the value of each term
  public double[] eval2(List<BigDecimal> x0, int numVariables) {
    double[] result = new double[numVariables];
    for (int i = 0; i < terms.size(); i++) {
      Term t = terms.get(i);
      result[t.pos] += t.eval(x0).doubleValue();
    }
    return result;
  }
}

class Term {
  public List<Integer> vars;
  public BigDecimal coeff;
  public int pos;

  Term(List<Integer> variables, BigDecimal coefficient, int position) {
    vars = new ArrayList<>();
    vars.addAll(variables);
    coeff = coefficient;
    pos = position;
  }

  public BigDecimal eval(List<BigDecimal> x0) {
    BigDecimal result = coeff;
    for (int i = 0; i < vars.size(); i++) {
      result = result.multiply(x0.get(vars.get(i)));
    }
    return result;
  }
}

class HailstoneJacobian implements MultivariateJacobianFunction {
  private SystemOfEquations F, jacobian;
  HailstoneJacobian(List<Hailstone> hs) {
    F = new SystemOfEquations();
    Day24.populateEquationsAll(F, hs, false);
    jacobian = new SystemOfEquations();
    Day24.populateEquationsAll(jacobian, hs, true);
  }

  private List<BigDecimal> toBigDecimal(RealVector point) {
    double[] pd = point.toArray();
    List<BigDecimal> result = new ArrayList<>();
    for (int i = 0; i < pd.length; i++) {
      BigDecimal b = new BigDecimal(pd[i]);
      result.add(b);
    }
    return result;
  }

  @Override
  public Pair<RealVector, RealMatrix> value(RealVector point) {
    //first argument is F_eval
    //second argument is jacobian
    List<BigDecimal> x0 = toBigDecimal(point);

    double[][] F_eval = F.eval(x0); //15 x 1 (m x 1)
    double[][] jacobi = jacobian.eval2(x0, x0.size()); //mxn matrix 15 x 11

    RealMatrix F_eval_m = MatrixUtils.createRealMatrix(F_eval);
    RealVector val = F_eval_m.getColumnVector(0);
    RealMatrix j = MatrixUtils.createRealMatrix(jacobi);
    return new Pair(val, j);
  }
}
