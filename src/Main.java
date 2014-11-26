import java.io.IOException;

class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        SVD svd = new SVD();
        svd.learn();
        svd.check();
    }
}
