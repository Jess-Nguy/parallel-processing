import java.io.File;
/**
 * created by Jess Nguyen 000747411
 */
public class Main_T1 {
    private static ProducerConsumer_T1 producerConsumer = new ProducerConsumer_T1();


 public static void main(String[] args) {

         File[] testFile = { new File("C:/test10183")};

         producerConsumer.startIndexing(testFile);

     }

}
