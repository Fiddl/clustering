package space.nates;

import java.util.Arrays;

/**
 * Created by nathan on 4/11/2016.
 */
public class LevensteinDistance {

    public static double computeDistance (CharSequence lhs, CharSequence rhs) {
        int len0 = lhs.length() + 1;
        int len1 = rhs.length() + 1;

        // the array of distances
        int[] cost = new int[len0];
        int[] newcost = new int[len0];

        // initial cost of skipping prefix in String s0
        for (int i = 0; i < len0; i++) cost[i] = i;

        // dynamically computing the array of distances

        // transformation cost for each letter in s1
        for (int j = 1; j < len1; j++) {
            // initial cost of skipping prefix in String s1
            newcost[0] = j;

            // transformation cost for each letter in s0
            for(int i = 1; i < len0; i++) {
                // matching current letters in both strings
                int match = (lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1;

                // computing cost for each transformation
                int cost_replace = cost[i - 1] + match;
                int cost_insert  = cost[i] + 1;
                int cost_delete  = newcost[i - 1] + 1;

                // keep minimum cost
                newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
            }

            // swap cost/newcost arrays
            int[] swap = cost; cost = newcost; newcost = swap;
        }

        // the distance is the cost for transforming all letters in both strings
        return (double) cost[len0 - 1] / Math.max(len0, len1);
    }


    public static double damerauLevenshteinDistance(
            String a, String b, int alphabetLength) {
        final int INFINITY = a.length() + b.length();
        int[][] H = new int[a.length()+2][b.length()+2];
        H[0][0] = INFINITY;
        for(int i = 0; i<=a.length(); i++) {
            H[i+1][1] = i;
            H[i+1][0] = INFINITY;
        }
        for(int j = 0; j<=b.length(); j++) {
            H[1][j+1] = j;
            H[0][j+1] = INFINITY;
        }
        int[] DA = new int[alphabetLength];
        Arrays.fill(DA, 0);
        for(int i = 1; i<=a.length(); i++) {
            int DB = 0;
            for(int j = 1; j<=b.length(); j++) {
                int i1 = DA[b.charAt(j-1)];
                int j1 = DB;
                int d = ((a.charAt(i-1)==b.charAt(j-1))?0:1);
                if(d==0) DB = j;
                H[i+1][j+1] =
                        Math.min(Math.min(H[i][j]+d,
                                H[i+1][j] + 1),Math.min(
                                H[i][j+1]+1,
                                H[i1][j1] + (i-i1-1) + 1 + (j-j1-1)));
            }
            DA[a.charAt(i-1)] = i;
        }
        return (double) H[a.length()+1][b.length()+1] / Math.max(a.length(), b.length());
    }
}
