/*
Д/з по теме "Многопоточность"
Обработка длинного массива обычным способом и с помощью 2-х параллельных нитей (потоков)

@author Grishin Dmitriy
@version dated 12.01.18, upgrade 29.06.18
@link null
 */


public class J2HW5Threads {

    public static void main(String[] args) {
        timeWorking1();
        timeWorking2();

    }
    //метод без разбивки массива
    public static void timeWorking1() {
        final int SIZE = 10000000;
        float[] arr = new float[SIZE];
        for (int i = 0; i < SIZE; i++) arr[i] = 1; //заполняю массив

        long a = System.currentTimeMillis();//засекаю начальное время [мс.]
        for (int i = 0; i < SIZE; i++)
            arr[i] = (float)(arr[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
        System.out.println("Время работы метода без разбивки массива: " + (System.currentTimeMillis() - a));
    }

    //метод с разбивкой массива
    public static void timeWorking2() {
        final int SIZE = 10000000;
        final int HALF = SIZE / 2;
        float[] arr = new float[SIZE]; //основной массив
        float[] a1 = new float[HALF]; //1-й вспомогательный массив
        float[] a2 = new float[HALF]; //2-й вспомогательный массив
        MyThread1 forA1;
        MyThread1 forA2;

        for (int i = 0; i < SIZE; i++) arr[i] = 1; //заполняю основной массив

        long a = System.currentTimeMillis();//засекаю начальное время [мс.]
        //делю массив на 2 массива:
        System.arraycopy(arr, 0, a1, 0, HALF);//1-й вспомогательный массив
        System.arraycopy(arr, HALF, a2, 0, HALF);//2-й вспомогательный массив
        //создаю нити/потоки для обработки
        forA1 = new MyThread1("thread1", a1, 0);
        forA2 = new MyThread1("thread2", a2, HALF);//с учётом первоначального индекса из массива источника
        //стартую параллельную обработку
        forA1.start();
        forA2.start();

        //ожидаю окончания выполнения дочерних потоков методом join
        try{
            forA1.join();
            forA2.join();
        } catch (InterruptedException ex) {
            System.out.println("Thread is interrupted.");
        }

        //продолжаю выполнение основного потока
        a1 = forA1.arrayToCount;
        a2 = forA2.arrayToCount;
        //склеиваю массив
        System.arraycopy(a1, 0, arr, 0, HALF);
        System.arraycopy(a2, 0, arr, HALF, HALF);

        System.out.println("Время работы метода c разбивкой массива: " + (System.currentTimeMillis() - a));

    }
}

//Класс для обработки массива в потоке/нити
class MyThread1 extends Thread {
    float[] arrayToCount;//поле - массив для вычислений
    int position;

    //Конструктор потока
    public MyThread1(String name, float[] arrayToCount, int position){
        super(name);//имя нити
        this.arrayToCount = arrayToCount;
        this.position = position;
    }

    //метод обработки массива:
    @Override
    public void run() {
        for (int i = 0; i < arrayToCount.length; i++)
            arrayToCount[i] = (float)(arrayToCount[i] * Math.sin(0.2f + (i + position) / 5) * Math.cos(0.2f + (i + position) / 5) * Math.cos(0.4f + (i + position) / 2));
    }
}
