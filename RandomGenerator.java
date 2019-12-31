/**
 * Questa classe genera numeri casuali.
 *
 * @author  Amos Cappellaro
 * @version 1.0
 * @since   2019-12-31
 */
public class RandomGenerator {
    private double seed;

    /**
     * Costruttore della classe, genera un'istanza di RandomGenerator, fissando il seme iniziale a s.
     */
    public RandomGenerator(double s) {
        seed = s;
    }

    /**
     * Restituisce un numero compreso tra 0 e 1 (ed aggiorna il seme) (Algoritmo 8 degli appunti)
     *
     * @return seed/m un numero compreso tra 0 e 1
     */
    public double get() {
        // Costanti
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
        return seed / m;
    }
}