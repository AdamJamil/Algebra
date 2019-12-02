package algebra;

public class Driver
{
    Driver()
    {
        Group Z2 = new Z(72);
        Z2.generateNormSub();
        for (Group group : Z2.normSub)
        {
            for (Element element : group.elements)
                System.out.println(element);
            System.out.println();
        }
    }

    public static void main(String[] args)
    {
	    new Driver();
    }
}
