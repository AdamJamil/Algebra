package algebra;

public class Driver
{
    Driver()
    {
        Group Z2 = new Z(2);
        Group Z4 = new Z(4);
        Group Z24 = new ProductGroup(new Group[]{Z2, Z4});
        Z24.generateNormSub();
        Group quotient = new QuotientGroup(Z24, Z24.normSub[1]);
        for (Element element : quotient.elements)
            System.out.println(element);
    }

    public static void main(String[] args)
    {
	    new Driver();
    }
}
