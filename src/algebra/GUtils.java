package algebra;

import java.util.HashSet;

import static java.lang.Math.*;

class GUtils
{
    private static int[][] nCk = new int[1000][1000];

    static Element[] findGenerators(Group G)
    {
        Element[] elements = G.elements;

        for (int size = 1; size <= ceil(log(elements.length) / log(2)); size++)
        {
            for (int idx = 0; idx < nCk[elements.length][size]; idx++)
            {
                int key = idx, n = elements.length, k = size;

                Element[] generators = new Element[k];
                int ptr = 0;

                for (Element g : elements)
                {
                    if (k == 0)
                        break;
                    if (key < nCk[n - 1][k - 1])
                    {
                        k--;
                        generators[ptr++] = g;
                    }
                    else
                        key -= nCk[n - 1][k - 1];

                    n--;
                }

                HashSet<Element> curr = new HashSet<>();
                curr.add(elements[0]);

                for (Element g : generators)
                {
                    HashSet<Element> next = new HashSet<>();
                    for (Element pow = g; !pow.equals(elements[0]); pow = pow.multiply(g))
                        next.add(pow);

                    while (!next.isEmpty())
                    {
                        HashSet<Element> temp = new HashSet<>();
                        for (Element g1 : next)
                            for (Element g2 : curr)
                            {
                                temp.add(g1.multiply(g2));
                                temp.add(g2.multiply(g1));
                            }

                        temp.removeAll(next);
                        temp.removeAll(curr);
                        curr.addAll(temp);
                        curr.addAll(next);
                        next = temp;
                    }

                    if (curr.size() == elements.length)
                        return generators;
                }
            }
        }

        return new Element[]{null};
    }

    static
    {
        for (int nMinusK = 0; nMinusK < nCk.length; nMinusK++)
        {
            nCk[nMinusK][0] = 1;
            for (int n = nMinusK + 1; n < nCk.length; n++)
                nCk[n][n - nMinusK] = (n * nCk[n - 1][n - nMinusK - 1]) / (n - nMinusK);
        }
    }
}
