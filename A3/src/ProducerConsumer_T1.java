
import java.io.File;
import java.io.FileFilter;
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
public class ProducerConsumer_T1 {



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
         * Would check if the file is already indexed
         * @param f File that is gettign crawled
         * @return true or false
         */
        private boolean alreadyIndexed(File f) {
            return false;
        }

        public void run() {
            try {
                crawl(root);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }finally{
                // submit poison objects use File (String) Constructor
                for( int i = 0; i<= N_CONSUMERS; i++) {
                    // When last loop, send that it's the last file for count display. Else poison file.
                    if(i + 1 == N_CONSUMERS){
                        fileQueue.add(finalFile);
                    }else if (i < N_CONSUMERS){
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }finally{
                            fileQueue.add(poisonedFile);

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
                        break;
                    }

                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        public void indexFile(File file) {
            if(!file.isDirectory() ){
                if(!file.equals(poisonedFile)) {
                    if(!file.equals(finalFile)){
                        count.getAndIncrement();
                    }
                }
            }
            // Index the file...

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
    public static void startIndexing(File[] roots) {
        BlockingQueue<File> queue = new LinkedBlockingQueue<File>(BOUND);
        FileFilter filter = new FileFilter() {
            public boolean accept(File file) {
                return true;
            }
        };

        for (File root : roots) {
            new Thread(new FileCrawler(queue, filter, root)).start();
        }

        for (int i = 0; i < N_CONSUMERS; i++) {
            new Thread(new Indexer(queue)).start();
        }
    }
}
