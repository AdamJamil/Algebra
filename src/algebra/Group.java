package algebra;

import java.util.*;

import static algebra.Element.primes;

abstract class Group
{
    Element[] elements;
    Group[] sub = null, normSub = null;

    abstract void generateSub();
    abstract void generateNormSub();

    @Override
    public String toString()
    {
        String out = "{";
        for (Element element : elements)
            out += element + ", ";

        return out.substring(0, out.length() - 2) + "}";
    }
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
                for (; order % primes[ptr] == 0; count++)
                    order /= primes[ptr];

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
    Group[] groups;

    ProductGroup flatten()
    {
        ArrayList<Group> allGroups = new ArrayList<>();

        for (Group group : groups)
            if (group instanceof ProductGroup)
            {
                ProductGroup flat = ((ProductGroup) group).flatten();
                Collections.addAll(allGroups, flat.groups);
            }
            else
                allGroups.add(group);

        Group[] out = new Group[allGroups.size()];
        allGroups.toArray(out);

        return new ProductGroup(out);
    }

    @Override
    void generateNormSub()
    {
        for (Group group : groups)
            if (group.normSub == null)
                group.generateNormSub();

        int total = 1;
        for (Group group : groups)
            total *= group.normSub.length;

        normSub = new Group[total];

        for (int i = 0; i < total; i++)
        {
            Group[] next = new Group[groups.length];
            int idx = i;
            for (int j = 0; j < groups.length; j++)
            {
                next[j] = groups[j].normSub[idx % groups[j].normSub.length];
                idx /= groups[j].normSub.length;
            }

            normSub[i] = new ProductGroup(next);
        }
    }

    @Override
    void generateSub()
    {
        for (Group group : groups)
            if (group.sub == null)
                group.generateSub();

        int total = 1;
        for (Group group : groups)
            total *= group.sub.length;

        sub = new Group[total];

        for (int i = 0; i < total; i++)
        {
            Group[] next = new Group[groups.length];
            int idx = i;
            for (int j = 0; j < groups.length; j++)
            {
                next[j] = groups[j].sub[idx % groups[j].sub.length];
                idx /= groups[j].sub.length;
            }

            sub[i] = new ProductGroup(next);
        }
    }

    ProductGroup(Group... groups)
    {
        this.groups = groups;

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

class QuotientGroup extends Group
{
    HashMap<Element, Element> rep;
    Group N;

    @Override
    void generateNormSub()
    {

    }

    @Override
    void generateSub()
    {

    }

    QuotientGroup(Group G, Group N)
    {
        elements = new Coset[G.elements.length / N.elements.length];
        rep = new HashMap<>();
        this.N = N;

        HashSet<Element> hit = new HashSet<>();
        int ptr = 0;

        for (Element g : G.elements)
        {
            if (hit.contains(g))
                continue;

            elements[ptr++] = new Coset(g, this);

            for (Element n : N.elements)
            {
                Element gn = g.multiply(n);
                hit.add(gn);
                rep.put(gn, g);
            }
        }
    }
}