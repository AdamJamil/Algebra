package algebra;

import java.util.*;

import static algebra.Element.primes;

abstract class Group
{
    Element[] elements;
    Group[] sub = null, normSub = null;

    abstract void generateSub();
    abstract void generateNormSub();

}

class S extends Group
{
    @Override
    void generateNormSub()
    {

    }

    @Override
    void generateSub()
    {

    }

    S(int n)
    {
        int factorial = 1;
        for (int i = 1; i <= n; i++)
            factorial *= i;

        elements = new Element[factorial];

        for (int i = 0; i < factorial; i++)
        {
            int[] arr = new int[n];
            int key = i;
            boolean[] used = new boolean[n];

            for (int j = n; j >= 1; j--)
            {
                int next = key % j;
                key /= j;
                int count = 0;
                for (int k = 0; k < n; k++)
                    if (!used[k])
                    {
                        if (count == next)
                        {
                            arr[n - j] = k;
                            used[k] = true;
                            break;
                        }
                        count++;
                    }
            }

            elements[i] = new Permutation(arr);
        }
    }
}

class Z extends Group
{
    @Override void generateSub() { generateNormSub(); }

    @Override
    void generateNormSub()
    {
        HashMap<Integer, Integer> p = new HashMap<>();
        HashMap<Integer, int[]> power = new HashMap<>();
        int order = elements.length;

        for (int ptr = 0; order > 1; ptr++)
            if (order % primes[ptr] == 0)
            {
                int count = 0;
                while (order % primes[ptr] == 0)
                {
                    order /= primes[ptr];
                    count++;
                }

                p.put(primes[ptr], count);

                int[] arr = new int[count + 1];
                arr[0] = 1;
                for (int i = 1; i < count + 1; i++)
                    arr[i] = arr[i - 1] * primes[ptr];

                power.put(primes[ptr], arr);
            }

        int subgroups = 1;
        for (Integer prime : p.keySet())
            subgroups *= p.get(prime) + 1;

        sub = new Group[subgroups];

        for (int i = 0; i < subgroups; i++)
        {
            ArrayList<Integer> temp = new ArrayList<>();
            temp.add(0);

            int idx = i;

            for (Integer prime : p.keySet())
            {
                int n = idx % (p.get(prime) + 1);
                idx /= p.get(prime) + 1;

                ArrayList<Integer> next = new ArrayList<>();
                for (Integer val : temp)
                    for (int j = 0; j < elements.length; j += elements.length / power.get(prime)[n])
                        next.add((val + j) % elements.length);

                temp = next;
            }

            Collections.sort(temp);

            sub[i] = new Z();
            sub[i].elements = new Element[temp.size()];

            for (int j = 0; j < temp.size(); j++)
                sub[i].elements[j] = new Int(temp.get(j), elements.length);
        }

        normSub = sub;
    }

    Z(int n)
    {
        elements = new Element[n];

        for (int i = 0; i < n; i++)
            elements[i] = new Int(i, n);
    }

    Z() {}
}

class ProductGroup extends Group
{
    @Override
    void generateNormSub()
    {

    }

    @Override
    void generateSub()
    {

    }

    ProductGroup(Group[] groups)
    {
        int count = 1;
        for (Group group : groups)
            count *= group.elements.length;

        elements = new Element[count];

        for (int i = 0; i < count; i++)
        {
            int key = i;
            Element[] next = new Element[groups.length];
            for (int j = 0; j < groups.length; j++)
            {
                next[j] = groups[j].elements[key % groups[j].elements.length];
                key /= groups[j].elements.length;
            }

            elements[i] = new Tuple(next);
        }
    }
}