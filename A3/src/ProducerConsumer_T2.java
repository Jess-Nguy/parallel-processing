
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileFilter;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ProducerConsumer
 * <p/>
 * Producer and consumer tasks in a desktop search application
 *
 * @author Brian Goetz and Tim Peierls
 * Edits by Jess Nguyen 000747411
 */
public class ProducerConsumer_T2 {


    /**
     * Producer for crawling through files
     */
    static class FileCrawler implements Runnable {
        private final BlockingQueue<File> fileQueue;
        private final FileFilter fileFilter;
        private final File root;

        public FileCrawler(BlockingQueue<File> fileQueue,
                           final FileFilter fileFilter,
                           File root) {
            this.fileQueue = fileQueue;
            this.root = root;
            this.fileFilter = new FileFilter() {
                public boolean accept(File f) {
                    return f.isDirectory() || fileFilter.accept(f);
                }
            };
        }

        /**
         * Checks if the file has already been added into fileIndexed
         * @param f File that is being crawled
         * @return boolean of true or false
         */
        private boolean alreadyIndexed(File f) {
            // check if file was added to fileIndexed
            if(fileIndexed.contains(f)){
                return true;
            }else{
                return false;
            }
        }

        public void run() {
            try {
                crawl(root);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }finally {
                // submit poison objects use File (String) Constructor
                for( int i = 0; i<= N_CONSUMERS; i++) {
                    // When last loop, send that it's the last file for count display. Else poison file.
                    if(i + 1 == N_CONSUMERS){
                        try {
                            Thread.sleep(50);
                            fileQueue.put(finalFile);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }else if (i < N_CONSUMERS){
                        try {
                            Thread.sleep(50);
                            fileQueue.put(poisonedFile);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        private void crawl(File root) throws InterruptedException {
            File[] entries = root.listFiles(fileFilter);

            if (entries != null) {
                for (File entry : entries) {
                    if (entry.isDirectory()) {
                        crawl(entry);
                    }
                    else if (!alreadyIndexed(entry)) {
                        fileQueue.put(entry);
                    }
                }
            }
        }
    }

    /**
     * Consumer class for indexing files
     */
    static class Indexer implements Runnable {
        private final BlockingQueue<File> queue;

        private static AtomicInteger count = new AtomicInteger(0);
        public Indexer(BlockingQueue<File> queue) {
            this.queue = queue;
        }

        public void run() {
            try {
                while (true) {

                    File pathString = queue.take();
                    indexFile(pathString);
                    // check for poison object - return from run method, which will shut down this thread
                    if (pathString.equals(poisonedFile)) {
                        break;
                    }else if(pathString.equals(finalFile)){
                        System.out.println("TOTAL COUNT: " + count.get());
                        count.set(0);
                        break;
                    }

                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        public void indexFile(File file) {
            if(!file.isDirectory() && !file.equals(poisonedFile) && !file.equals(finalFile)) {
                count.getAndIncrement();
                // Index the file...
                try {
                    fileIndexed.put(file.getAbsoluteFile());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        System.out.println(fileIndexed.take().getAbsoluteFile());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    private static final int BOUND = 10;  // Set blocked queue size.
    private static final int N_CONSUMERS = Runtime.getRuntime().availableProcessors(); // Set number of object create by the number of available processors
    private static File poisonedFile = new File("POISON"); // A poison file for shutting down.
    private static File finalFile = new File("FINAL"); // A final file for displaying total count.
    private static LinkedBlockingQueue<File> fileIndexed = new LinkedBlockingQueue(); // Linked queue for the index files.

    /**
     * Start indexing the files that are being searched
     */
    public static void startIndexing() {
        BlockingQueue<File> queue = new LinkedBlockingQueue<File>(BOUND);


        // Create thread pool from the executor service
        ExecutorService pool = Executors.newFixedThreadPool(N_CONSUMERS);
        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        System.out.print("Press \"c\" to cancel.\n");

        while(true) {
            try {
                Thread.sleep(1200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally{
                System.out.print("Enter directory: ");
                String filePath = myObj.next();

                // Exit key
                if(filePath.equals("c")){
                    break;
                }
                File[] testFiles = { new File(filePath)};

                System.out.print("Type in search: ");
                String search = myObj.next().toLowerCase();

                /**
                 * Accept files that match search or if `.*\.` for all files of certain extension.
                 */
                FileFilter filter = new FileFilter() {

                    public boolean accept(File file) {
                        if (search.contains(".*\\.")) {
                            if(file.getName().toLowerCase().endsWith(search.substring(search.indexOf("\\") + 1))) {
                                return true;
                            }else{
                                return false;
                            }
                        }else if(file.getName().toLowerCase().matches(search)){
                            return true;
                        }else{
                            return false;
                        }
                    }
                };

                for (File root : testFiles) { // test different ones - C:\test10183 C:\test10183\Canada .*\.txt Hamilton.txt
                    pool.submit(new FileCrawler(queue, filter, root));
                }
                for (int i = 0; i < N_CONSUMERS; i++) {
                    pool.submit(new Indexer(queue));
                }
            }
        }
        // shutdown
        pool.shutdown();

    }
}

