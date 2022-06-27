import java.io. *;
import java.util. *;

public class SortingTest {
    public static void main(String args[]) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System. in));

        try {
            boolean isRandom = false; // 입력받은 배열이 난수인가 아닌가?
            int[] value; // 입력 받을 숫자들의 배열
            String nums = br.readLine(); // 첫 줄을 입력 받음
            if (nums.charAt(0) == 'r') {
                // 난수일 경우
                isRandom = true; // 난수임을 표시

                String[] nums_arg = nums.split(" ");

                int numsize = Integer.parseInt(nums_arg[1]); // 총 갯수
                int rminimum = Integer.parseInt(nums_arg[2]); // 최소값
                int rmaximum = Integer.parseInt(nums_arg[3]); // 최대값

                Random rand = new Random(); // 난수 인스턴스를 생성한다.

                value = new int[numsize]; // 배열을 생성한다.
                for (int i = 0; i < value.length; i++) // 각각의 배열에 난수를 생성하여 대입
                    value[i] = rand.nextInt(rmaximum - rminimum + 1) + rminimum; // rmin ~ rmax사이 난수
                }
            else {
                // 난수가 아닐 경우
                int numsize = Integer.parseInt(nums);

                value = new int[numsize]; // 배열을 생성한다.
                for (int i = 0; i < value.length; i++) // 한줄씩 입력받아 배열원소로 대입
                    value[i] = Integer.parseInt(br.readLine());
                }
            
            // 숫자 입력을 다 받았으므로 정렬 방법을 받아 그에 맞는 정렬을 수행한다.
            while (true) {
                int[] newvalue = (int[])value.clone(); // 원래 값의 보호를 위해 복사본을 생성한다.

                String command = br.readLine();

                long t = System.currentTimeMillis();
                switch (command.charAt(0)) {
                    case 'B': // Bubble Sort
                        newvalue = DoBubbleSort(newvalue);
                        break;
                    case 'I': // Insertion Sort
                        newvalue = DoInsertionSort(newvalue);
                        break;
                    case 'H': // Heap Sort
                        newvalue = DoHeapSort(newvalue);
                        break;
                    case 'M': // Merge Sort

                        newvalue = DoMergeSort(newvalue);
                        break;
                    case 'Q': // Quick Sort
                        newvalue = DoQuickSort(newvalue, 0, newvalue.length - 1);
                        break;
                    case 'R': // Radix Sort
                        newvalue = DoRadixSort(newvalue);
                        break;
                    case 'X':
                        return; // 프로그램을 종료한다.
                    default:
                        throw new IOException("잘못된 정렬 방법을 입력했습니다.");
                }
                if (isRandom) {
                    // 난수일 경우 수행시간을 출력한다.
                    System
                        .out
                        .println((System.currentTimeMillis() - t) + " ms");
                } else {
                    // 난수가 아닐 경우 정렬된 결과값을 출력한다.
                    for (int i = 0; i < newvalue.length; i++) {
                        System
                            .out
                            .println(newvalue[i]);
                    }
                }

            }
        } catch (IOException e) {
            System
                .out
                .println("입력이 잘못되었습니다. 오류 : " + e.toString());
        }
    }

    // ///////////////////////////////////////// /
    private static int[] DoBubbleSort(int[] value) {
        // TODO : Bubble Sort 를 구현하라. value는 정렬안된 숫자들의 배열이며 value.length 는 배열의 크기가 된다.
        // 결과로 정렬된 배열은 리턴해 주어야 하며, 두가지 방법이 있으므로 잘 생각해서 사용할것. 주어진 value 배열에서 안의 값만을 바꾸고
        // value를 다시 리턴하거나 같은 크기의 새로운 배열을 만들어 그 배열을 리턴할 수도 있다.
        for (int last = value.length - 1; last >= 0; last--) {
            for (int i = 0; i < last; i++) {
                if (value[i] > value[i + 1]) {
                    int tmp = value[i + 1];
                    value[i + 1] = value[i];
                    value[i] = tmp;
                } else {
                    continue;
                }
            }
        }

        return (value);
    }

    // ///////////////////////////////////////// /
    private static int[] DoInsertionSort(int[] value) {
        // TODO : Insertion Sort 를 구현하라.

        for (int last = 1; last <= value.length - 1; last++) {
            int j = last - 1;
            int tmp = value[last];

            while (tmp < value[j]) {
                value[j + 1] = value[j];
                j--;
                if (j < 0) {
                    break;
                }
            }
            value[j + 1] = tmp;
        }
        return (value);
    }

    // ///////////////////////////////////////// /
    private static int[] DoHeapSort(int[] value) {
        // TODO : Heap Sort 를 구현하라.

        buildHeap(value);

        for (int last = value.length - 1; last > 0; last--) {

            int tmp = value[last];
            value[last] = value[0];
            value[0] = tmp;
            percolateDown(value, 0, last - 1);
        }

        return (value);
    }
    private static void buildHeap(int[] array) {
        int n = (array.length - 1) / 2;
        while (n >= 0) {
            percolateDown(array, n--, array.length - 1);
        }
    }

    private static void percolateDown(int[] array, int n, int last) {

        int target = n;
        int rightChild = target * 2 + 2;
        int child = target * 2 + 1;

        while (child <= last) {
            if (rightChild <= last && array[child] < array[rightChild]) {
                child = rightChild;
            }
            if (array[child] > array[target]) {
                int tmp = array[target];
                array[target] = array[child];
                array[child] = tmp;

                target = child;
                rightChild = target * 2 + 2;
                child = target * 2 + 1;
                continue;
            }
            break;
        }
    }

    // ///////////////////////////////////////// /
    private static int[] DoMergeSort(int[] value) {
        // TODO : Merge Sort 를 구현하라.
        int[] value2 = new int[value.length];
        int idx = 0;
        for (int v : value) {
            value2[idx++] = v;
        }

        msort(value, value2, 0, value.length - 1);
        return (value);
    }
    private static void msort(int[] value1, int[] value2, int p, int q) {
        if (p < q) {

            int r = (p + q) / 2;

            msort(value2, value1, p, r);
            msort(value1, value2, r + 1, q);
            DoMerge(value1, value2, p, r, q);
        }
    }

    private static void DoMerge(int[] value1, int[] value2, int p, int r, int q) {
        int t = p;
        int i = p;
        int j = r + 1;
        while (i <= r && j <= q) {
            if (value2[i] > value1[j]) {
                value1[t++] = value1[j++];
            } else {
                value1[t++] = value2[i++];
            }
        }
        while (i <= r) {
            value1[t++] = value2[i];
            i++;
        }
        while (j <= q) {
            value1[t++] = value1[j];
            j++;
        }
    }

    // ///////////////////////////////////////// /
    private static int[] DoQuickSort(int[] value, int p, int q) {
        // TODO : Quick Sort 를 구현하라.

        if (p < q) {
            int random_idx = (int)(Math.random() * (q - p + 1)) + p;
            int criterion = value[random_idx];
            value[random_idx] = value[q];
            value[q] = criterion;

            int i = p - 1;
            int j = p;
            while (j <= q - 1) {
                if (value[j] < criterion) {
                    int tmp = value[j];
                    value[j] = value[i + 1];
                    value[i + 1] = tmp;
                    i++;
                    j++;
                } else if (value[j] > criterion) {
                    j++;
                } else if (value[j] == criterion) {
                    if (Math.random() < 0.5) {
                        int tmp = value[j];
                        value[j] = value[i + 1];
                        value[i + 1] = tmp;
                        i++;
                    }

                    j++;

                }
            }
            //swapping criterion and 2nd area's ele.
            value[q] = value[i + 1];
            value[i + 1] = criterion;

            DoQuickSort(value, p, i);
            DoQuickSort(value, i + 2, q);
        }

        return (value);
    }

    // ///////////////////////////////////////// /
    private static int[] DoRadixSort(int[] value) {
        // TODO : Radix Sort 를 구현하라.

        int[] cnt_pos = new int[10];
        int[] cnt_neg = new int[10];
        int[] cum_pos = new int[10];
        int[] cum_neg = new int[10];
        int[] tmp = new int[value.length];
        int max = -1;

        for (int i = 0; i <= value.length - 1; i++) {
            if (Math.abs(value[i]) > max) {
                max = Math.abs(value[i]);
            }
        }
        int max_digit = (int)Math.log10(max) + 1;
        for (int digit = 1; digit <= max_digit; digit++) {
            for (int i = 0; i <= 9; i++) {
                cnt_pos[i] = 0;
                cnt_neg[i] = 0;
            }
            for (int j = 0; j <= value.length - 1; j++) {
                if (value[j] >= 0) {
                    cnt_pos[(int)(value[j] / Math.pow(10, digit - 1)) % 10]++;
                } else {
                    cnt_neg[(int)(-value[j] / Math.pow(10, digit - 1)) % 10]++;
                }
            }

            cum_neg[9] = 0;

            for (int k = 8; k >= 0; k--) {
                cum_neg[k] = cum_neg[k + 1] + cnt_neg[k + 1]; //cum_neg[0] is the last one in negative world
            }

            cum_pos[0] = cum_neg[0] + cnt_neg[0];

            for (int k = 1; k <= 9; k++) {
                cum_pos[k] = cum_pos[k - 1] + cnt_pos[k - 1];
            }
            for (int i = 0; i <= value.length - 1; i++) {
                if (value[i] >= 0) {
                    tmp[cum_pos[(int)(value[i] / Math.pow(10, digit - 1)) % 10]++] = value[i];
                } else {
                    tmp[cum_neg[(int)(-value[i] / Math.pow(10, digit - 1)) % 10]++] = value[i];
                }
            }
            for (int i = 0; i <= value.length - 1; i++) {
                value[i] = tmp[i];
            }
        }
        return (value);
    }

}