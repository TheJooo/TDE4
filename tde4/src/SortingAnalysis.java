import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class SortingAnalysis {

    public static void main(String[] args) {
        // Diretório de trabalho atual
        String currentDir = System.getProperty("user.dir");
        System.out.println("Diretório de trabalho: " + currentDir);

        // Arquivos CSV a serem processados
        String[] files = {
            "tde4\\src\\aleatorio_100.csv", "tde4\\\\src\\aleatorio_1000.csv", "tde4\\\\src\\aleatorio_10000.csv",
            "tde4\\\\src\\crescente_100.csv", "tde4\\\\src\\crescente_1000.csv", "tde4\\\\src\\crescente_10000.csv",
            "tde4\\\\src\\decrescente_100.csv", "tde4\\\\src\\decrescente_1000.csv", "tde4\\\\src\\decrescente_10000.csv"
        };

        for (String file : files) {
            System.out.println("\nArquivo: " + file);
            int[] data = readData(file);

            if (data.length == 0) {
                System.out.println("Pulando ordenação devido à falha na leitura do arquivo.");
                continue;
            }

            // Benchmark Bubble Sort
            long bubbleTime = benchmarkSort("Bubble Sort", data, SortingAnalysis::bubbleSort);
            System.out.println("Bubble Sort Average Time: " + bubbleTime + " ns");

            // Benchmark Insertion Sort
            long insertionTime = benchmarkSort("Insertion Sort", data, SortingAnalysis::insertionSort);
            System.out.println("Insertion Sort Average Time: " + insertionTime + " ns");

            // Benchmark Quick Sort
            long quickTime = benchmarkSort("Quick Sort", data, arr -> quickSort(arr, 0, arr.length - 1));
            System.out.println("Quick Sort Average Time: " + quickTime + " ns");
        }
    }

    private static long benchmarkSort(String sortName, int[] data, SortFunction sortFunction) {
        final int iterations = 100; // Number of iterations for averaging
        long totalTime = 0;

        for (int i = 0; i < iterations; i++) {
            // Create a fresh copy of the data for each iteration
            int[] arrayToSort = Arrays.copyOf(data, data.length);

            long startTime = System.nanoTime();
            sortFunction.sort(arrayToSort);
            long endTime = System.nanoTime();

            totalTime += (endTime - startTime);

            // Validate that the array is sorted
            if (!isSorted(arrayToSort)) {
                System.err.println(sortName + " failed to sort the array correctly on iteration " + (i + 1));
                break;
            }
        }

        // Calculate and return the average time
        return totalTime / iterations;
    }

    private static int[] readData(String fileName) {
        String currentDir = System.getProperty("user.dir");
        String fullPath = currentDir + "\\" + fileName;
        System.out.println("Tentando ler o arquivo: " + fullPath);

        try (BufferedReader br = new BufferedReader(new FileReader(fullPath))) {
            // Ignorar a primeira linha (cabeçalho)
            String header = br.readLine();
            System.out.println("Cabeçalho ignorado: " + header);

            return br.lines()
                     .flatMap(line -> Arrays.stream(line.split(","))) // Ajuste conforme o delimitador
                     .mapToInt(Integer::parseInt)
                     .toArray();
        } catch (IOException | NumberFormatException e) {
            System.err.println("Erro ao ler o arquivo: " + fileName);
            e.printStackTrace();
            return new int[0];
        }
    }

    // Implementação do Bubble Sort
    private static void bubbleSort(int[] array) {
        int n = array.length;
        boolean swapped;
        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (array[j] > array[j + 1]) {
                    // Troca
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
    }

    private static void insertionSort(int[] array) {
        for (int i = 1; i < array.length; i++) {
            int key = array[i];
            int j = i - 1;
            while (j >= 0 && array[j] > key) {
                array[j + 1] = array[j];
                j--;
            }
            array[j + 1] = key;
        }
    }

    private static void quickSort(int[] array, int low, int high) {
        if (low < high) {
            int pi = randomizedPartition(array, low, high);
            quickSort(array, low, pi - 1);
            quickSort(array, pi + 1, high);
        }
    }

    private static int randomizedPartition(int[] array, int low, int high) {
        // Escolher um pivot aleatório entre low e high
        int pivotIndex = low + (int)(Math.random() * (high - low + 1));
        swap(array, pivotIndex, high); // Mover o pivot para o final
        return partition(array, low, high);
    }

    private static int partition(int[] array, int low, int high) {
        int pivot = array[high];
        int i = (low - 1); // Índice do menor elemento
        for (int j = low; j < high; j++) {
            // Se o elemento atual for menor ou igual ao pivot
            if (array[j] <= pivot) {
                i++;
                // Troca array[i] e array[j]
                int temp = array[i];
                array[i] = array[j];
                array[j] = temp;
            }
        }
        // Troca array[i+1] e array[high] (ou pivot)
        int temp = array[i + 1];
        array[i + 1] = array[high];
        array[high] = temp;
        return i + 1;
    }
    private static void swap(int[] array, int i, int j) {
        if (i == j) return; // Evitar troca se os índices forem iguais
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    private static boolean isSorted(int[] array) {
        for (int i = 1; i < array.length; i++) {
            if (array[i - 1] > array[i]) {
                return false;
            }
        }
        return true;
    }

    @FunctionalInterface
    interface SortFunction {
        void sort(int[] array);
    }
}














