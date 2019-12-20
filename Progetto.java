import java.util.Scanner;
import java.util.Arrays;

public class Progetto {
    private static Scanner scanner;
    private static String input;

    public static void main(String[] args) {
        System.out.println("Input:");

        //Lettura da stdIn
        scanner = new Scanner(System.in);
        input = scanner.nextLine();

        //Parsing dell'input
        double[] sample = importFromStdIn(input);

        //Calcolo e stampa della mediana (inferiore) pesata
        System.out.println("Output:\n"+getWeightedMedian(sample, 0, sample.length-1));
    }

    /**
     * Esegue il parsing dell'input
     *
     * @param str la stringa di cui effettuare il parsing
     * @return array di double composto dei valori (pesi)
     */
    public static double[] importFromStdIn(String str) {
        //Eliminazione del '.' di terminazione dell'input, sostituzione spazi (' ') con '', split ad ogni ','
        String[] clean = str.substring(0, str.length() - 1).replaceAll("\\s", "").split(",");

        //Conversione di stringhe in double
        double[] converted = new double[clean.length];
        for (int i = 0; i < clean.length; i++) {
            converted[i] = Double.parseDouble(clean[i]);
        }

        return converted;
    }

    /**
     * Calcola la mediana (inferiore) pesata
     *
     * @param a array di double da cui prelevare i valori (pesi)
     * @param p indice di partenza
     * @param r indice di arrivo
     * @return mediana (inferiore) pesata
     */
    public static double getWeightedMedian(double[] a, int p, int r) {
        System.out.println("\n************ DEBUG getWeightedMedian() ************");

        //Caso base (r e' uguale a p)
        if (r == p) {
            System.out.println("**Base case -> "+ a[p]);
            return a[p];
        }

        //Partizione randomizzata dell'array a (da indice p ad indice r)
        int q = randomizedPartition(a, p, r);

        double leftWeight = sumElements(a, 0, q-1);
        double rightWeight = sumElements(a, q+1, a.length-1);
        double totalWeight = sumElements(a, 0, a.length-1);

        //Caso base (leftWeight o rightWeight raggiungono esattamente 0.5)
        if (leftWeight/totalWeight == 0.5) {
            return a[q-1];
        } else if (rightWeight/totalWeight == 0.5) {
            return a[q];
        }

        System.out.print("**DEBUG w(left) \u2248 "+ (Math.round(leftWeight * 100))/100.0);
        System.out.print(" ("+ Math.round(leftWeight/totalWeight * 10000.00)/100.0+"%)");
        System.out.print(", w(pivot) = "+ a[q] + " ("+ (Math.round(a[q]/totalWeight * 10000.00)/100.0) +"%)");
        System.out.print(", w(right) \u2248 "+ (Math.round(rightWeight * 100))/100.0);
        System.out.println(" ("+ Math.round(rightWeight/totalWeight * 10000.00)/100.0+"%)");

        //Se le partizioni sono bilanciate, il pivot è la mediana (inferiore) pesata
        if (leftWeight/totalWeight<0.5 && rightWeight/totalWeight<0.5) {
            return a[q];
        } else {
            //Assegna il peso del pivot alla partizione piu' leggera e richiama ricorsivamente getWeightedMedian()
            if (leftWeight > rightWeight) {
                rightWeight += a[q]/totalWeight;
                return getWeightedMedian(a, p, q-1);
            } else {
                leftWeight += a[q]/totalWeight;
                return getWeightedMedian(a, q+1, r);
            }
        }
    }

    /**
     * Ripartisce i valori (pesi) minori e maggiori relativamente a sinistra e a destra
     * rispetto ad un valore (pivot)
     *
     * @param a l'array di double da cui prelevare gli elementi
     * @param p indice di partenza
     * @param r indice di arrivo
     * @return il valore 'pivot'
     */
    public static int randomizedPartition(double[] a, int p, int r) {
        System.out.println("**DEBUG before randPart(): "+Arrays.toString(a));

        //Seleziona il pivot randomicamente
        //int index = (int) Math.round(random(123456789) * (r-p+1));
        int index = getRandomIndex(p, r);

        System.out.println("**DEBUG pivot: "+a[index]+" (index: "+index+")");
        double pivot = a[index];

        //Scambio a[index] con a[r] (si vuole spostare il pivot in fondo)
        swap(a, index, r);

        int i = (p-1);
        for (int j=p; j<r; j++) {
            //Se l'elemento e' piu' piccolo del pivot
            if (a[j] <= pivot) {
                i++;
                //Scambio a[i] con a[j]
                swap(a, i, j);
            }
        }
        //Scambio a[i+1] con a[r]
        swap(a, i+1, r);

        System.out.println("**DEBUG after randPart(): "+Arrays.toString(a));

        return i+1;
    }

    /**
     * Scambia la posizione di due elementi in un array di double
     *
     * @param array l'array di double
     * @param a il primo elemento da scambiare
     * @param b il secondo elemento da scambiare
     */
    public static void swap(double[] array, int a, int b) {
        double tmp = array[a];
        array[a] = array[b];
        array[b] = tmp;
    }

    /**
     * Calcola un numero random tra 1 ed r-p+1
     *
     * @param p il primo elemento
     * @param r il secondo elemento
     * @return numero random tra 1 e r-p+1
     */
    public static int getRandomIndex(int p, int r) {
        return p + (int) Math.floor(Math.random() * (r - p + 1));
    }

    /**
     * Calcola un numero random (Algoritmo 8)
     *
     * @return numero random computato
     */
    public static double random(double seed) {
        final int a = 16087;
        final int m = 2147483647;
        final int q = 127773;
        final int r = 2836;

        double lo, hi, test;
        hi = Math.ceil(seed / q);
        lo = seed - q * hi;
        test = a * lo - r * hi;
        if (test < 0.0) {
            seed = test + m;
        } else {
            seed = test;
        }
        return seed/m;
    }


    /**
     * Somma i valori degli elementi in un array di double
     *
     * @param a l'array di double
     * @param p indice di partenza
     * @param r indice di arrivo
     * @return somma dei valori degli elementi
     */
    public static double sumElements(double[] a, int p, int r) {
        double sum = 0;
        for (int i=p; i<=r; i++) {
            sum += a[i];
        }

        return sum;
    }
}