package algebra;

public class Driver
{
    Driver()
    {
        Group Z2 = new Z(2);
        ProductGroup A = new ProductGroup(Z2, Z2);
        A = new ProductGroup(A, new ProductGroup(A, Z2));
        for (Element element : A.elements)
            System.out.println(element);
        for (Element element : A.flatten().elements)
            System.out.println(element);

    }

    public static void main(String[] args)
    {
	    new Driver();
    }
}
