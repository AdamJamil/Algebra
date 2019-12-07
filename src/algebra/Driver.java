package algebra;

public class Driver
{
    Driver()
    {
        for (Element g : GUtils.findGenerators(new ProductGroup(new S(4), new S(3))))
            System.out.println(g);
    }

    public static void main(String[] args)
    {
	    new Driver();
    }
}
