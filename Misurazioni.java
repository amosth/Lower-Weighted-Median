/**
 * Questo programma esegue un'analisi empirica della complessità dell'algoritmo implementato
 * nella classe Progetto (tutti i metodi utilizzati sono stati presenti in questa classe).
 *
 * @author  Amos Cappellaro
 * @version 1.0
 * @since   2019-12-31
 */
public class Misurazioni {
    private static long[] testGranularita;
    private static String input;
    private static RandomGenerator rand = new RandomGenerator(123456789);

    public static void main(String[] args) {

        //Esecuzione di 10 test per il calcolo di granularita' di sistema
        System.out.print("Test granularita' ");
        for (int i = 0; i<10; i++) {
            System.out.print("- "+granularita()+"ms ");
        }
        System.out.print("\n\n");

        //Generazione di input (di dimensione da 1 a 120) con valori casuali
        for (int i = 1; i <= 120; i++) {
            input = "";
            for (int j = 1; j < i; j++) {
                input += Math.round((rand.get() * 100) * 100.0) / 100.0 + ",";
            }
            input += Math.round((rand.get() * 100) * 100.0) / 100.0 + ".";

            //Misurazione dei tempi tramite misurazione() (vedi Algoritmo 9 degli appunti)
            System.out.println(misurazione(input,i,2.33,150,0.02));
        }
    }

    /**
     * Calcola la granularita' del sistema (Algoritmo 4 degli appunti)
     *
     * @return la granularita'
     */
    public static long granularita() {
        long t0 = System.currentTimeMillis();
        long t1 = System.currentTimeMillis();
        while (t1 == t0) {
            t1 = System.currentTimeMillis();
        }
        return t1-t0;
    }

    /**
     * Calcola il numero di ripetizioni (Algoritmo 5 degli appunti)
     *
     * @param p determinare se eseguire il 'lordo' o 'tara' del programma
     * @param d input
     * @param tMin valore del tempo minimo
     * @return la granularita'
     */
    public static int calcolaRip(boolean p, String d, long tMin) {
        long t0 = 0;
        long t1 = 0;
        int rip = 1;

        while((t1-t0) <= tMin) {
            rip = rip*2; //stima di rip con crescita esponenziale
            t0 = System.currentTimeMillis();
            for (int i = 1; i<=rip; i++) {
                if (p) {
                    lordo(d);
                } else {
                    importFromStdIn(d);
                }
            }
            t1 = System.currentTimeMillis();
        }

        //ricerca esatta del numero di ripetizioni per bisezione a 5 cicli
        int max = rip;
        int min = rip/2;
        int cicliErrati = 5;

        while ((max-min) >= cicliErrati) {
            rip = (max+min)/2;
            t0 = System.currentTimeMillis();
            for (int i = 1; i<=rip; i++) {
                if (p) {
                    lordo(d);
                } else {
                    importFromStdIn(d);
                }
            }
            t1 = System.currentTimeMillis();

            if ((t1-t0) <= tMin) {
                min = rip;
            } else {
                max = rip;
            }
        }

        return max;
    }

    /**
     * Scorporo della tara dal lordo (Algoritmo 7 degli appunti)
     *
     * @param d input
     * @param tMin valore del tempo minimo
     * @return il tempo medio di esecuzione
     */
    public static double tempoMedioNetto(String d, long tMin) {
        double ripTara = calcolaRip(false, d, tMin);
        double ripLordo = calcolaRip(true, d, tMin);

        long t0 = System.currentTimeMillis();
        for (int i = 1; i<=ripTara; i++) {
            importFromStdIn(d);
        }

        long t1 = System.currentTimeMillis();
        double tTara = t1-t0; //tempo totale di esecuzione della tara

        t0 = System.currentTimeMillis();
        for (int i=1; i<=ripLordo; i++) {
            lordo(d);
        }

        t1 = System.currentTimeMillis();
        double tLordo = t1-t0; //tempo totale di esecuzione del lordo
        double tMedio = tLordo/ripLordo + tTara/ripTara; //tempo medio di esecuzione

        return tMedio;
    }

    /**
     * Determina il tempo medio con un errore minore di 'capitalDelta' (Algoritmo 9 degli appunti)
     *
     * @param d
     * @param n
     * @param za
     * @param tMin
     * @param capitalDelta
     * @return stringa contenente
     */
    public static String misurazione(String d, int c, double za, long tMin, double capitalDelta) {
        double t = 0;
        double sum2 = 0;
        double m = 0;
        int cn = 0;
        double delta = 0;
        double s = 0;
        double e = 0;

        do {
            for (int i = 1; i<=c; i++) {
                m = tempoMedioNetto(d, tMin);
                t = t + m;
                sum2 = sum2 + m*m;
            }
            cn = cn + c;
            e = t/cn;
            s = Math.sqrt(sum2/cn - e*e);
            delta = (1/Math.sqrt(cn)) * za * s;
        } while (delta >= capitalDelta);

        return c + ";" + e + ";" + delta + ";";
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
        double solution = 0;
        //Caso base (r e' uguale a p)
        if (r == p) {
            solution = a[p];
        }

        //Partizione randomizzata dell'array a (da indice p ad indice r)
        int q = randomizedPartition(a, p, r);

        double leftWeight = sumElements(a, 0, q-1);
        double rightWeight = sumElements(a, q+1, a.length-1);
        double totalWeight = sumElements(a, 0, a.length-1);

        //Se si verifica la condizione della definizione di mediana (inferiore) pesata
        if (leftWeight/totalWeight<0.5 && (leftWeight+a[q])/totalWeight>=0.5) {
            solution = a[q];
        } else {
            //Assegna il peso del pivot alla partizione piu' leggera e richiama ricorsivamente getWeightedMedian()
            if (leftWeight > rightWeight) {
                rightWeight += a[q]/totalWeight;
                solution = getWeightedMedian(a, p, q-1);
            } else if (leftWeight < rightWeight) {
                leftWeight += a[q]/totalWeight;
                solution = getWeightedMedian(a, q+1, r);
            }
        }

        return solution;
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
        //Seleziona il pivot randomicamente
        RandomGenerator rand = new RandomGenerator(123456789);
        int index = p + (int) Math.floor(rand.get() * (r-p+1));
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
        double temp = array[a];
        array[a] = array[b];
        array[b] = temp;
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

    /**
     * Metodo funzionale alla misurazione dei tempi (serve a determinare
     * l'esecuzione del programma 'lordo' in funzione del tempo)
     *
     * @param input la stringa dei valori in input
     * @return la mediana (inferiore) pesata
     */
    public static double lordo(String input) {
        double[] sample = importFromStdIn(input);
        return getWeightedMedian(sample, 0, sample.length-1);
    }
}